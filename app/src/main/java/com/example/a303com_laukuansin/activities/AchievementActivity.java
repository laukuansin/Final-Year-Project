package com.example.a303com_laukuansin.activities;

import android.os.Build;
import android.os.Bundle;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.cores.BaseActivity;
import com.example.a303com_laukuansin.fragments.AchievementFragment;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class AchievementActivity extends BaseActivity {
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

        //set toolbar
        setupToolbar();

        //set the default fragment
        if (savedInstanceState == null) {
            Fragment _fragment = AchievementFragment.newInstance();
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
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));// set status background dark primary
        }
        //set back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //set elevation to 0
        getSupportActionBar().setElevation(0);
        //set background
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.colorPrimary));
    }

    @Override
    public void finish() {
        super.finish();
        //when the activity is finish, add slide left to right animation
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}
