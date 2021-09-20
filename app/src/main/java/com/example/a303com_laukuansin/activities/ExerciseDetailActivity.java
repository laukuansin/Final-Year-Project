package com.example.a303com_laukuansin.activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.cores.BaseActivity;
import com.example.a303com_laukuansin.domains.Exercise;
import com.example.a303com_laukuansin.fragments.ExerciseDetailFragment;
import com.example.a303com_laukuansin.fragments.ExerciseListFragment;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class ExerciseDetailActivity extends BaseActivity {
    public static final String DATE_KEY = "date_key";
    public static final String EXERCISE_ID_KEY = "exercise_id_key";
    public static final String EXERCISE_RECORD_ID_KEY = "meal_record_id_key";
    private String date;
    private String exerciseID;
    private String exerciseRecordID="";
    private Fragment _fragment;
    @Override
    protected int ContentView() {
        return R.layout.activity_template;
    }

    @Override
    protected boolean RequiredInternetConnection() {
        return true;
    }

    @Override
    protected void AttemptSave() {

    }

    @Override
    protected void AttemptDelete() {

    }

    @Override
    protected void AttemptSearch() {

    }

    @Override
    protected void AttemptAdd() {

    }

    @Override
    protected void AttemptHelp() {

    }

    @Override
    protected int MenuResource() {
        return 0;
    }

    @Override
    protected boolean DisableActionMenu() {
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            if(bundle.containsKey(DATE_KEY))
            {
                date = bundle.getString(DATE_KEY);
            }
            if(bundle.containsKey(EXERCISE_ID_KEY))
            {
                exerciseID = bundle.getString(EXERCISE_ID_KEY);
            }
            if(bundle.containsKey(EXERCISE_RECORD_ID_KEY))
            {
                exerciseRecordID = bundle.getString(EXERCISE_RECORD_ID_KEY);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.deep_orange_900));// set status background dark orange
        }
        //set the default fragment
        if (savedInstanceState == null) {
            _fragment = ExerciseDetailFragment.newInstance(date,exerciseID,exerciseRecordID);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.frame_container, _fragment).commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState,outPersistentState);
        outState.putString(DATE_KEY,date);
        outState.putString(EXERCISE_ID_KEY,exerciseID);
        outState.putString(EXERCISE_RECORD_ID_KEY,exerciseRecordID);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        date = savedInstanceState.getString(DATE_KEY);
        exerciseID = savedInstanceState.getString(EXERCISE_ID_KEY);
        exerciseRecordID = savedInstanceState.getString(EXERCISE_RECORD_ID_KEY);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

}
