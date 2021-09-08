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
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

public class ExerciseFragment extends BaseFragment {
    private String date;
    private User user;
    private LinearProgressIndicator _exerciseProgressBar;
    private TextView _exerciseProgressView;
    private LinearLayout _emptyExerciseLayout;
    private RecyclerView _exerciseRecyclerView;
    private RetrieveExerciseRecord _retrieveExerciseRecord = null;
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
        if (_retrieveExerciseRecord == null) {
            _retrieveExerciseRecord = new RetrieveExerciseRecord(date, user);
            _retrieveExerciseRecord.execute();
        }
    }

    private void initialization(View view)
    {
        //bind view with id
        _exerciseProgressView = view.findViewById(R.id.exerciseProgressView);
        _exerciseProgressBar = view.findViewById(R.id.exerciseProgressBar);
        _emptyExerciseLayout = view.findViewById(R.id.emptyExerciseLayout);
        _exerciseRecyclerView = view.findViewById(R.id.exerciseRecyclerView);
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

    private class RetrieveExerciseRecord extends AsyncTask<Void, Void, Void> {
        private String date;
        private User user;
        private double totalCalories = 0;

        public RetrieveExerciseRecord(String date, User user) {
            this.date = date;
            this.user = user;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //date format
            DateFormat format = new SimpleDateFormat("dd MMM yyyy");
            //if the date argument is not "Today"
            if (date.equals("Today")) {
                date = format.format(new Date());//get current date
            }

            //set collection path
            String COLLECTION_PATH = String.format("ExerciseRecords/%1$s/%2$s", user.getUID(), date);

            //get the Collection reference
            //collection path = ExerciseRecords/UID/Date
            CollectionReference collectionReference = database.collection(COLLECTION_PATH);
            //get the exercise record
            collectionReference.addSnapshotListener(getActivity(), (value, error) -> {
                //if error appears
                if (error != null) {
                    //show error with dialog
                    ErrorAlert(error.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(),true).show();
                    _retrieveExerciseRecord = null;
                    return;
                }
                getActivity().runOnUiThread(() -> {
                    //initialize the exercise list
                    List<Exercise> _exerciseRecordList = new ArrayList<>();
                    //loop the document
                    for (DocumentSnapshot document : value.getDocuments()) {
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
                        double calories = (caloriesBurnedPerKGPerMin*duration*user.getWeight());
                        totalCalories+=calories;
                        exercise.setCalories(calories);

                        _exerciseRecordList.add(exercise);
                    }

                    //set progress bar
                    _exerciseProgressBar.setMax((int) Math.round(user.getDailyCaloriesBurnt()));
                    _exerciseProgressBar.clearAnimation();

                    //create animation, from 0 animate to current value
                    ProgressAnimation animation = new ProgressAnimation(_exerciseProgressBar, 0, (int) Math.round(totalCalories));
                    animation.setDuration(1000);//set 2 milliseconds animation
                    _exerciseProgressBar.setAnimation(animation);//start animation


                    _exerciseProgressView.setText(String.format("%1$s of %2$s Calories Burnt", (int)Math.round(totalCalories), (int) Math.round(user.getDailyCaloriesBurnt())));

                    //if no exercise record at today
                    if(_exerciseRecordList.isEmpty())
                    {
                        _emptyExerciseLayout.setVisibility(View.VISIBLE);
                        _exerciseRecyclerView.setVisibility(View.GONE);
                    }
                    else{
                        _emptyExerciseLayout.setVisibility(View.GONE);
                        _exerciseRecyclerView.setVisibility(View.VISIBLE);
                        ExerciseRecordAdapter adapter = new ExerciseRecordAdapter( getContext(),_exerciseRecordList);
                        _exerciseRecyclerView.setAdapter(adapter);
                    }

                });
            });

            _retrieveExerciseRecord = null;
            return null;
        }
    }
    public void editExerciseRecord(Exercise exercise)
    {
        Intent intent = new Intent(getContext(), ExerciseDetailActivity.class);
        intent.putExtra(ExerciseDetailActivity.DATE_KEY, date);
        intent.putExtra(ExerciseDetailActivity.EXERCISE_RECORD_ID_KEY,exercise.getExerciseRecordID());
        intent.putExtra(ExerciseDetailActivity.EXERCISE_ID_KEY,exercise.getExerciseID());
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}
