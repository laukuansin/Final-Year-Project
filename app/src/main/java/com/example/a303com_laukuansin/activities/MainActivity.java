package com.example.a303com_laukuansin.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.cores.BaseActivity;
import com.example.a303com_laukuansin.fragments.MainFragment;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;

public class MainActivity extends BaseActivity {
    public Fragment _fragment;
    private ActionBar actionBar;
    @Override
    protected int ContentView() {
        return R.layout.activity_logged_template;
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
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        if (savedInstanceState == null ) {
            _fragment = MainFragment.newInstance();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.frame_container, _fragment).commit();

        }
        //initialize
       // initialization();

//        _logout.setOnClickListener(new OnSingleClickListener() {
//            @Override
//            public void onSingleClick(View v) {
//
//            }
//        });
    }


//    private void initialization()
//    {
//        _logout = findViewById(R.id.logout);
//    }

}
