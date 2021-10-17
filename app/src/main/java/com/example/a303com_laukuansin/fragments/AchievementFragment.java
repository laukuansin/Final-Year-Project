package com.example.a303com_laukuansin.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.domains.User;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class AchievementFragment extends BaseFragment {
    private final User user;
    private Calendar currentMonthYear;
    private TextView _monthYearView;
    private RetrieveMonthlyData _retrieveMonthlyData = null;
    private final String monthYearFormat = "MMMM yyyy";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
    private FirebaseFirestore database;
    private LinearLayout _containerLayout, _emptyRecordLayout;
    private TextView _goalView,_recordView,_averageView,_scoreView, _targetCaloriesEaten, _targetCaloriesBurnt,_recordCaloriesEaten,_recordCaloriesBurnt;
    private TextView _titleRecordCaloriesEaten,_recordCaloriesEatenGoal,_titleRecordCaloriesBurnt,_recordCaloriesBurntGoal;
    private TextView _averageCaloriesEaten,_averageCaloriesBurnt;
    private TextView _totalScore,_scoreReview;

    public AchievementFragment() {
        user = getSessionHandler().getUser();
    }

    public static AchievementFragment newInstance() {
        return new AchievementFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_achievement, container, false);
        initialization(view);
        //load data
        currentMonthYear = Calendar.getInstance();
        loadData(user, currentMonthYear);
        return view;
    }

    private void loadData(User user, Calendar calendar) {
        if (_retrieveMonthlyData == null) {
            _retrieveMonthlyData = new RetrieveMonthlyData(user, calendar);
            _retrieveMonthlyData.execute();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(_retrieveMonthlyData!=null)
        {
            _retrieveMonthlyData.cancel(true);
        }
    }

    private void initialization(View view) {
        //bind view with id
        LinearLayout _monthYearLayout = view.findViewById(R.id.monthYearLayout);
        _containerLayout = view.findViewById(R.id.containerLayout);
        _emptyRecordLayout = view.findViewById(R.id.emptyRecordLayout);
        _monthYearView = view.findViewById(R.id.monthYearView);
        _goalView = view.findViewById(R.id.goalView);
        _recordView = view.findViewById(R.id.recordView);
        _averageView = view.findViewById(R.id.averageView);
        _scoreView = view.findViewById(R.id.scoreView);
        _targetCaloriesEaten = view.findViewById(R.id.targetCaloriesEaten);
        _targetCaloriesBurnt = view.findViewById(R.id.targetCaloriesBurnt);
        _recordCaloriesEaten = view.findViewById(R.id.recordCaloriesEaten);
        _recordCaloriesBurnt = view.findViewById(R.id.recordCaloriesBurnt);
        _titleRecordCaloriesEaten = view.findViewById(R.id.titleRecordCaloriesEaten);
        _recordCaloriesEatenGoal = view.findViewById(R.id.recordCaloriesEatenGoal);
        _titleRecordCaloriesBurnt = view.findViewById(R.id.titleRecordCaloriesBurnt);
        _recordCaloriesBurntGoal = view.findViewById(R.id.recordCaloriesBurntGoal);
        _averageCaloriesEaten = view.findViewById(R.id.averageCaloriesEaten);
        _averageCaloriesBurnt = view.findViewById(R.id.averageCaloriesBurnt);
        _totalScore = view.findViewById(R.id.score);
        _scoreReview = view.findViewById(R.id.scoreReview);

        //initialize database
        database = FirebaseFirestore.getInstance();
        //on click month year layout
        _monthYearLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(getContext(), (selectedMonth, selectedYear) -> {
                    currentMonthYear.set(selectedYear, selectedMonth + 1, 0);
                    _monthYearView.setText(DateFormat.format(monthYearFormat, currentMonthYear));
                    loadData(user, currentMonthYear);
                }, currentMonthYear.get(Calendar.YEAR), currentMonthYear.get(Calendar.MONTH) - 1);

                builder.setTitle("Select Month and Year")
                        .setActivatedMonth(currentMonthYear.get(Calendar.MONTH))
                        .setMinYear(2000)
                        .setActivatedYear(currentMonthYear.get(Calendar.YEAR))
                        .setMaxYear(Calendar.getInstance().get(Calendar.YEAR))
                        .build()
                        .show();
            }
        });

    }

    private class RetrieveMonthlyData extends AsyncTask<Void, Void, Void> {
        private User user;
        private SweetAlertDialog _progressDialog;
        private Calendar calendar;
        private int dayRecordCaloriesEaten = 0, dayRecordCaloriesBurnt = 0;
        private int reachGoalCaloriesEaten = 0, reachGoalCaloriesBurnt = 0;
        private double totalCaloriesEaten = 0, totalCaloriesBurnt = 0;
        private DatabaseCallBackListener _listener;

        public RetrieveMonthlyData(User user, Calendar calendar) {
            this.user = user;
            this.calendar = calendar;
            _progressDialog = showProgressDialog("Loading", getResources().getColor(R.color.colorPrimary));

            //initialize database call back
            _listener = new DatabaseCallBackListener() {
                @Override
                public void updateCaloriesEaten(int days) {
                    dayRecordCaloriesEaten = days;
                }

                @Override
                public void updateCaloriesBurnt(HashMap<Date, Double> hashMap, String startDateOfMonth, String endDateOfMonth) {
                    //step collection path
                    String STEP_COLLECTION_PATH = String.format("StepRecords/%1$s/Records", user.getUID());
                    //get step collection reference
                    CollectionReference stepCollectionRef = database.collection(STEP_COLLECTION_PATH);
                    stepCollectionRef.get().addOnSuccessListener(stepSnapshots -> {
                        if (_progressDialog.isShowing())
                            _progressDialog.dismiss();
                        for (DocumentSnapshot documentSnapshot : stepSnapshots.getDocuments()) {
                            Map<String, Object> documentMapData = documentSnapshot.getData();
                            Long stepWalked = (long) documentMapData.get("stepCount");
                            double calories = stepWalked * user.getCaloriesBurnedPerStepWalked();
                            String dateString = documentMapData.get("date").toString();
                            try {
                                Date date = dateFormat.parse(dateString);
                                Date startDate = dateFormat.parse(startDateOfMonth);
                                Date endDate = dateFormat.parse(endDateOfMonth);
                                //if the start date is not after the date AND end date is not before date
                                //example date = 15 September 2021
                                //start date = 01 September 2021
                                //end date = 30 September 2021
                                //date is in the range of September
                                if (!startDate.after(date) && !endDate.before(date)) {
                                    //if hash map contains the date before
                                    if (hashMap.containsKey(date)) {
                                        //get the hashmap value
                                        double temp = hashMap.get(date);
                                        //update the value
                                        hashMap.put(date, temp + calories);
                                    } else {
                                        //add the key and value
                                        hashMap.put(date, calories);
                                    }
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        for (Map.Entry<Date, Double> entry : hashMap.entrySet()) {
                            //total calories burnt in a day
                            double totalCaloriesInDay = entry.getValue();
                            //accept +-10% deviation
                            if(totalCaloriesInDay>=user.getDailyCaloriesBurnt()*0.95&&totalCaloriesInDay<=user.getDailyCaloriesBurnt()*1.05)
                            {
                                reachGoalCaloriesBurnt++;
                            }
                            //calculate total calories in a month
                            totalCaloriesBurnt += entry.getValue();
                        }
                        dayRecordCaloriesBurnt = hashMap.size();

                        if (dayRecordCaloriesBurnt == 0 && dayRecordCaloriesEaten == 0) {
                            _emptyRecordLayout.setVisibility(View.VISIBLE);
                            _containerLayout.setVisibility(View.GONE);
                        } else {
                            _emptyRecordLayout.setVisibility(View.GONE);
                            _containerLayout.setVisibility(View.VISIBLE);

                            //set the target goal
                            switch (user.getTargetGoal()) {
                                case "Maintain Weight": {
                                    _goalView.setText(String.format("To maintain the body weight at %.1f kg, you need to reach", user.getTargetWeight()));
                                    break;
                                }
                                case "Gain Weight": {
                                    _goalView.setText(String.format("To gain the body weight to %.1f kg, you need to reach", user.getTargetWeight()));
                                    break;
                                }
                                case "Lose Weight": {
                                    _goalView.setText(String.format("To lose the body weight to %.1f kg, you need to reach", user.getTargetWeight()));
                                    break;
                                }
                            }
                            //set daily target
                            _targetCaloriesEaten.setText(String.valueOf(Math.round(user.getDailyCaloriesEaten())));
                            _targetCaloriesBurnt.setText(String.valueOf(Math.round(user.getDailyCaloriesBurnt())));

                            //set the text with view
                            _recordView.setText(String.format("Record in %1$s",DateFormat.format(monthYearFormat,calendar)));
                            _averageView.setText(String.format("Average in %1$s",DateFormat.format(monthYearFormat,calendar)));
                            _scoreView.setText(String.format("Total Score in %1$s",DateFormat.format(monthYearFormat,calendar)));

                            _recordCaloriesEaten.setText(String.format("You have made %1$d %2$s record of Calories Eaten",dayRecordCaloriesEaten,dayRecordCaloriesEaten>1?"days":"day"));
                            _recordCaloriesBurnt.setText(String.format("You have made %1$d %2$s record of Calories Burnt",dayRecordCaloriesBurnt,dayRecordCaloriesBurnt>1?"days":"day"));

                            _titleRecordCaloriesEaten.setText(String.format("Among the %1$d %2$s record of Calories Eaten",dayRecordCaloriesEaten,dayRecordCaloriesEaten>1?"days":"day"));
                            _recordCaloriesEatenGoal.setText(String.format("The number of day you have reach target goal of Calories Eaten is %1$d",reachGoalCaloriesEaten));

                            _titleRecordCaloriesBurnt.setText(String.format("Among the %1$d %2$s record of Calories Burnt",dayRecordCaloriesBurnt,dayRecordCaloriesBurnt>1?"days":"day"));
                            _recordCaloriesBurntGoal.setText(String.format("The number of day you have reach target goal of Calories Burnt is %1$d",reachGoalCaloriesBurnt));

                            _averageCaloriesEaten.setText(String.valueOf((int)Math.round(totalCaloriesEaten/dayRecordCaloriesEaten)));
                            _averageCaloriesBurnt.setText(String.valueOf((int)Math.round(totalCaloriesBurnt/dayRecordCaloriesBurnt)));

                            int maximumDayOfRecord = Math.max(dayRecordCaloriesEaten,dayRecordCaloriesBurnt);
                            // total day reach the goal of calories eaten + total day reach the goal of calories burnt divide with 2 times maximum day of record
                            //because for example user have record 20 day calories eaten, but only 15 day for calories burnt.
                            //its mean that user have miss out 5 day of calories burnt
                            double score = ((double)(reachGoalCaloriesEaten + reachGoalCaloriesBurnt)/(double)(2*maximumDayOfRecord))*100;

                            //rounding score
                            _totalScore.setText(String.format("%1$d/100",Math.round(score)));
                            if(score<50)
                            {
                                _scoreReview.setText("Don't give up! You can do this");
                                _totalScore.setTextColor(getResources().getColor(R.color.red_A700));
                            }
                            else if(score<80)
                            {
                                _scoreReview.setText("Keep Going!");
                                _totalScore.setTextColor(getResources().getColor(R.color.yellow_900));
                            }
                            else{
                                _scoreReview.setText("Excellent! You have done a great job");
                                _totalScore.setTextColor(getResources().getColor(R.color.green_A700));
                            }
                        }
                    }).addOnFailureListener(e -> {
                        ErrorAlert(e.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(), true).show();
                    });
                }
            };
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getActivity().runOnUiThread(() -> {
                //set the month year view
                _monthYearView.setText(DateFormat.format(monthYearFormat, calendar));
                //get the first day of month, example 01 September 2021
                calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                String startDateOfMonth = dateFormat.format(calendar.getTime());
                //get the last day of month, example 30 September 2021
                calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                String endDateOfMonth = dateFormat.format(calendar.getTime());

                //meal collection path
                String MEAL_COLLECTION_PATH = String.format("MealRecords/%1$s/Records", user.getUID());
                //get meal collection reference
                CollectionReference mealCollectionRef = database.collection(MEAL_COLLECTION_PATH);
                mealCollectionRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    //hash map is to differentiate each unique day data
                    HashMap<Date, Double> caloriesEatenHashMap = new HashMap<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        Map<String, Object> documentMapData = documentSnapshot.getData();
                        double calories = (double) documentMapData.get("calories");
                        String dateString = documentMapData.get("date").toString();
                        try {
                            Date date = dateFormat.parse(dateString);
                            Date startDate = dateFormat.parse(startDateOfMonth);
                            Date endDate = dateFormat.parse(endDateOfMonth);

                            //if the start date is not after the date AND end date is not before date
                            //example date = 15 September 2021
                            //start date = 01 September 2021
                            //end date = 30 September 2021
                            //date is in the range of September
                            if (!startDate.after(date) && !endDate.before(date)) {
                                //if hash map contains the date before
                                if (caloriesEatenHashMap.containsKey(date)) {
                                    //get the hashmap value
                                    double temp = caloriesEatenHashMap.get(date);
                                    //update the value
                                    caloriesEatenHashMap.put(date, temp + calories);
                                } else {
                                    //add the key and value
                                    caloriesEatenHashMap.put(date, calories);
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    for (Map.Entry<Date, Double> entry : caloriesEatenHashMap.entrySet()) {
                        //that day of total calories
                        double totalCaloriesInDay = entry.getValue();
                        //accept +-5% deviation
                        if(totalCaloriesInDay>=user.getDailyCaloriesEaten()*0.95&&totalCaloriesInDay<=user.getDailyCaloriesEaten()*1.05)
                        {
                            reachGoalCaloriesEaten++;
                        }
                        //calculate total calories in a month
                        totalCaloriesEaten += totalCaloriesInDay;
                    }
                    //set hash map to the call back
                    _listener.updateCaloriesEaten(caloriesEatenHashMap.size());
                }).addOnFailureListener(e -> {
                    if (_progressDialog.isShowing())
                        _progressDialog.dismiss();
                    ErrorAlert(e.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(), true).show();
                });

                //exercise collection path
                String EXERCISE_COLLECTION_PATH = String.format("ExerciseRecords/%1$s/Records", user.getUID());
                //get exercise collection reference
                CollectionReference exerciseCollectionRef = database.collection(EXERCISE_COLLECTION_PATH);
                exerciseCollectionRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    //hash map is to differentiate each unique day data
                    HashMap<Date, Double> caloriesBurntHashMap = new HashMap<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        Map<String, Object> documentMapData = documentSnapshot.getData();
                        Long duration = (long) documentMapData.get("duration");
                        double caloriesBurnedPerKGPerMin = (double) documentMapData.get("caloriesPerKGPerMin");
                        double calories = (caloriesBurnedPerKGPerMin * duration * user.getWeight());
                        String dateString = documentMapData.get("date").toString();
                        try {
                            Date date = dateFormat.parse(dateString);
                            Date startDate = dateFormat.parse(startDateOfMonth);
                            Date endDate = dateFormat.parse(endDateOfMonth);

                            //if the start date is not after the date AND end date is not before date
                            //example date = 15 September 2021
                            //start date = 01 September 2021
                            //end date = 30 September 2021
                            //date is in the range of September
                            if (!startDate.after(date) && !endDate.before(date)) {
                                //if hash map contains the date before
                                if (caloriesBurntHashMap.containsKey(date)) {
                                    //get the hashmap value
                                    double temp = caloriesBurntHashMap.get(date);
                                    //update the value
                                    caloriesBurntHashMap.put(date, temp + calories);
                                } else {
                                    //add the key and value
                                    caloriesBurntHashMap.put(date, calories);
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    //set hash map to the call back
                    _listener.updateCaloriesBurnt(caloriesBurntHashMap, startDateOfMonth, endDateOfMonth);
                }).addOnFailureListener(e -> {
                    if (_progressDialog.isShowing())
                        _progressDialog.dismiss();
                    ErrorAlert(e.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(), true).show();
                });
                _retrieveMonthlyData = null;
            });
            return null;
        }
    }

    private interface DatabaseCallBackListener {
        void updateCaloriesEaten(int days);
        void updateCaloriesBurnt(HashMap<Date, Double> hashMap, String startDate, String endDate);
    }
}
