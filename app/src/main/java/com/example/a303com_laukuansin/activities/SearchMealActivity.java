package com.example.a303com_laukuansin.activities;

import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.cores.BaseActivity;
import com.example.a303com_laukuansin.fragments.SearchMealFragment;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class SearchMealActivity extends BaseActivity {
    public static final String DATE_KEY = "date_key";
    public static final String MEAL_TYPE_KEY = "meal_type_key";
    private String mealType;
    private String date;

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

    }

    @Override
    protected int MenuResource() {
        return R.menu.search_only;
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
            if(bundle.containsKey(MEAL_TYPE_KEY))
            {
                mealType = bundle.getString(MEAL_TYPE_KEY);
            }
        }

        setupToolbar();


        //set the default fragment
        if (savedInstanceState == null) {
            Fragment _fragment = SearchMealFragment.newInstance(date, mealType);
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
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.green_700));// set status background dark green
        }
        //set back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //set background
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.green_A700));

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState,outPersistentState);
        outState.putString(DATE_KEY,date);
        outState.putString(MEAL_TYPE_KEY, mealType);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        date = savedInstanceState.getString(DATE_KEY);
        mealType = savedInstanceState.getString(MEAL_TYPE_KEY);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        hideSoftKeyboard();
    }
}
