package com.example.a303com_laukuansin.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.activities.ExerciseActivity;
import com.example.a303com_laukuansin.activities.MealActivity;
import com.example.a303com_laukuansin.activities.WaterActivity;
import com.example.a303com_laukuansin.adapters.MealAdapter;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.domains.MealType;
import com.example.a303com_laukuansin.domains.User;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;
import com.example.a303com_laukuansin.utilities.ProgressAnimation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.model.CalendarItemStyle;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarPredicate;

public class HomeFragment extends BaseFragment{
    private final User user;
    private TextView _dateView;
    private SimpleDateFormat dateFormat;
    private RecyclerView _mealRecyclerView;
    private TextView _dailyCaloriesEatenView, _dailyCaloriesBurntView, _dailyStepView, _dailyWaterView;
    private LinearProgressIndicator _dailyCaloriesEatenProgress, _dailyCaloriesBurntProgress,_dailyStepProgress,_dailyWaterProgress;
    private RetrieveDailyData _retrieveData = null;
    private FirebaseFirestore database;
    private int stepWalked = 0,glassOfWaterDrink = 0;
    private String realDate;
    private SyncDailyStepData _syncStepData = null;

    public HomeFragment() {
        user = getSessionHandler().getUser();
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initialization(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();//load data
    }

    private void initialization(View view) {
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), R.color.white));// set status background white
        }
        //set date format
        dateFormat = new SimpleDateFormat("dd MMM yyyy");

        //bind view with id
        LinearLayout _containerLayout = view.findViewById(R.id.containerLayout);
        LinearLayout _calendarContainer = view.findViewById(R.id.containerCalendar);
        MaterialCardView _mealCardView = view.findViewById(R.id.mealCardView);
        MaterialCardView _exerciseCardView = view.findViewById(R.id.exerciseCardView);
        MaterialCardView _waterCardView = view.findViewById(R.id.waterCardView);
        TextView _viewMoreMealButton = view.findViewById(R.id.viewMoreMealButton);
        _dateView = view.findViewById(R.id.date_view);
        ImageView arrow = view.findViewById(R.id.arrowView);
        LinearLayout datePickerButton = view.findViewById(R.id.datePickerLayout);
        TextView _welcomeView = view.findViewById(R.id.welcomeText);
        _dailyCaloriesEatenView = view.findViewById(R.id.dailyCaloriesEaten);
        _dailyCaloriesEatenProgress = view.findViewById(R.id.dailyCaloriesEatenProgress);
        _dailyCaloriesBurntView = view.findViewById(R.id.dailyCaloriesBurnt);
        _dailyCaloriesBurntProgress = view.findViewById(R.id.dailyCaloriesBurntProgress);
        _dailyStepView = view.findViewById(R.id.dailyStep);
        _dailyStepProgress = view.findViewById(R.id.dailyStepProgress);
        _dailyWaterView = view.findViewById(R.id.dailyWater);
        _dailyWaterProgress = view.findViewById(R.id.dailyWaterProgress);
        TextView _goalView = view.findViewById(R.id.goal);
        _mealRecyclerView = view.findViewById(R.id.mealRecyclerView);
        _mealRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        _mealRecyclerView.setItemAnimator(new DefaultItemAnimator());
        ImageButton _syncStepButton = view.findViewById(R.id.syncStepButton);

        //setup calendar
        setupCalendar(view);
        //set today date
        setDate(Calendar.getInstance());
        //setup database
        database = FirebaseFirestore.getInstance();
        //set target goal
        _goalView.setText(String.format("Target goal: %1$s",user.getTargetGoal()));
        //click view more meal
        _viewMoreMealButton.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition(_containerLayout, new AutoTransition());//delay the transition
            if (_mealRecyclerView.getVisibility() == View.VISIBLE) {//if recycler view is shown
                _viewMoreMealButton.setText("more");//change text to more
                _mealRecyclerView.setVisibility(View.GONE);//hide the recycler view
            } else {//if recycler view is no shown
                _viewMoreMealButton.setText("less");//change text to less
                _mealRecyclerView.setVisibility(View.VISIBLE);//shown the recycler view
            }
        });

        //click date picker
        datePickerButton.setOnClickListener(v -> {
            //rotate the arrow when the calendar is open or close
            float rotation = _calendarContainer.getVisibility() == View.VISIBLE ? 0 : 180;
            ViewCompat.animate(arrow).rotation(rotation).start();
            if (_calendarContainer.getVisibility() == View.VISIBLE)//if calendar is open
            {
                _calendarContainer.setVisibility(View.GONE);
            } else {//if calendar is close
                _calendarContainer.setVisibility(View.VISIBLE);
            }
        });

        //when click the meal card view
        _mealCardView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getContext(), MealActivity.class);
                intent.putExtra(MealActivity.DATE_KEY,_dateView.getText().toString());
                startActivity(intent);
                //add animation sliding to next activity
                getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

            }
        });

        //when click exercise card view
        _exerciseCardView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getContext(), ExerciseActivity.class);
                intent.putExtra(ExerciseActivity.DATE_KEY,_dateView.getText().toString());
                startActivity(intent);
                //add animation sliding to next activity
                getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

        //when sync button clicked
        _syncStepButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                syncStepData(_syncStepButton);
            }
        });

        //when click water card view
        _waterCardView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getContext(), WaterActivity.class);
                intent.putExtra(WaterActivity.DATE_KEY,_dateView.getText().toString());
                startActivity(intent);
                //add animation sliding to next activity
                getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

        //set first name of user
        _welcomeView.setText(String.format("Hi %1$s!", user.getName().split(" ")[0]));
    }

    private void loadData() {
        if (_retrieveData == null)//if retrieve data class is null, by default will null
        {
            _retrieveData = new RetrieveDailyData();
            _retrieveData.execute();
        }
    }

    private void setDate(Calendar calendarDate) {
        //change the calendar date to string with date format
        String date = dateFormat.format(calendarDate.getTime());
        //get the current date and change to string with date format
        String currDate = dateFormat.format(Calendar.getInstance().getTime());
        //if the date is same as current date
        if (date.equals(currDate)) {
            _dateView.setText("Today");
        } else {
            _dateView.setText(date);
        }
        realDate = date;
    }

    private void setupCalendar(View view) {
        //set start date from 10 years before
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.YEAR, -10);
        //range 10 years before from now until now
        //date number on screen 5
        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(view, R.id.calendarView).range(startDate, Calendar.getInstance()).datesNumberOnScreen(5).disableDates(new HorizontalCalendarPredicate() {
            @Override
            public boolean test(Calendar date) {
                return false;
            }

            @Override
            public CalendarItemStyle style() {//change style for when the date is disable
                return new CalendarItemStyle(getResources().getColor(R.color.grey_300), getResources().getColor(R.color.grey_300), getResources().getColor(R.color.grey_300), null);
            }
        }).build();
        //when calendar is scroll or click
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                setDate(date);//set date
                loadData();//load data
            }
        });
    }

    private void syncStepData(ImageButton imageButton)
    {
        if(_syncStepData==null)
        {
            _syncStepData = new SyncDailyStepData(imageButton);
            _syncStepData.execute();
        }
    }

    //to sync the step data
    private class SyncDailyStepData extends AsyncTask<Void,Void,Void>
    {
        private ImageButton _syncStepButton;

        public SyncDailyStepData(ImageButton _syncStepButton) {
            this._syncStepButton = _syncStepButton;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //rotation animation
            getActivity().runOnUiThread(() -> {
                Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_animation);
                _syncStepButton.startAnimation(animation);
                _syncStepButton.setEnabled(false);
            });
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getActivity().runOnUiThread(() -> {
                //load step data
                loadStepAndExerciseData(false);
                //delay 2s
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    _syncStepButton.clearAnimation();
                    _syncStepButton.setEnabled(true);

                    Toast.makeText(getContext(), "Synchronized the step data", Toast.LENGTH_SHORT).show();
               },2000);
                
            });
            _syncStepData = null;
            return null;
        }
    }

    private class RetrieveDailyData extends AsyncTask<Void, Void, Void> {

        public RetrieveDailyData() {
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getActivity().runOnUiThread(() -> {
                //load meal data
                loadMealData();
                //load step data
                loadStepAndExerciseData(true);
                //load water data
                loadWaterData();
                _retrieveData = null;
            });
            return null;
        }
    }

    private void loadMealData()
    {
        //meal collection path
        String MEAL_COLLECTION_PATH = String.format("MealRecords/%1$s/%2$s", user.getUID(),realDate);

        //get meal collection reference
        CollectionReference mealCollectionRef = database.collection(MEAL_COLLECTION_PATH);
        mealCollectionRef.addSnapshotListener((value, error) -> {
            if(error!=null)//if appear error
            {
                ErrorAlert(error.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(),true).show();
                return;
            }

            double totalCalories=0,breakfastCalories=0,lunchCalories=0,dinnerCalories=0,snackCalories=0;
            List<MealType> _mealTypeList = new ArrayList<>();

            for(DocumentSnapshot document:value.getDocuments())
            {
                Map<String, Object> documentMapData = document.getData();
                String mealType = documentMapData.get("mealType").toString();
                double calories = (double) documentMapData.get("calories");
                //switch case see the meal type
                switch (mealType) {
                    case "Breakfast": {
                        breakfastCalories += calories;
                        break;
                    }
                    case "Lunch": {
                        lunchCalories += calories;
                        break;
                    }
                    case "Dinner": {
                        dinnerCalories += calories;
                        break;
                    }
                    case "Snack": {
                        snackCalories += calories;
                        break;
                    }
                    default: {
                        Log.d("Error:", "Unknown class");
                        break;
                    }
                }
            }
            //get total calories
            totalCalories = breakfastCalories+lunchCalories+dinnerCalories+snackCalories;

            //set text for daily calories eaten
            _dailyCaloriesEatenView.setText(String.format("%1$d of %2$d Calories Eaten",(int)Math.round(totalCalories), (int)Math.round(user.getDailyCaloriesEaten())));
            //setup progress bar and animation for meal
            setupProgressAndAnimation(_dailyCaloriesEatenProgress,(int)Math.round(totalCalories),(int)Math.round(user.getDailyCaloriesEaten()));

            //add different meal type to the meal list
            addMeal(_mealTypeList, "Breakfast", user.getSuggestBreakfastCalorieEaten(), breakfastCalories);
            addMeal(_mealTypeList, "Lunch", user.getSuggestLunchCalorieEaten(), lunchCalories);
            addMeal(_mealTypeList, "Dinner", user.getSuggestDinnerCalorieEaten(), dinnerCalories);
            addMeal(_mealTypeList, "Snack", user.getSuggestSnackCalorieEaten(), snackCalories);

            //create meal adapter
            MealAdapter _mealAdapter = new MealAdapter(getContext(), _mealTypeList);
            //set adapter for meal recyclerview
            _mealRecyclerView.setAdapter(_mealAdapter);
        });
    }

    private void loadExerciseData()
    {
        //exercise collection path
        String EXERCISE_COLLECTION_PATH = String.format("ExerciseRecords/%1$s/%2$s", user.getUID(),realDate);

        //get exercise collection reference
        CollectionReference exerciseCollectionRef = database.collection(EXERCISE_COLLECTION_PATH);
        exerciseCollectionRef.addSnapshotListener((value, error) -> {
            if(error!=null)//if appear error
            {
                ErrorAlert(error.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(),true).show();
                return;
            }
            double totalCalories = 0;
            for(DocumentSnapshot document:value.getDocuments())
            {
                Map<String, Object> documentMapData = document.getData();
                Long duration = (Long) documentMapData.get("duration");
                double caloriesBurnedPerKGPerMin = (double) documentMapData.get("caloriesPerKGPerMin");
                double calories = (caloriesBurnedPerKGPerMin*duration*user.getWeight());
                totalCalories+=calories;
            }
            //total calories add up with the calories of step walked
            totalCalories += stepWalked*user.getCaloriesBurnedPerStepWalked();

            //set text for daily calories burned
            _dailyCaloriesBurntView.setText(String.format("%1$d of %2$d Calories Burnt",(int)Math.round(totalCalories),(int)Math.round(user.getDailyCaloriesBurnt())));
            //setup progress bar and animation for exercise
            setupProgressAndAnimation(_dailyCaloriesBurntProgress,(int)Math.round(totalCalories),(int)Math.round(user.getDailyCaloriesBurnt()));

        });
    }

    private void loadStepAndExerciseData(boolean showDialog)
    {
        SweetAlertDialog _progressDialog = showProgressDialog("Loading...",getResources().getColor(R.color.colorPrimary));

        if(showDialog)
        {
            _progressDialog.show();
        }

        //initialize the step count
        stepWalked = 0;
        //step collection path
        String STEP_COLLECTION_PATH = String.format("StepRecords/%1$s/%2$s", user.getUID(),realDate);
        //get step collection reference
        CollectionReference stepCollectionRef = database.collection(STEP_COLLECTION_PATH);
        stepCollectionRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if(showDialog)
            {
                if(_progressDialog.isShowing())
                    _progressDialog.dismiss();
            }
            //if has record
            if(!queryDocumentSnapshots.isEmpty())
            {
                stepWalked = queryDocumentSnapshots.getDocuments().get(0).getLong("stepCount").intValue();
            }
            //set text for daily step walked
            _dailyStepView.setText(String.format("%1$d of %2$d Steps Walked",stepWalked,user.getSuggestStepWalk()));
            //setup progress bar and animation for step
            setupProgressAndAnimation(_dailyStepProgress,stepWalked,user.getSuggestStepWalk());

            //load exercise data
            loadExerciseData();
        }).addOnFailureListener(e -> ErrorAlert(e.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(),true).show());

    }

    private void loadWaterData()
    {
        //initialize water drink to 0
        glassOfWaterDrink = 0;
        //water collection path
        String WATER_COLLECTION_PATH = String.format("WaterRecords/%1$s/%2$s", user.getUID(),realDate);
        //get water collection reference
        CollectionReference waterCollectionRef = database.collection(WATER_COLLECTION_PATH);
        waterCollectionRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if(!queryDocumentSnapshots.isEmpty())
            {
                glassOfWaterDrink = queryDocumentSnapshots.getDocuments().get(0).getLong("glassOfWater").intValue();
            }
            //set text for daily water consumed
            _dailyWaterView.setText(String.format("%1$d of %2$d Glasses water consumed", glassOfWaterDrink, user.getSuggestWaterIntakeInGlass()));

            //setup progress bar and animation for water
            setupProgressAndAnimation(_dailyWaterProgress, glassOfWaterDrink, user.getSuggestWaterIntakeInGlass());

        }).addOnFailureListener(e -> ErrorAlert(e.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(),true).show());

    }

    private void addMeal(List<MealType> _mealTypeList, String mealType, double suggestCalories, double currentCalories) {
        //create meal
        MealType meal = new MealType(mealType);
        meal.setSuggestCalorie(suggestCalories);
        meal.setCurrentCalorie(currentCalories);
        //add meal in meal list
        _mealTypeList.add(meal);
    }

    private void setupProgressAndAnimation(LinearProgressIndicator _progressBar,int currentValue,int targetValue)
    {
        //set target value as maximum
        _progressBar.setMax(targetValue);
        //clear the animation before start animation
        _progressBar.clearAnimation();

        //create animation, from 0 animate to current value
        ProgressAnimation animation = new ProgressAnimation(_progressBar,0,currentValue);
        animation.setDuration(1000);//set 2 milliseconds animation
        _progressBar.setAnimation(animation);//start animation
    }

}
