package com.example.a303com_laukuansin.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.domains.User;
import com.example.a303com_laukuansin.utilities.DateXAxisValueFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class AnalyticFragment extends BaseFragment {
    private final User user;
    private FirebaseFirestore database;
    private RetrieveCaloriesEaten _retrieveCaloriesEaten = null;
    private RetrieveCaloriesBurned _retrieveCaloriesBurned = null;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
    private BarChart caloriesEatenBarChart, caloriesBurnedBarChart;

    public AnalyticFragment() {
        user = getSessionHandler().getUser();
    }

    public static AnalyticFragment newInstance() {
        return new AnalyticFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analytic, container, false);
        initialization(view);
        //load calories eaten
        loadCaloriesEatenData(user, caloriesEatenBarChart);
        //load calories burned
        loadCaloriesBurnedData(user, caloriesBurnedBarChart);

        return view;
    }

    private void initialization(View view) {
        //bind view with id
        caloriesEatenBarChart = view.findViewById(R.id.caloriesEatenBarChart);
        caloriesBurnedBarChart = view.findViewById(R.id.caloriesBurnedBarChart);

        //initialize database
        database = FirebaseFirestore.getInstance();
    }

    private void loadCaloriesEatenData(User user, BarChart barChart) {
        if (_retrieveCaloriesEaten == null) {
            _retrieveCaloriesEaten = new RetrieveCaloriesEaten(user, barChart);
            _retrieveCaloriesEaten.execute();
        }
    }

    private void loadCaloriesBurnedData(User user, BarChart barChart) {
        if (_retrieveCaloriesBurned == null) {
            _retrieveCaloriesBurned = new RetrieveCaloriesBurned(user, barChart);
            _retrieveCaloriesBurned.execute();
        }
    }

    private class RetrieveCaloriesEaten extends AsyncTask<Void, Void, Void> {
        private User user;
        private SweetAlertDialog _progressDialog;
        private BarChart barChart;

        public RetrieveCaloriesEaten(User user, BarChart barChart) {
            this.user = user;
            this.barChart = barChart;
            _progressDialog = showProgressDialog("Loading...", getResources().getColor(R.color.colorPrimary));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //meal collection path
            String MEAL_COLLECTION_PATH = String.format("MealRecords/%1$s/Records", user.getUID());
            //get meal collection reference
            CollectionReference mealCollectionRef = database.collection(MEAL_COLLECTION_PATH);
            mealCollectionRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (_progressDialog.isShowing())
                    _progressDialog.dismiss();
                //barEntries is the list to input the bar chart data
                List<BarEntry> barEntries = new ArrayList<>();
                //date list to display the x-axis date
                List<String> dateList = new ArrayList<>();
                //hash map is to differentiate each unique day data
                HashMap<Date, Double> hashMap = new HashMap<>();

                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    Map<String, Object> documentMapData = documentSnapshot.getData();
                    double calories = (double) documentMapData.get("calories");
                    String dateString = documentMapData.get("date").toString();
                    try {
                        //change the date in string to date
                        Date date = dateFormat.parse(dateString);
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
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                //for the index purpose, the BarEntry is required
                int count = 0;
                //convert the hash map to tree map for sorting purpose
                Map<Date, Double> sortedMap = new TreeMap<>(hashMap);
                //loop through the sorted map
                for (Map.Entry<Date, Double> entry : sortedMap.entrySet()) {
                    //get the date and calories
                    String dateString = dateFormat.format(entry.getKey());
                    double totalCaloriesPerDay = entry.getValue();

                    //add into barEntries and date list
                    barEntries.add(new BarEntry(count++, (float) totalCaloriesPerDay));
                    dateList.add(dateString);
                }
                //set the BarDataSet and the label of the bar chart
                BarDataSet barDataSet = new BarDataSet(barEntries, "Calories Eaten");
                //change the bar chart color
                barDataSet.setColor(getResources().getColor(R.color.green_A700));
                //change the label text size of the bar chart
                barDataSet.setValueTextSize(10f);

                //set the BarData
                BarData data = new BarData(barDataSet);
                //set each bar width
                data.setBarWidth(0.5f);
                //set data into bar chart
                barChart.setData(data);

                setupBarChartXAxis(dateList, barChart);
                setupBarChartYAxis(barChart, R.color.green_A700, user.getDailyCaloriesEaten());
                setupBarChart(barEntries.size(), barChart);
            }).addOnFailureListener(e -> {
                if (_progressDialog.isShowing())
                    _progressDialog.dismiss();

                ErrorAlert(e.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(), true).show();
                _retrieveCaloriesEaten = null;
            });

            _retrieveCaloriesEaten = null;
            return null;
        }
    }

    private class RetrieveCaloriesBurned extends AsyncTask<Void, Void, Void> {
        private User user;
        private SweetAlertDialog _progressDialog;
        private BarChart barChart;

        public RetrieveCaloriesBurned(User user, BarChart barChart) {
            this.user = user;
            this.barChart = barChart;
            _progressDialog = showProgressDialog("Loading...", getResources().getColor(R.color.colorPrimary));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //exercise collection path
            String EXERCISE_COLLECTION_PATH = String.format("ExerciseRecords/%1$s/Records", user.getUID());
            //step collection path
            String STEP_COLLECTION_PATH = String.format("StepRecords/%1$s/Records", user.getUID());
            //get exercise collection reference
            CollectionReference exerciseCollectionRef = database.collection(EXERCISE_COLLECTION_PATH);
            //get step collection reference
            CollectionReference stepCollectionRef = database.collection(STEP_COLLECTION_PATH);

            exerciseCollectionRef.get().addOnSuccessListener(exerciseSnapshots -> {
                if (_progressDialog.isShowing())
                    _progressDialog.dismiss();

                //barEntries is the list to input the bar chart data
                List<BarEntry> barEntries = new ArrayList<>();
                //date list to display the x-axis date
                List<String> dateList = new ArrayList<>();
                //hash map is to differentiate each unique day data
                HashMap<Date, Double> hashMap = new HashMap<>();

                for (DocumentSnapshot documentSnapshot : exerciseSnapshots.getDocuments()) {
                    Map<String, Object> documentMapData = documentSnapshot.getData();
                    Long duration = (long) documentMapData.get("duration");
                    double caloriesBurnedPerKGPerMin = (double) documentMapData.get("caloriesPerKGPerMin");
                    double calories = (caloriesBurnedPerKGPerMin * duration * user.getWeight());
                    String dateString = documentMapData.get("date").toString();
                    try {
                        //change the date in string to date
                        Date date = dateFormat.parse(dateString);
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
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                //load step calories
                stepCollectionRef.get().addOnSuccessListener(stepSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : stepSnapshots.getDocuments()) {
                        Map<String, Object> documentMapData = documentSnapshot.getData();
                        Long stepWalked = (long) documentMapData.get("stepCount");
                        double calories = stepWalked * user.getCaloriesBurnedPerStepWalked();
                        String dateString = documentMapData.get("date").toString();
                        try {
                            //change the date in string to date
                            Date date = dateFormat.parse(dateString);
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
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    //for the index purpose, the BarEntry is required
                    int count = 0;
                    //convert the hash map to tree map for sorting purpose
                    Map<Date, Double> sortedMap = new TreeMap<>(hashMap);
                    //loop through the sorted map
                    for (Map.Entry<Date, Double> entry : sortedMap.entrySet()) {
                        //get the date and calories
                        String dateString = dateFormat.format(entry.getKey());
                        double totalCaloriesPerDay = entry.getValue();

                        //add into barEntries and date list
                        barEntries.add(new BarEntry(count++, (float) totalCaloriesPerDay));
                        dateList.add(dateString);
                    }
                    //set the BarDataSet and the label of the bar chart
                    BarDataSet barDataSet = new BarDataSet(barEntries, "Calories Burned");
                    //change the bar chart color
                    barDataSet.setColor(getResources().getColor(R.color.yellow_900));
                    //change the label text size of the bar chart
                    barDataSet.setValueTextSize(10f);

                    //set the BarData
                    BarData data = new BarData(barDataSet);
                    //set each bar width
                    data.setBarWidth(0.5f);
                    //set data into bar chart
                    barChart.setData(data);
                   
                    setupBarChartXAxis(dateList, barChart);
                    setupBarChartYAxis(barChart, R.color.yellow_900, user.getDailyCaloriesBurnt());
                    setupBarChart(barEntries.size(), barChart);

                }).addOnFailureListener(e -> {
                    ErrorAlert(e.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(), true).show();
                    _retrieveCaloriesBurned = null;
                });
            }).addOnFailureListener(e -> {
                if (_progressDialog.isShowing())
                    _progressDialog.dismiss();

                ErrorAlert(e.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(), true).show();
                _retrieveCaloriesBurned = null;
            });

            _retrieveCaloriesBurned = null;
            return null;
        }
    }

    private void setupBarChartXAxis(List<String> dateList, BarChart barChart) {
        //get the bar chart x-axis
        XAxis xAxis = barChart.getXAxis();
        //remove the x-axis grid line
        xAxis.setDrawGridLines(false);
        //set the text size
        xAxis.setTextSize(10f);
        //change the text position to bottom
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //rotate the label angle
        xAxis.setLabelRotationAngle(-45);
        //set the granularity is when the user zoom the bar chart, it will only display one value
        xAxis.setGranularity(1);
        //add the new custom value formatter is to change the x-axis label to date, by default it is only can display number
        xAxis.setValueFormatter(new DateXAxisValueFormatter(dateList));
    }

    private void setupBarChartYAxis(BarChart barChart, int color, double goalValue) {
        //limit line is to add a horizontal straight in the bar chart
        //set up the limit and label as "Goal"
        LimitLine limitLine = new LimitLine(Math.round(goalValue), "Goal");
        //set the line width
        limitLine.setLineWidth(1f);
        //set the text size
        limitLine.setTextSize(10f);
        //enable the line into dashed
        limitLine.enableDashedLine(10f, 10f, 0f);
        //set the line color
        limitLine.setLineColor(getResources().getColor(color));

        //get the bar chart y-axis
        YAxis yAxis = barChart.getAxisLeft();
        //also remove the y-axis grid line
        yAxis.setDrawGridLines(false);
        //remove all previous limit lines
        yAxis.removeAllLimitLines();
        //set the axis minimum which mean the minimum of label value is 0
        yAxis.setAxisMinimum(0f);
        //add the limit line
        yAxis.addLimitLine(limitLine);
    }

    private void setupBarChart(int size, BarChart barChart) {
        //remove the description
        barChart.setDescription(null);
        //remove the grid background
        barChart.setDrawGridBackground(false);
        //disable double tap to zoom
        barChart.setDoubleTapToZoomEnabled(false);
        //enable the drag x
        barChart.setDragXEnabled(true);
        //remove the label of y-axis on right
        barChart.getAxisRight().setDrawLabels(false);
        //set the visible of bar chart as maximum 7
        barChart.setVisibleXRangeMaximum(7);
        //move the current view to latest
        barChart.moveViewToX(size);
        //set the bar chart is equal width
        barChart.setFitBars(true);

        //get the bar chart's legend
        Legend legend = barChart.getLegend();
        //change the text size
        legend.setTextSize(14f);
        //not allow the legend is inside the bar chart
        legend.setDrawInside(false);

        //update the bar chart
        barChart.notifyDataSetChanged();
    }

}
