package com.example.a303com_laukuansin.activities;

import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.adapters.ExerciseListAdapter;
import com.example.a303com_laukuansin.cores.BaseActivity;
import com.example.a303com_laukuansin.domains.Exercise;
import com.example.a303com_laukuansin.fragments.ExerciseListFragment;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class ExerciseListActivity extends BaseActivity implements ExerciseListAdapter.OnActionListener {
    public static final String DATE_KEY = "date_key";
    private String date;
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
    protected void AttemptFilter() {
        ((ExerciseListFragment)_fragment).openFilterDialog();
    }

    @Override
    protected int MenuResource() {
        return R.menu.filter_only;
    }

    @Override
    protected boolean DisableActionMenu() {
        return false;
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
        }
        setupToolbar();

        //set the default fragment
        if (savedInstanceState == null) {
            _fragment = ExerciseListFragment.newInstance(date);
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
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.deep_orange_900));// set status background dark orange
        }
        //set back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //set background
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.yellow_900));
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState,outPersistentState);
        outState.putString(DATE_KEY,date);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        date=savedInstanceState.getString(DATE_KEY);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    @Override
    public void selectExercise(Exercise exercise) {
        ((ExerciseListFragment)_fragment).selectExercise(exercise);
    }
}
