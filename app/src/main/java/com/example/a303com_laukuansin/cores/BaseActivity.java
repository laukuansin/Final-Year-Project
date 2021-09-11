package com.example.a303com_laukuansin.cores;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.example.a303com_laukuansin.R;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

public abstract class BaseActivity extends BaseAppCompatActivity {
    private static final int PERMISSION_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Handle Toolbar
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    public void isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED||
                    checkSelfPermission(Manifest.permission.BODY_SENSORS)!=PackageManager.PERMISSION_GRANTED)//if storage, camera, or body sensor did not get permission
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,Manifest.permission.BODY_SENSORS},PERMISSION_CODE);//ask permission for storage, camera, and body sensor
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {//if same permission code
            if (grantResults.length > 0)
            {
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED && grantResults[1]!=PackageManager.PERMISSION_GRANTED
                  && grantResults[2] != PackageManager.PERMISSION_GRANTED)//if denied camera, storage, body sensor permission
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
                else if(grantResults[2]!=PackageManager.PERMISSION_GRANTED)//if denied camera permission only
                {
                    Toast.makeText(this, "Body Sensor Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}