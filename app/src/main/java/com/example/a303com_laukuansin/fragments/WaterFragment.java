package com.example.a303com_laukuansin.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.activities.WaterActivity;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.domains.User;
import com.example.a303com_laukuansin.utilities.ProgressAnimation;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yangp.ypwaveview.YPWaveView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class WaterFragment extends BaseFragment {
    private String date;
    private final User user;
    private LinearProgressIndicator _waterProgressBar;
    private FirebaseFirestore database;
    private TextView _waterProgressView;
    private YPWaveView _waveProgressView;
    private RetrieveWaterRecord _retrieveWaterRecord = null;
    private int currentGlassOfWater = 0;
    private String waterRecordID = "";
    private ImageView _decreaseButton;
    private String WATER_COLLECTION_PATH = "";

    public WaterFragment() {
        user = getSessionHandler().getUser();
    }

    public static WaterFragment newInstance(String date) {
        WaterFragment fragment = new WaterFragment();
        Bundle args = new Bundle();
        args.putString(WaterActivity.DATE_KEY, date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(WaterActivity.DATE_KEY)) {
                date = getArguments().getString(WaterActivity.DATE_KEY, "");
            }
        }
        setHasOptionsMenu(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(_retrieveWaterRecord!=null)
        {
            _retrieveWaterRecord.cancel(true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_water, container, false);
        initialization(view);
        //load water data
        loadData(user);
        return view;
    }

    private void initialization(View view) {
        //bind view with id
        _waterProgressBar = view.findViewById(R.id.waterProgressBar);
        _waterProgressView = view.findViewById(R.id.waterProgressView);
        _waveProgressView = view.findViewById(R.id.waterWaveProgressView);
        _decreaseButton = view.findViewById(R.id.decreaseGlassButton);
        ImageView _increaseButton = view.findViewById(R.id.increaseGlassButton);

        //initialize database
        database = FirebaseFirestore.getInstance();

        //date format
        DateFormat format = new SimpleDateFormat("dd MMM yyyy");
        //if the date argument is "Today"
        if (date.equals("Today")) {
            date = format.format(new Date());//get current date
        }

        //water collection path
        WATER_COLLECTION_PATH = String.format("WaterRecords/%1$s/Records", user.getUID());

        //when click decrease button
        _decreaseButton.setOnClickListener(clickView -> {
            //if the current glass is 1, then click decrease, the current glass become 0, so the decrease button must disable
            if (currentGlassOfWater <= 1) {
                _decreaseButton.setEnabled(false);
                _decreaseButton.setAlpha(0.2f);
            } else {//otherwise higher than 1
                _decreaseButton.setEnabled(true);
                _decreaseButton.setAlpha(1f);
            }
            //update the water progress
            updateWaterProgress(false);
        });

        //when click increase button
        _increaseButton.setOnClickListener(clickView -> {
            //enable the decrease button
            _decreaseButton.setEnabled(true);
            _decreaseButton.setAlpha(1f);
            //update the water progress
            updateWaterProgress(true);
        });

    }

    private void updateWaterProgress(boolean add) {
        //set the animation from current to current +1 or current -1
        ProgressAnimation animation = new ProgressAnimation(_waterProgressBar, currentGlassOfWater, add ? ++currentGlassOfWater  : --currentGlassOfWater);
        //set the wave progress
        _waveProgressView.setProgress(currentGlassOfWater * 100 / user.getSuggestWaterIntakeInGlass());
        _waterProgressBar.startAnimation(animation);//start animation
        //set progress text
        _waterProgressView.setText(String.format("%1$d of %2$d Glasses water consumed", currentGlassOfWater, user.getSuggestWaterIntakeInGlass()));

        Map<String, Object> waterRecordMap = new HashMap<>();//create hash map to store the water record's data
        waterRecordMap.put("glassOfWater", currentGlassOfWater);
        CollectionReference collectionReference = database.collection(WATER_COLLECTION_PATH);
        if (waterRecordID.isEmpty()) {//if no has record before, then add new record, otherwise update
            waterRecordMap.put("date",date);
            waterRecordID = collectionReference.document().getId();
            collectionReference.document(waterRecordID).set(waterRecordMap);
        } else {
            collectionReference.document(waterRecordID).update(waterRecordMap);
        }
    }

    private void loadData(User user) {
        if (_retrieveWaterRecord == null) {
            _retrieveWaterRecord = new RetrieveWaterRecord(user);
            _retrieveWaterRecord.execute();
        }
    }


    private class RetrieveWaterRecord extends AsyncTask<Void, Void, Void> {
        private User user;
        private SweetAlertDialog _progressDialog;

        public RetrieveWaterRecord(User user) {
            this.user = user;
            _progressDialog = showProgressDialog("Loading...",getResources().getColor(R.color.light_blue_A400));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            _progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getActivity().runOnUiThread(() -> {
                //get water collection reference
                CollectionReference waterCollectionRef = database.collection(WATER_COLLECTION_PATH);
                waterCollectionRef.whereEqualTo("date",date).get().addOnSuccessListener(documentSnapshot -> {
                    if(_progressDialog.isShowing())
                        _progressDialog.dismiss();
                    //if no record
                    if(documentSnapshot.isEmpty())
                    {
                        currentGlassOfWater = 0;
                        _decreaseButton.setEnabled(false);
                        _decreaseButton.setAlpha(0.2f);
                    }
                    else{
                        waterRecordID = documentSnapshot.getDocuments().get(0).getId();
                        currentGlassOfWater = documentSnapshot.getDocuments().get(0).getLong("glassOfWater").intValue();
                        if(currentGlassOfWater<=0)//if the water drink is less than or equal to 0
                        {
                            _decreaseButton.setEnabled(false);
                            _decreaseButton.setAlpha(0.2f);
                        }
                        else{
                            _decreaseButton.setEnabled(true);
                            _decreaseButton.setAlpha(1f);
                        }
                    }
                    //start the animation
                    _waveProgressView.startAnimation();
                    //set the progress for wave
                    setupAnimationForWave();
                    //set progress bar
                    _waterProgressBar.setMax(user.getSuggestWaterIntakeInGlass());
                    _waterProgressBar.clearAnimation();
                    //create animation, from 0 animate to current value
                    ProgressAnimation animation = new ProgressAnimation(_waterProgressBar, 0, currentGlassOfWater);
                    animation.setDuration(1000);//set 1 milliseconds animation
                    _waterProgressBar.setAnimation(animation);//start animation
                    _waterProgressView.setText(String.format("%1$d of %2$d Glasses water consumed", currentGlassOfWater, user.getSuggestWaterIntakeInGlass()));

                }).addOnFailureListener(e -> {
                    if(_progressDialog.isShowing())
                        _progressDialog.dismiss();
                    //show error with dialog
                    ErrorAlert(e.getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(), true).show();
                    _retrieveWaterRecord = null;
                });
            });
            return null;
        }
    }

    private void setupAnimationForWave()
    {
        //set animator set to wave progress view
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animPlay = ObjectAnimator.ofInt(_waveProgressView,"progress",0,currentGlassOfWater*100/user.getSuggestWaterIntakeInGlass());
        //finish the animation within 1.5 seconds
        animPlay.setDuration(1500);
        animPlay.setInterpolator(new DecelerateInterpolator());
        //start and play animator
        animatorSet.playTogether(animPlay);
        animatorSet.start();
    }
}
