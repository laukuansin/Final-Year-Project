package com.example.a303com_laukuansin.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.activities.ExerciseActivity;
import com.example.a303com_laukuansin.activities.ExerciseDetailActivity;
import com.example.a303com_laukuansin.activities.ExerciseListActivity;
import com.example.a303com_laukuansin.adapters.ExerciseRecordAdapter;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.domains.Exercise;
import com.example.a303com_laukuansin.domains.User;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;
import com.example.a303com_laukuansin.utilities.ProgressAnimation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class ExerciseFragment extends BaseFragment {
    private String date;
    private final User user;
    private LinearProgressIndicator _exerciseProgressBar;
    private TextView _exerciseProgressView;
    private LinearLayout _emptyExerciseLayout;
    private RecyclerView _exerciseRecyclerView;
    private RetrieveExerciseRecordAndStepData _retrieveExerciseRecordAndStep = null;
    private LinearLayout _stepLayout;
    private TextView _stepCaloriesView, _stepCountView;
    private FirebaseFirestore database;

    public ExerciseFragment() {
        user = getSessionHandler().getUser();
    }


    public static ExerciseFragment newInstance(String date) {
        ExerciseFragment fragment = new ExerciseFragment();
        Bundle args = new Bundle();
        args.putString(ExerciseActivity.DATE_KEY, date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(ExerciseActivity.DATE_KEY)) {
                date = getArguments().getString(ExerciseActivity.DATE_KEY, "");
            }
        }
        setHasOptionsMenu(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(date, user);//load data
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise, container, false);
        initialization(view);
        return view;
    }

    private void loadData(String date, User user) {
        if (_retrieveExerciseRecordAndStep == null) {
            _retrieveExerciseRecordAndStep = new RetrieveExerciseRecordAndStepData(date, user);
            _retrieveExerciseRecordAndStep.execute();
        }
    }

    private void initialization(View view) {
        //bind view with id
        _exerciseProgressView = view.findViewById(R.id.exerciseProgressView);
        _exerciseProgressBar = view.findViewById(R.id.exerciseProgressBar);
        _emptyExerciseLayout = view.findViewById(R.id.emptyExerciseLayout);
        _exerciseRecyclerView = view.findViewById(R.id.exerciseRecyclerView);
        _stepLayout = view.findViewById(R.id.stepLayout);
        _stepCaloriesView = view.findViewById(R.id.stepCaloriesView);
        _stepCountView = view.findViewById(R.id.stepCountView);
        Button _addExerciseButton = view.findViewById(R.id.addExerciseButton);

        //setup recyclerview
        _exerciseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        _exerciseRecyclerView.setItemAnimator(new DefaultItemAnimator());
        _exerciseRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        //initialize database
        database = FirebaseFirestore.getInstance();

        //when click add exercise button
        _addExerciseButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getContext(), ExerciseListActivity.class);
                intent.putExtra(ExerciseActivity.DATE_KEY, date);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    private class RetrieveExerciseRecordAndStepData extends AsyncTask<Void, Void, Void> {
        private String date;
        private User user;
        private List<Exercise> _exerciseRecordList;
        private double totalCalories = 0;
        private double stepCalories = 0;
        private int stepWalked = 0;
        private SweetAlertDialog _progressDialog;

        public RetrieveExerciseRecordAndStepData(String date, User user) {
            this.date = date;
            this.user = user;
            //initialize the exercise list
            _exerciseRecordList = new ArrayList<>();
            _progressDialog = showProgressDialog("Loading...",getResources().getColor(R.color.yellow_900));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //date format
            DateFormat format = new SimpleDateFormat("dd MMM yyyy");
            //if the date argument is not "Today"
            if (date.equals("Today")) {
                date = format.format(new Date());//get current date
            }

            getActivity().runOnUiThread(() -> {

                //set exercise collection path
                String EXERCISE_COLLECTION_PATH = String.format("ExerciseRecords/%1$s/%2$s", user.getUID(), date);
                //get the Exercise record Collection reference
                //collection path = ExerciseRecords/UID/Date
                CollectionReference exerciseCollectionReference = database.collection(EXERCISE_COLLECTION_PATH);
                //get the exercise record
                exerciseCollectionReference.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    //loop the document
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Exercise exercise = new Exercise();
                        Map<String, Object> documentMapData = document.getData();
                        Long duration = (Long) documentMapData.get("duration");
                        double caloriesBurnedPerKGPerMin = (double) documentMapData.get("caloriesPerKGPerMin");
                        exercise.setExerciseRecordID(document.getId());
                        exercise.setExerciseID(documentMapData.get("exerciseID").toString());
                        exercise.setExerciseName(documentMapData.get("exerciseName").toString());

                        exercise.setDuration(duration.intValue());
                        exercise.setCaloriesBurnedPerKGPerMin(caloriesBurnedPerKGPerMin);
                        //get the current weight * duration * calories burned per min per kg
                        double calories = (caloriesBurnedPerKGPerMin * duration * user.getWeight());
                        totalCalories += calories;
                        exercise.setCalories(calories);

                        _exerciseRecordList.add(exercise);
                    }
                }).addOnFailureListener(exerciseError -> {
                    //show error with dialog
                    ErrorAlert(exerciseError.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(), true).show();
                    _retrieveExerciseRecordAndStep = null;
                });

                //set step collection path
                String STEP_COLLECTION_PATH = String.format("StepRecords/%1$s/%2$s", user.getUID(), date);
                //get the step record collection reference
                //collection path = StepRecords/UID/Date/StepRecordID
                CollectionReference stepCollectionReference = database.collection(STEP_COLLECTION_PATH);
                stepCollectionReference.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if(_progressDialog.isShowing())
                        _progressDialog.dismiss();
                    if(!queryDocumentSnapshots.isEmpty())
                    {
                        stepWalked = queryDocumentSnapshots.getDocuments().get(0).getLong("stepCount").intValue();
                        _stepCountView.setText(String.format("%1$d steps", stepWalked));
                        stepCalories = stepWalked * user.getCaloriesBurnedPerStepWalked();
                        _stepCaloriesView.setText(String.format("%1$d Calories", (int) Math.round(stepCalories)));
                    }

                    totalCalories += stepCalories;
                    //set progress bar
                    _exerciseProgressBar.setMax((int) Math.round(user.getDailyCaloriesBurnt()));
                    _exerciseProgressBar.clearAnimation();
                    //create animation, from 0 animate to current value
                    ProgressAnimation animation = new ProgressAnimation(_exerciseProgressBar, 0, (int) Math.round(totalCalories));
                    animation.setDuration(1000);//set 2 milliseconds animation
                    _exerciseProgressBar.setAnimation(animation);//start animation


                    _exerciseProgressView.setText(String.format("%1$s of %2$s Calories Burnt", (int) Math.round(totalCalories), (int) Math.round(user.getDailyCaloriesBurnt())));

                    //if no exercise record and step walked today
                    if (_exerciseRecordList.isEmpty() && stepWalked <= 0) {
                        _emptyExerciseLayout.setVisibility(View.VISIBLE);
                        _exerciseRecyclerView.setVisibility(View.GONE);
                        _stepLayout.setVisibility(View.GONE);
                    } else {
                        _emptyExerciseLayout.setVisibility(View.GONE);
                        //when the exercise record list is empty;
                        if (_exerciseRecordList.isEmpty()) {
                            _exerciseRecyclerView.setVisibility(View.GONE);
                        }
                        else{
                            _exerciseRecyclerView.setVisibility(View.VISIBLE);
                            ExerciseRecordAdapter adapter = new ExerciseRecordAdapter(getContext(), _exerciseRecordList);
                            _exerciseRecyclerView.setAdapter(adapter);
                        }
                        //when the step walk is high than 0
                        if (stepWalked > 0) {
                            _stepLayout.setVisibility(View.VISIBLE);
                        }
                        else{
                            _stepLayout.setVisibility(View.GONE);
                        }
                    }
                }).addOnFailureListener(stepError -> {
                    if(_progressDialog.isShowing())
                        _progressDialog.dismiss();
                    ErrorAlert(stepError.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(), true).show();
                    _retrieveExerciseRecordAndStep = null;
                });

            });
            _retrieveExerciseRecordAndStep = null;
            return null;
        }
    }

    public void editExerciseRecord(Exercise exercise) {
        Intent intent = new Intent(getContext(), ExerciseDetailActivity.class);
        intent.putExtra(ExerciseDetailActivity.DATE_KEY, date);
        intent.putExtra(ExerciseDetailActivity.EXERCISE_RECORD_ID_KEY, exercise.getExerciseRecordID());
        intent.putExtra(ExerciseDetailActivity.EXERCISE_ID_KEY, exercise.getExerciseID());
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}
