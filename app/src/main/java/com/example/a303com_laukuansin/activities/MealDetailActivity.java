package com.example.a303com_laukuansin.activities;

import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.cores.BaseActivity;
import com.example.a303com_laukuansin.fragments.MealDetailFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MealDetailActivity extends BaseActivity {
    public static final String DATE_KEY = "date_key";
    public static final String MEAL_TYPE_KEY = "meal_type_key";
    public static final String FOOD_NAME_KEY = "food_name_key";
    public static final String FOOD_ID_KEY = "food_id_key";
    public static final String MEAL_RECORD_ID_KEY = "meal_record_id_key";
    private String mealType;
    private String date;
    private String foodName = "";
    private String foodID = "";
    private String mealRecordID = "";

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
    protected void AttemptFilter() {

    }

    @Override
    protected void AttemptRefresh() {

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
            if(bundle.containsKey(MEAL_TYPE_KEY))
            {
                mealType = bundle.getString(MEAL_TYPE_KEY);
            }
            if(bundle.containsKey(FOOD_NAME_KEY))
            {
                foodName = bundle.getString(FOOD_NAME_KEY);
            }
            if(bundle.containsKey(FOOD_ID_KEY))
            {
                foodID = bundle.getString(FOOD_ID_KEY);
            }
            if(bundle.containsKey(MEAL_RECORD_ID_KEY))
            {
                mealRecordID = bundle.getString(MEAL_RECORD_ID_KEY);
            }
        }

        setToolbar();

        //set the default fragment
        if (savedInstanceState == null) {
            Fragment _fragment = MealDetailFragment.newInstance(date, mealType,foodName,foodID,mealRecordID);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.frame_container, _fragment).commit();
        }
    }

    private void setToolbar()
    {
        //handle toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.green_700));// set status background dark green
        }
        //set back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //set toolbar color
        toolbar.setBackgroundColor(getResources().getColor(R.color.green_A700));
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState,outPersistentState);
        outState.putString(DATE_KEY,date);
        outState.putString(MEAL_TYPE_KEY, mealType);
        outState.putString(FOOD_NAME_KEY, foodName);
        outState.putString(FOOD_ID_KEY, foodID);
        outState.putString(MEAL_RECORD_ID_KEY, mealRecordID);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        date = savedInstanceState.getString(DATE_KEY);
        mealType = savedInstanceState.getString(MEAL_TYPE_KEY);
        foodName = savedInstanceState.getString(FOOD_NAME_KEY);
        foodID = savedInstanceState.getString(FOOD_ID_KEY);
        mealRecordID = savedInstanceState.getString(MEAL_RECORD_ID_KEY);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}
