package com.example.a303com_laukuansin.activities;

import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.cores.BaseActivity;
import com.example.a303com_laukuansin.fragments.BodyWeightDetailFragment;
import com.example.a303com_laukuansin.fragments.ExerciseDetailFragment;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class BodyWeightDetailActivity extends BaseActivity {
    public static final String DATE_KEY = "date_key";
    public static final String BODY_WEIGHT_RECORD_ID_KEY = "body_weight_record_id_key";
    private String date;
    private String bodyWeightRecordID="";
    private Fragment _fragment;
    @Override
    protected int ContentView() {
        return R.layout.activity_template_toolbar;
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
            if(bundle.containsKey(BODY_WEIGHT_RECORD_ID_KEY))
            {
                bodyWeightRecordID = bundle.getString(BODY_WEIGHT_RECORD_ID_KEY);
            }
        }
        //ste toolbar
        setupToolbar();

        //set the default fragment
        if (savedInstanceState == null) {
            _fragment = BodyWeightDetailFragment.newInstance(date,bodyWeightRecordID);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.frame_container, _fragment).commit();
        }
    }

    private void setupToolbar()
    {
        //handle toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.pink_900));// set status background dark pink
        }
        //set back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //set title
        getSupportActionBar().setTitle(date);
        //set background
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.pink_A400));
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState,outPersistentState);
        outState.putString(DATE_KEY,date);
        outState.putString(BODY_WEIGHT_RECORD_ID_KEY,bodyWeightRecordID);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        date = savedInstanceState.getString(DATE_KEY);
        bodyWeightRecordID = savedInstanceState.getString(BODY_WEIGHT_RECORD_ID_KEY);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

}
