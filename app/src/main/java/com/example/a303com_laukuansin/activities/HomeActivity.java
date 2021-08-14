package com.example.a303com_laukuansin.activities;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.cores.BaseActivity;
import com.example.a303com_laukuansin.fragments.AccountFragment;
import com.example.a303com_laukuansin.fragments.AnalyticFragment;
import com.example.a303com_laukuansin.fragments.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends BaseActivity {
    public Fragment _fragment;
    private static final int PERMISSION_CODE = 100;

    @Override
    protected int ContentView() {
        return R.layout.activity_bottom_navigation;
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
        isPermissionGranted();//check permission

        initialization();

        if (savedInstanceState == null) {
            _fragment = HomeFragment.newInstance();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.frame_container, _fragment).commit();
        }
    }

    private void initialization()
    {
        //setup bottom navigation bar
        BottomNavigationView btmNavBar = (BottomNavigationView)findViewById(R.id.bottomNavigationBar);
        btmNavBar.getMenu().getItem(0).setEnabled(false);//because on default first item at bottom navigation did not disable

        btmNavBar.setOnNavigationItemSelectedListener(item -> {

            for (int i = 0; i < 3; i++) {
                btmNavBar.getMenu().getItem(i).setEnabled(true);
            }
            switch (item.getItemId()) {
                case R.id.home://when click home page
                    _fragment = HomeFragment.newInstance();
                    btmNavBar.getMenu().getItem(0).setEnabled(false);
                    break;
                case R.id.analytic://when click analytic page
                    _fragment = AnalyticFragment.newInstance();
                    btmNavBar.getMenu().getItem(1).setEnabled(false);
                    break;
                case R.id.account://when click account page
                    _fragment = AccountFragment.newInstance();
                    btmNavBar.getMenu().getItem(2).setEnabled(false);
                    break;
                default:
                    Toast.makeText(HomeActivity.this, "Default frag", Toast.LENGTH_SHORT).show();
                    break;
            }
            btmNavBar.setSelectedItemId(item.getItemId());//set selected item
            return loadFragment(_fragment);
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in,R.anim.fade_out,R.anim.fade_in,R.anim.fade_out);//set animation
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.commit();
            return true;
        }
        return false;
    }

    private void isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED)//if storage or camera did not get permission
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA},PERMISSION_CODE);//ask permission for storage and camera
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {//if same permission code
            if (grantResults.length > 0)
            {
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED && grantResults[1]!=PackageManager.PERMISSION_GRANTED)//if denied camera and storage permission
                {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                else if(grantResults[0] != PackageManager.PERMISSION_GRANTED)//if denied storage permission only
                {
                    Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
                }
                else if(grantResults[1]!=PackageManager.PERMISSION_GRANTED)//if denied camera permission only
                {
                    Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
