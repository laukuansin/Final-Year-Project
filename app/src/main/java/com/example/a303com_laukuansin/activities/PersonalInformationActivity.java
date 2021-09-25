package com.example.a303com_laukuansin.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.cores.AppController;
import com.example.a303com_laukuansin.domains.User;
import com.example.a303com_laukuansin.fragments.PersonalInformation.FillActivityLevelFragment;
import com.example.a303com_laukuansin.fragments.PersonalInformation.FillAgeFragment;
import com.example.a303com_laukuansin.fragments.PersonalInformation.FillGenderFragment;
import com.example.a303com_laukuansin.fragments.PersonalInformation.FillHeightFragment;
import com.example.a303com_laukuansin.fragments.PersonalInformation.FillNameFragment;
import com.example.a303com_laukuansin.fragments.PersonalInformation.FillTargetWeightFragment;
import com.example.a303com_laukuansin.fragments.PersonalInformation.FillWeightFragment;
import com.example.a303com_laukuansin.receivers.ConnectivityReceiver;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import cn.pedant.SweetAlert.SweetAlertDialog;
import kr.co.prnd.StepProgressBar;

public class PersonalInformationActivity extends AppCompatActivity implements FillNameFragment.OnReturnNameListener,
        FillGenderFragment.OnReturnGenderListener, FillAgeFragment.OnReturnAgeListener, FillHeightFragment.OnReturnHeightListener,
        FillWeightFragment.OnReturnWeightListener, FillTargetWeightFragment.OnReturnTargetWeightListener,
        FillActivityLevelFragment.OnReturnActivityListener {

    private StepProgressBar _progressBar;
    private int step;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);

        //initialize
        initialization();
    }

    private void initialization() {
        _progressBar = findViewById(R.id.progressBar);
        FrameLayout _frameLayout = findViewById(R.id.frameLayout);

        //initial step is 1
        step = 1;

        //change status bar to gray color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.GRAY);
        }

        //load fill name fragment on the default
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(_frameLayout.getId(), FillNameFragment.newInstance());
        fragmentTransaction.commit();
    }

    @Override
    public void nextStep() {
        //update the step number
        incrementStep();
    }

    private void incrementStep()//increase step
    {
        step += 1;
        _progressBar.setStep(step);
    }

    private void decrementStep()//decrease step
    {
        step -= 1;
        _progressBar.setStep(step);
    }


    @Override
    public void onBackPressed() {
        //when click back button

        //if(step is bigger than 1 only minus 1 and update the step
        if (step > 1) {
            decrementStep();
            super.onBackPressed();
        }
        //disable back button to close the app

    }

    @Override
    public void backPressed() {
        onBackPressed();
    }

    @Override
    public void completed() {//when user fill all information
        //create progress dialog
        SweetAlertDialog _progressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        _progressDialog.setContentText("Loading...");
        _progressDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimary));
        _progressDialog.setCancelable(false);
        _progressDialog.show();

        User user = AppController.getInstance().getSessionHandler().getUser();//get user
        if (!user.getName().isEmpty() && user.getYearOfBirth() != 0 && !user.getGender().isEmpty() && user.getHeight() != 0
                && user.getWeight() != 0 && user.getTargetWeight() != 0 && user.getActivityLevel() != 0)//if all the user personal information is filled
        {
            FirebaseFirestore database = FirebaseFirestore.getInstance();//create database
            if (ConnectivityReceiver.isConnected())//if have wifi connection
            {
                //get the date format
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");//date format example 11 Sep 2021
                //user document path
                String USER_DOCUMENT_PATH = String.format("Users/%1$s", user.getUID());
                //body weight collection path
                String BODY_WEIGHT_COLLECTION_PATH = String.format("BodyWeightRecords/%1$s/Records", user.getUID());

                Map<String, Object> userMap = new HashMap<>();//create hash map to store the user's data
                userMap.put("email", user.getEmailAddress());
                userMap.put("name", user.getName());
                userMap.put("gender", user.getGender());
                userMap.put("yearOfBirth", user.getYearOfBirth());
                userMap.put("height", user.getHeight());
                userMap.put("weight", user.getWeight());
                userMap.put("startWeight", user.getStartWeight());
                userMap.put("targetWeight", user.getTargetWeight());
                userMap.put("activityLevel", user.getActivityLevel());
                userMap.put("dateCreated", dateFormat.format(new Date()));
                user.setDateCreated(dateFormat.format(new Date()));

                Map<String, Object> bodyWeightMap = new HashMap<>();//create hash map to store the body weight's data
                bodyWeightMap.put("bodyWeight", user.getStartWeight());
                bodyWeightMap.put("date", dateFormat.format(new Date()));

                //add initially body weight to database
                database.collection(BODY_WEIGHT_COLLECTION_PATH).add(bodyWeightMap);
                //add user to database
                database.document(USER_DOCUMENT_PATH).set(userMap).addOnSuccessListener(documentReference -> {//if success
                    if (_progressDialog.isShowing())//cancel dialog
                        _progressDialog.dismiss();

                    Intent intent = new Intent(PersonalInformationActivity.this, HomeActivity.class);
                    //remove all previous activity and task
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }).addOnFailureListener(e -> {//if failure
                    if (_progressDialog.isShowing())//cancel dialog
                        _progressDialog.dismiss();

                    createErrorDialog(e.getMessage());
                });
            } else {//have wifi connection
                if (_progressDialog.isShowing())//cancel dialog
                    _progressDialog.dismiss();

                createErrorDialog("No internet connection! Please check your connection and try again.");
            }
        } else {//if one of the user personal information is missing
            //cancel dialog
            if (_progressDialog.isShowing())
                _progressDialog.dismiss();

            createErrorDialog("There are missing some personal information. Please fill in before click done!");
        }
    }

    private void createErrorDialog(String message)//create error dialog
    {
        SweetAlertDialog _errorDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        _errorDialog.setTitleText(R.string.alert_title_error);
        _errorDialog.setContentText(message);
        _errorDialog.setConfirmButton(R.string.alert_ok, sweetAlertDialog -> sweetAlertDialog.dismiss());
        _errorDialog.show();
    }

}
