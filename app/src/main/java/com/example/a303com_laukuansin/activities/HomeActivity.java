package com.example.a303com_laukuansin.activities;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    public static final String KEY="key";
    private ImageView _imageView;
    private TextView _titleView;
    private Button _signUpButton,_loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Initialize
        initialization();
    }
    private void initialization()
    {
        _imageView = findViewById(R.id.logoImage);
        _titleView = findViewById(R.id.title);
        _signUpButton = findViewById(R.id.signUpButton);
        _loginButton = findViewById(R.id.loginButton);

        //when click login button action
        _loginButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
               intentToLoginSignUpActivity(0);//pass 0
            }
        });

        //when click sign up button action
        _signUpButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
               intentToLoginSignUpActivity(1);//pass 1
            }
        });

        //set animation
        setAnimation();
    }

    private void intentToLoginSignUpActivity(int key)
    {
        Intent intent = new Intent(HomeActivity.this, LoginSignUpActivity.class);
        //create pair to store the view for animation
        Pair[] pairs = new Pair[2];
        pairs[0] = new Pair<View,String>(_imageView,"logoImage");
        pairs[1] = new Pair<View,String>(_titleView,"title");

        intent.putExtra(KEY,key);
        //animate and transition to login page
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(HomeActivity.this,pairs);
            startActivity(intent,options.toBundle());
        }
        else{
            startActivity(intent);
        }
    }

    private void setAnimation()
    {
        Animation _topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);//move from top to bottom
        Animation _bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);//move from bottom to top
        _imageView.setAnimation(_topAnim);
        _titleView.setAnimation(_topAnim);
        _signUpButton.setAnimation(_bottomAnim);
        _loginButton.setAnimation(_bottomAnim);
    }

}
