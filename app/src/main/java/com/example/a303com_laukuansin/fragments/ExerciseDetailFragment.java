package com.example.a303com_laukuansin.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.activities.ExerciseActivity;
import com.example.a303com_laukuansin.activities.ExerciseDetailActivity;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.domains.User;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class ExerciseDetailFragment extends BaseFragment {
    private String date;
    private User user;
    private String exerciseID;
    private String exerciseName;
    private String exerciseRecordID = "";
    private ImageView _imageView;
    private FirebaseFirestore database;
    private RetrieveExerciseDetail _retrieveExerciseDetail = null;
    private Button _addButton, _deleteButton, _updateButton;
    private TextView _exerciseName, _caloriesBurned;
    private TextInputLayout _inputDuration;
    private double caloriesPerKGPerMinutes;

    public ExerciseDetailFragment() {
        user = getSessionHandler().getUser();
    }

    public static ExerciseDetailFragment newInstance(String date, String exerciseID, String exerciseRecordID) {
        ExerciseDetailFragment fragment = new ExerciseDetailFragment();
        Bundle args = new Bundle();
        args.putString(ExerciseDetailActivity.DATE_KEY, date);
        args.putString(ExerciseDetailActivity.EXERCISE_ID_KEY, exerciseID);
        args.putString(ExerciseDetailActivity.EXERCISE_RECORD_ID_KEY, exerciseRecordID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(ExerciseDetailActivity.DATE_KEY)) {
                date = getArguments().getString(ExerciseDetailActivity.DATE_KEY, "");
            }
            if (getArguments().containsKey(ExerciseDetailActivity.EXERCISE_ID_KEY)) {
                exerciseID = getArguments().getString(ExerciseDetailActivity.EXERCISE_ID_KEY, "");
            }
            if (getArguments().containsKey(ExerciseDetailActivity.EXERCISE_RECORD_ID_KEY)) {
                exerciseRecordID = getArguments().getString(ExerciseDetailActivity.EXERCISE_RECORD_ID_KEY, "");
            }
        }
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_detail, container, false);
        initialization(view);
        //load data
        loadData(exerciseID);

        return view;
    }

    private void initialization(View view) {
        //bind view with ID
        _imageView = view.findViewById(R.id.imageView);
        _addButton = view.findViewById(R.id.addButton);
        _updateButton = view.findViewById(R.id.updateButton);
        _deleteButton = view.findViewById(R.id.deleteButton);
        TextInputEditText _durationEditText = view.findViewById(R.id.durationEditLayout);
        _exerciseName = view.findViewById(R.id.exerciseName);
        _caloriesBurned = view.findViewById(R.id.caloriesBurned);
        _inputDuration = view.findViewById(R.id.durationLayout);
        Toolbar _toolbar = view.findViewById(R.id.toolbar);

        //setup toolbar
        setupToolbar(_toolbar);

        //set the key on can enter 0 to 9
        _durationEditText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        //add the text changed listener
        _durationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //before text typing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().isEmpty()) {
                    //disable add and update button
                    _addButton.setAlpha(0.2f);
                    _addButton.setEnabled(false);
                    _updateButton.setAlpha(0.2f);
                    _updateButton.setEnabled(false);
                } else {
                    //enable add and update button
                    _addButton.setAlpha(1.0f);
                    _addButton.setEnabled(true);
                    _updateButton.setAlpha(1.0f);
                    _updateButton.setEnabled(true);
                    calculateCalories(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        _addButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                saveExercise();
            }
        });
        _updateButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                updateExercise();
            }
        });
        _deleteButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                SweetAlertDialog warningDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
                warningDialog.setTitleText("Are you sure?")
                        .setContentText("Won't be able to recover this record!")
                        .setConfirmText("Yes")
                        .setCancelText("No")
                        .setConfirmClickListener(sweetAlertDialog -> deleteExercise(sweetAlertDialog))
                        .setCancelClickListener(Dialog::dismiss).show();
            }
        });

        //initial database
        database = FirebaseFirestore.getInstance();
    }

    private void calculateCalories(String duration) {
        double caloriesBurned = Integer.parseInt(duration) * caloriesPerKGPerMinutes * user.getWeight();
        _caloriesBurned.setText(String.format("Calories Burned: %1$d", (int) Math.round(caloriesBurned)));
    }

    private void saveExercise() {
        int duration = 0;
        boolean check = true;
        String durationStr = _inputDuration.getEditText().getText().toString();
        if (durationStr.isEmpty()) {//if duration is empty
            ErrorAlert("Duration cannot be empty!", sweetAlertDialog -> sweetAlertDialog.dismiss(), true).show();
            check = false;
        } else {
            duration = Integer.parseInt(durationStr);
            if (duration <= 0) {//if duration is 0
                ErrorAlert("Quantity less than 0!", sweetAlertDialog -> sweetAlertDialog.dismiss(), true).show();
                check = false;
            }
        }

        if (check)//if no error
        {
            //add exercise record to database
            addExerciseRecordToDatabase(duration);
        }
    }

    private void addExerciseRecordToDatabase(int duration) {
        //create progress dialog
        SweetAlertDialog _progressDialog = showProgressDialog("Adding...", getResources().getColor(R.color.yellow_900));
        _progressDialog.show();

        //date format
        DateFormat format = new SimpleDateFormat("dd MMM yyyy");
        //if the date argument is "Today"
        if (date.equals("Today")) {
            date = format.format(new Date());//get current date
        }
        //the collection path, example: ExerciseRecords/UID/Records
        String COLLECTION_PATH = String.format("ExerciseRecords/%1$s/Records", getSessionHandler().getUser().getUID());
        //get exercise record reference
        CollectionReference exerciseRecordRef = database.collection(COLLECTION_PATH);
        //create exercise record class
        Map<String, Object> exerciseRecordMap = new HashMap<>();//create hash map to store the exercise record's data
        exerciseRecordMap.put("exerciseID", exerciseID);
        exerciseRecordMap.put("date",date);
        exerciseRecordMap.put("exerciseName", exerciseName);
        exerciseRecordMap.put("caloriesPerKGPerMin", caloriesPerKGPerMinutes);
        exerciseRecordMap.put("duration", duration);

        exerciseRecordRef.add(exerciseRecordMap).addOnSuccessListener(documentReference -> {
            if (_progressDialog.isShowing())//cancel dialog
                _progressDialog.dismiss();
            //toast success message
            Toast.makeText(getContext(), "Add Exercise Success", Toast.LENGTH_SHORT).show();
            //intent to exercise activity
            Intent intent = new Intent(getContext(), ExerciseActivity.class);
            startActivity(intent);
            //finish current activity
            getActivity().finish();
        }).addOnFailureListener(e -> {
            if (_progressDialog.isShowing())//cancel dialog
                _progressDialog.dismiss();
            //show error dialog
            ErrorAlert(e.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(), true).show();
        });
    }

    private void updateExercise()
    {
        int duration = 0;
        boolean check = true;
        String durationStr = _inputDuration.getEditText().getText().toString();
        if (durationStr.isEmpty()) {//if duration is empty
            ErrorAlert("Duration cannot be empty!", sweetAlertDialog -> sweetAlertDialog.dismiss(), true).show();
            check = false;
        } else {
            duration = Integer.parseInt(durationStr);
            if (duration <= 0) {//if duration is 0
                ErrorAlert("Quantity less than 0!", sweetAlertDialog -> sweetAlertDialog.dismiss(), true).show();
                check = false;
            }
        }

        if (check)//if no error
        {
            //update exercise record to database
            updateExerciseRecordToDatabase(duration);
        }
    }

    private void deleteExercise(SweetAlertDialog sweetAlertDialog)
    {
        sweetAlertDialog.dismiss();
        //create progress dialog
        SweetAlertDialog _progressDialog = showProgressDialog("Deleting...",getResources().getColor(R.color.yellow_900));
        _progressDialog.show();

        //date format
        DateFormat format = new SimpleDateFormat("dd MMM yyyy");
        //if the date argument is "Today"
        if(date.equals("Today"))
        {
            date = format.format(new Date());//get current date
        }
        //the document path, example: ExerciseRecords/UID/Records/ExerciseRecordID
        String DOCUMENT_PATH = String.format("ExerciseRecords/%1$s/Records/%2$s",getSessionHandler().getUser().getUID(),exerciseRecordID);
        //get exercise record document
        DocumentReference exerciseRecordRef = database.document(DOCUMENT_PATH);
        exerciseRecordRef.delete().addOnSuccessListener(unused -> {
            if(_progressDialog.isShowing())
                _progressDialog.dismiss();
            Toast.makeText(getContext(), "Delete exercise success", Toast.LENGTH_SHORT).show();
            //finish current activity
            getActivity().finish();
        }).addOnFailureListener(e -> {
            if(_progressDialog.isShowing())
                _progressDialog.dismiss();
            Log.d("Error:",e.getMessage());
        });
    }

    private void updateExerciseRecordToDatabase(int duration)
    {
        //create progress dialog
        SweetAlertDialog _progressDialog = showProgressDialog("Updating...",getResources().getColor(R.color.yellow_900));
        _progressDialog.show();

        //date format
        DateFormat format = new SimpleDateFormat("dd MMM yyyy");
        //if the date argument is "Today"
        if(date.equals("Today"))
        {
            date = format.format(new Date());//get current date
        }
        //the document path, example: ExerciseRecords/UID/Records/ExerciseRecordID
        String DOCUMENT_PATH = String.format("ExerciseRecords/%1$s/Records/%2$s",getSessionHandler().getUser().getUID(),exerciseRecordID);
        //get exercise record document
        DocumentReference exerciseRecordRef = database.document(DOCUMENT_PATH);
        //create exercise record class
        Map<String, Object> exerciseRecordMap = new HashMap<>();//create hash map to store the exercise record's data
        exerciseRecordMap.put("duration",duration);

        exerciseRecordRef.update(exerciseRecordMap).addOnSuccessListener(documentReference -> {
            if (_progressDialog.isShowing())//cancel dialog
                _progressDialog.dismiss();

            //toast success message
            Toast.makeText(getContext(), "Update Exercise Success", Toast.LENGTH_SHORT).show();
            //finish current activity
            getActivity().finish();
        }).addOnFailureListener(e -> {
            if (_progressDialog.isShowing())//cancel dialog
                _progressDialog.dismiss();
            //show error dialog
            ErrorAlert(e.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(),true).show();
        });
    }

    private void setupToolbar(Toolbar toolbar) {
        //set support action bar
        ((ExerciseDetailActivity) getActivity()).setSupportActionBar(toolbar);
        //set back button
        ((ExerciseDetailActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //set name to empty
        ((ExerciseDetailActivity) getActivity()).getSupportActionBar().setTitle("");
    }

    private void loadData(String exerciseID) {
        if (_retrieveExerciseDetail == null) {
            _retrieveExerciseDetail = new RetrieveExerciseDetail(exerciseID);
            _retrieveExerciseDetail.execute();
        }
    }

    private class RetrieveExerciseDetail extends AsyncTask<Void, Void, Void> {
        private String exerciseID;
        private SweetAlertDialog _progressDialog;

        public RetrieveExerciseDetail(String exerciseID) {
            this.exerciseID = exerciseID;
            _progressDialog = showProgressDialog("Loading...", getResources().getColor(R.color.yellow_900));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getActivity().runOnUiThread(() -> {
                String DOCUMENT_PATH = String.format("Exercises/%1$s", exerciseID);
                //get the Document reference
                //document path = ExerciseList/exerciseID
                DocumentReference documentReference = database.document(DOCUMENT_PATH);
                documentReference.get().addOnSuccessListener(documentSnapshot -> {
                    if (_progressDialog.isShowing())
                        _progressDialog.dismiss();

                    exerciseName = documentSnapshot.getString("exerciseName");
                    caloriesPerKGPerMinutes = documentSnapshot.getDouble("caloriesPerKGPerMin");
                    _exerciseName.setText(exerciseName);
                    Glide.with(getContext())
                            .load(documentSnapshot.getString("imageURL"))
                            .placeholder(R.drawable.ic_image_holder)
                            .centerCrop()
                            .into(_imageView);

                }).addOnFailureListener(e -> {
                    if (_progressDialog.isShowing())
                        _progressDialog.dismiss();
                    ErrorAlert(e.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(), true).show();
                    _retrieveExerciseDetail = null;
                });
            });
            if (exerciseRecordID.isEmpty()) {//if add exercise
                _addButton.setVisibility(View.VISIBLE);
                _updateButton.setVisibility(View.GONE);
                _deleteButton.setVisibility(View.GONE);
            } else {//if update exercise
                _addButton.setVisibility(View.GONE);
                _updateButton.setVisibility(View.VISIBLE);
                _deleteButton.setVisibility(View.VISIBLE);

                getExerciseRecordDetailFromDatabase(date);
            }
            return null;
        }
    }

    private void getExerciseRecordDetailFromDatabase(String date)
    {
        //date format
        DateFormat format = new SimpleDateFormat("dd MMM yyyy");
        //if the date argument is "Today"
        if (date.equals("Today")) {
            date = format.format(new Date());//get current date
        }
        String DOCUMENT_PATH = String.format("ExerciseRecords/%1$s/Records/%2$s", user.getUID(), exerciseRecordID);
        //get the Document reference
        //document path = ExerciseRecords/UID/Records/ExerciseRecordID
        DocumentReference documentReference = database.document(DOCUMENT_PATH);
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            //set the default quantity
            _inputDuration.getEditText().setText(String.valueOf(documentSnapshot.getLong("duration").intValue()));
            caloriesPerKGPerMinutes = documentSnapshot.getDouble("caloriesPerKGPerMin");
            calculateCalories(String.valueOf(documentSnapshot.getLong("duration").intValue()));

        }).addOnFailureListener(e -> {
            //show error with dialog
            ErrorAlert(e.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(),true).show();
        });
    }
}
