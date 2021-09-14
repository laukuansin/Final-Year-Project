package com.example.a303com_laukuansin.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.activities.BodyWeightActivity;
import com.example.a303com_laukuansin.activities.BodyWeightDetailActivity;
import com.example.a303com_laukuansin.activities.MealActivity;
import com.example.a303com_laukuansin.activities.WaterActivity;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.domains.User;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class BodyWeightDetailFragment extends BaseFragment {
    private String date;
    private String bodyWeightRecordID;
    private User user;
    private FirebaseFirestore database;
    private NumberPickerView _weightPicker, _weightDecimalPicker;
    private String[] displayedValueWeight, displayedValueWeightDecimal;
    private Button _addButton, _updateButton;
    private RetrieveBodyWeightDetail _retrieveDetail = null;
    private final DateFormat format = new SimpleDateFormat("dd MMM yyyy");

    public BodyWeightDetailFragment() {
        user = getSessionHandler().getUser();
    }

    public static BodyWeightDetailFragment newInstance(String date, String bodyWeightRecordID) {
        BodyWeightDetailFragment fragment = new BodyWeightDetailFragment();
        Bundle args = new Bundle();
        args.putString(BodyWeightDetailActivity.DATE_KEY, date);
        args.putString(BodyWeightDetailActivity.BODY_WEIGHT_RECORD_ID_KEY, bodyWeightRecordID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(BodyWeightDetailActivity.DATE_KEY)) {
                date = getArguments().getString(BodyWeightDetailActivity.DATE_KEY, "");
            }
            if (getArguments().containsKey(BodyWeightDetailActivity.BODY_WEIGHT_RECORD_ID_KEY)) {
                bodyWeightRecordID = getArguments().getString(BodyWeightDetailActivity.BODY_WEIGHT_RECORD_ID_KEY, "");
            }
        }
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_body_weight_detail, container, false);
        initialization(view);
        //load data
        loadData(user, date);
        return view;
    }

    private void loadData(User user, String date) {
        if (_retrieveDetail == null) {
            _retrieveDetail = new RetrieveBodyWeightDetail(user, date);
            _retrieveDetail.execute();
        }
    }

    private void initialization(View view) {
        //bind view with ID
        _weightPicker = view.findViewById(R.id.weightPicker);
        _weightDecimalPicker = view.findViewById(R.id.weightDecimalPicker);
        _addButton = view.findViewById(R.id.addButton);
        _updateButton = view.findViewById(R.id.updateButton);

        //setup the weight picker
        setupWeightPicker();
        //setup the weight decimal picker
        setupWeightDecimalPicker();
        //set animation
        setAnimation(view);
        //initialize database
        database = FirebaseFirestore.getInstance();

        //when click add button
        _addButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                saveBodyWeight();
            }
        });

        //when click update button
        _updateButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                updateBodyWeight();
            }
        });
    }

    private void setAnimation(View view) {
        TextView _units = view.findViewById(R.id.units);
        Animation _slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_animation_shorter);//bottom to up

        _weightPicker.setAnimation(_slideUp);
        _weightDecimalPicker.setAnimation(_slideUp);
        _units.setAnimation(_slideUp);
    }

    private void setupWeightPicker() {
        displayedValueWeight = new String[271];//create display values for weight, the weight is limit between 30 KG until 300 KG
        for (int i = 0; i < 271; i++) {
            displayedValueWeight[i] = String.valueOf(i + 30);//assign value into string array, array start from 0, so the minimum weight is 30, then must +30
        }
        _weightPicker.setDisplayedValues(displayedValueWeight);//set string array into number picker view
        //the range between 30 to 300 is 270, so minimum value is 0, maximum value is 270
        _weightPicker.setMinValue(0);
        _weightPicker.setMaxValue(270);
    }

    private void setupWeightDecimalPicker() {
        displayedValueWeightDecimal = new String[10];//create display values for weight decimal, the weight is limit between 0 until 9
        for (int i = 0; i < 10; i++) {
            displayedValueWeightDecimal[i] = String.valueOf(i);//assign value into string array
        }
        _weightDecimalPicker.setDisplayedValues(displayedValueWeightDecimal);//set string array into number picker view
        _weightDecimalPicker.setMinValue(0);
        _weightDecimalPicker.setMaxValue(9);

    }

    private class RetrieveBodyWeightDetail extends AsyncTask<Void, Void, Void> {
        private User user;
        private String date;
        private double bodyWeight;
        private SweetAlertDialog _progressDialog;

        public RetrieveBodyWeightDetail(User user, String date) {
            this.user = user;
            this.date = date;
            bodyWeight = user.getWeight();
            _progressDialog = showProgressDialog("Loading", getResources().getColor(R.color.pink_A400));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getActivity().runOnUiThread(() -> {
                //if the date argument is "Today"
                if (date.equals("Today")) {
                    date = format.format(new Date());//get current date
                }

                //set body weight collection path
                String BODY_WEIGHT_COLLECTION_PATH = String.format("BodyWeightRecords/%1$s/Records", user.getUID());
                //get the body weight record collection reference
                //collection path = BodyWeightRecords/UID/Records
                CollectionReference bodyWeightCollectionReference = database.collection(BODY_WEIGHT_COLLECTION_PATH);
                //to check the date has record before or not
                bodyWeightCollectionReference.whereEqualTo("date", date).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (_progressDialog.isShowing())
                        _progressDialog.dismiss();

                    if (!queryDocumentSnapshots.isEmpty()) {
                        bodyWeightRecordID = queryDocumentSnapshots.getDocuments().get(0).getId();
                        bodyWeight = queryDocumentSnapshots.getDocuments().get(0).getDouble("bodyWeight");
                    }
                    //set the front number
                    _weightPicker.setValue((int) Math.floor(bodyWeight - 30));
                    //set the back number
                    _weightDecimalPicker.setValue((int) ((bodyWeight * 10) - (Math.floor(bodyWeight) * 10)));
                    //if no have body weight record before
                    if (bodyWeightRecordID.isEmpty()) {
                        _addButton.setVisibility(View.VISIBLE);
                        _updateButton.setVisibility(View.GONE);
                    } else {
                        _updateButton.setVisibility(View.VISIBLE);
                        _addButton.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(e -> {
                    if (_progressDialog.isShowing())
                        _progressDialog.dismiss();

                    ErrorAlert(e.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(), true).show();
                    _retrieveDetail = null;
                });
            });
            return null;
        }
    }

    private void saveBodyWeight() {
        //create progress dialog
        SweetAlertDialog _progressDialog = showProgressDialog("Adding...", getResources().getColor(R.color.pink_A400));
        _progressDialog.show();

        double bodyWeight = getBodyWeightFromPicker();

        //if the date argument is "Today"
        if (date.equals("Today")) {
            date = format.format(new Date());//get current date
        }
        //the collection path, example: BodyWeightRecords/UID/Records
        String BODY_WEIGHT_COLLECTION_PATH = String.format("BodyWeightRecords/%1$s/Records", getSessionHandler().getUser().getUID());
        //get body weight record reference
        CollectionReference bodyWeightRecordRef = database.collection(BODY_WEIGHT_COLLECTION_PATH);
        //create body weight record class
        Map<String, Object> bodyWeightRecordMap = new HashMap<>();//create hash map to store the body weight record's data
        bodyWeightRecordMap.put("date", date);
        bodyWeightRecordMap.put("bodyWeight", bodyWeight);
        //add the record of body weight
        bodyWeightRecordRef.add(bodyWeightRecordMap).addOnSuccessListener(documentReference ->
                bodyWeightRecordRef.orderBy("date", Query.Direction.DESCENDING).limit(1).get().addOnSuccessListener(queryDocumentSnapshots -> {

            if (_progressDialog.isShowing())
                _progressDialog.dismiss();
            //get the last record of body weight that user has made
            String latestDate = queryDocumentSnapshots.getDocuments().get(0).getString("date");
            //then to compare with the date and latestDate, to check whether need to change the current body weight or not
            //Example: latestDate: 12 Sep 2021, date: 10 Sep 2021, this situation no need to update current weight, because user is add the history body weight
            //Example: latestDate: 12 Sep 2021, date: 14 Sep 2021, the last record user has made is at 12 Sep 2021, so user currently is add the body weight in 14 Sep
            //2021, so it must update the current weight of user
            //Example: latestDate: 12 Sep 2021, date: 12  Sep 2021, same date also need to update the body weight
            try {
                if (!format.parse(latestDate).after(format.parse(date))) {
                    //update the preferences user
                    user.setWeight(bodyWeight);
                    getSessionHandler().setUser(user);

                    String USER_DOCUMENT_PATH = String.format("Users/%1$s", user.getUID());
                    Map<String, Object> userMap = new HashMap<>();//create hash map to store the user's data
                    userMap.put("weight", bodyWeight);
                    //update the database user
                    DocumentReference userDocumentRef = database.document(USER_DOCUMENT_PATH);
                    userDocumentRef.update(userMap);
                }

                //toast success message
                Toast.makeText(getContext(), "Add Body Weight Success", Toast.LENGTH_SHORT).show();
                //finish current activity
                getActivity().finish();

            } catch (ParseException e) {
                e.printStackTrace();
            }
        })).addOnFailureListener(e -> {
            if (_progressDialog.isShowing())
                _progressDialog.dismiss();
            ErrorAlert(e.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(), true).show();
        });
    }

    private void updateBodyWeight() {
        //create progress dialog
        SweetAlertDialog _progressDialog = showProgressDialog("Updating...", getResources().getColor(R.color.pink_A400));
        _progressDialog.show();

        double bodyWeight = getBodyWeightFromPicker();

        //if the date argument is "Today"
        if (date.equals("Today")) {
            date = format.format(new Date());//get current date
        }
        //the collection path, example: BodyWeightRecords/UID/Records
        String BODY_WEIGHT_COLLECTION_PATH = String.format("BodyWeightRecords/%1$s/Records", getSessionHandler().getUser().getUID());
        //get body weight record reference
        CollectionReference bodyWeightRecordRef = database.collection(BODY_WEIGHT_COLLECTION_PATH);
        //create body weight record class
        Map<String, Object> bodyWeightRecordMap = new HashMap<>();//create hash map to store the body weight record's data
        bodyWeightRecordMap.put("bodyWeight", bodyWeight);
        //update the record of body weight
        bodyWeightRecordRef.document(bodyWeightRecordID).update(bodyWeightRecordMap).addOnSuccessListener(unused ->
                bodyWeightRecordRef.orderBy("date", Query.Direction.DESCENDING).limit(1).get().addOnSuccessListener(queryDocumentSnapshots -> {

            if (_progressDialog.isShowing())
                _progressDialog.dismiss();
            //get the last record of body weight that user has made
            String latestDate = queryDocumentSnapshots.getDocuments().get(0).getString("date");
            //then to compare with the date and latestDate, to check whether need to change the current body weight or not
            //Example: latestDate: 12 Sep 2021, date: 10 Sep 2021, this situation no need to update current weight, because user is add the history body weight
            //Example: latestDate: 12 Sep 2021, date: 14 Sep 2021, the last record user has made is at 12 Sep 2021, so user currently is add the body weight in 14 Sep
            //2021, so it must update the current weight of user
            //Example: latestDate: 12 Sep 2021, date: 12  Sep 2021, same date also need to update the body weight
            try {
                if (!format.parse(latestDate).after(format.parse(date))) {
                    //update the preferences user
                    user.setWeight(bodyWeight);
                    getSessionHandler().setUser(user);

                    String USER_DOCUMENT_PATH = String.format("Users/%1$s", user.getUID());
                    Map<String, Object> userMap = new HashMap<>();//create hash map to store the user's data
                    userMap.put("weight", bodyWeight);
                    //update the database user
                    DocumentReference userDocumentRef = database.document(USER_DOCUMENT_PATH);
                    userDocumentRef.update(userMap);
                }

                //toast success message
                Toast.makeText(getContext(), "Update Body Weight Success", Toast.LENGTH_SHORT).show();
                //finish current activity
                getActivity().finish();

            } catch (ParseException e) {
                e.printStackTrace();
            }
        })).addOnFailureListener(e -> {
            if (_progressDialog.isShowing())
                _progressDialog.dismiss();
            ErrorAlert(e.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(), true).show();
        });
    }

    private double getBodyWeightFromPicker() {
        _weightPicker.stopScrollingAndCorrectPosition();//stop the scrolling the number picker and get correct position
        _weightDecimalPicker.stopScrollingAndCorrectPosition();//stop the scrolling the number picker and get correct position
        double frontNumber = Double.parseDouble(displayedValueWeight[_weightPicker.getValue()]);//get the front number.
        double decimalNumber = Double.parseDouble(displayedValueWeightDecimal[_weightDecimalPicker.getValue()]);//get decimal number.

        return frontNumber + (decimalNumber / 10);
    }
}
