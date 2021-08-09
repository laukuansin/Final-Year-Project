package com.example.a303com_laukuansin.activities;

import android.os.Bundle;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.cores.AppController;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        AppController.getInstance().getSessionHandler().checkAuthorization();//check authorization, determine which activity to go
    }
}
