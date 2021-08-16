package com.example.a303com_laukuansin.fragments;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.adapters.MealAdapter;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.domains.Meal;
import com.example.a303com_laukuansin.domains.User;
import com.example.a303com_laukuansin.utilities.ProgressAnimation;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.model.CalendarItemStyle;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarPredicate;

public class HomeFragment extends BaseFragment {
    private final User user;
    private TextView dateView;
    private SimpleDateFormat dateFormat;
    private RecyclerView _mealRecyclerView;
    private TextView _dailyCaloriesEatenView, _dailyCaloriesBurntView, _dailyStepView, _dailyWaterView,_goalView;
    private LinearProgressIndicator _dailyCaloriesEatenProgress, _dailyCaloriesBurntProgress,_dailyStepProgress,_dailyWaterProgress;
    private RetrieveDailyData _retrieveData = null;

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
        loadData(user);//load data

        return view;
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
        TextView _viewMoreMealButton = view.findViewById(R.id.viewMoreMealButton);
        dateView = view.findViewById(R.id.date_view);
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
        _goalView = view.findViewById(R.id.goal);
        _mealRecyclerView = view.findViewById(R.id.mealRecyclerView);
        _mealRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        _mealRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //setup calendar
        setupCalendar(view);
        //set today date
        setDate(Calendar.getInstance());

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

        //set first name of user
        _welcomeView.setText(String.format("Hi %1$s!", user.getName().split(" ")[0]));
    }

    private void loadData(User user) {
        if (_retrieveData == null)//if retrieve data class is null, by default will null
        {
            _retrieveData = new RetrieveDailyData(user);
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
            dateView.setText("Today");
        } else {
            dateView.setText(date);
        }
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
                loadData(user);
                //load data
            }
        });
    }

    private class RetrieveDailyData extends AsyncTask<Void, Void, Void> {
        private User user;
        private List<Meal> _mealList;

        public RetrieveDailyData(User user) {
            this.user = user;
            _mealList = new ArrayList<>();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getActivity().runOnUiThread(() -> {
                //set text for daily calories eaten
                _dailyCaloriesEatenView.setText(String.format("%1$d of %2$d Calories Eaten",2000, user.getDailyCaloriesEaten()));
                //set text for daily calories burned
                _dailyCaloriesBurntView.setText(String.format("%1$d of %2$d Calories Burnt",200,user.getDailyCaloriesBurnt()));
                //set text for daily step walked
                _dailyStepView.setText(String.format("%1$d of %2$d Steps walked",300,user.getSuggestStepWalk()));
                //set text for daily water consumed
                _dailyWaterView.setText(String.format("%1$d of %2$d Glasses water consumed",10, user.getSuggestWaterIntakeInGlass()));

                //setup progress bar and animation for meal
                setupProgressAndAnimation(_dailyCaloriesEatenProgress,2000,user.getDailyCaloriesEaten());
                //setup progress bar and animation for exercise
                setupProgressAndAnimation(_dailyCaloriesBurntProgress,200,user.getDailyCaloriesBurnt());
                //setup progress bar and animation for step
                setupProgressAndAnimation(_dailyStepProgress,300,user.getSuggestStepWalk());
                //setup progress bar and animation for water
                setupProgressAndAnimation(_dailyWaterProgress,10,user.getSuggestWaterIntakeInGlass());


                //add different meal type to the meal list
                addMeal(_mealList, "Breakfast", user.getSuggestBreakfastCalorieEaten(), 100);
                addMeal(_mealList, "Lunch", user.getSuggestLunchCalorieEaten(), 100);
                addMeal(_mealList, "Dinner", user.getSuggestDinnerCalorieEaten(), 100);
                addMeal(_mealList, "Snack", user.getSuggestSnackCalorieEaten(), 100);

                //create meal adapter
                MealAdapter _mealAdapter = new MealAdapter(getContext(), _mealList);
                //set adapter for meal recyclerview
                _mealRecyclerView.setAdapter(_mealAdapter);
                _retrieveData = null;
            });
            return null;
        }
    }

    private void addMeal(List<Meal> _mealList, String mealType, int suggestCalories, int currentCalories) {
        //create meal
        Meal meal = new Meal(mealType);
        meal.setSuggestCalorie(suggestCalories);
        meal.setCurrentCalorie(currentCalories);
        //add meal in meal list
        _mealList.add(meal);
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
