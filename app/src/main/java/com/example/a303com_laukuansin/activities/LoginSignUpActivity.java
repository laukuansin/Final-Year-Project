package com.example.a303com_laukuansin.activities;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.fragments.ForgotPasswordFragment;
import com.example.a303com_laukuansin.fragments.LoginFragment;
import com.example.a303com_laukuansin.fragments.SignUpFragment;
import com.example.a303com_laukuansin.fragments.SuccessEmailFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class LoginSignUpActivity extends AppCompatActivity{
    private FrameLayout _frameLayout;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);

        //initialize
        initialization();
    }

    private void initialization()
    {
        _frameLayout = findViewById(R.id.frameLayout);

        //received data from splash screen activity
        receivedData();
        //set the Fragment
        setFragment();
        //set animation
        setAnimation();
    }

    private void setFragment()
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //set fragment when type is 0, Go to login page
        if(type==0)
        {
            fragmentTransaction.add(_frameLayout.getId(),LoginFragment.newInstance());
            fragmentTransaction.commit();
        }
        else if(type==1)//set fragment when type is 1, Go to Sign Up Fragment
        {
            fragmentTransaction.add(_frameLayout.getId(),SignUpFragment.newInstance());
            fragmentTransaction.commit();
        }
        else//When did not received the data
        {
            Toast.makeText(this, "Error occurs: Cannot received previous activity data", Toast.LENGTH_SHORT).show();
        }
    }

    private void receivedData()//received data
    {
        Intent intent = getIntent();
        type = intent.getIntExtra(MainActivity.KEY,-1);//set to -1 if did not received data
    }

    private void setAnimation()//set animation
    {
        Animation _bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation_faster);
        _frameLayout.setAnimation(_bottomAnim);//set animation
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frameLayout);
        //check the fragment is ForgotPasswordFragment or success email fragment
        if(fragment instanceof ForgotPasswordFragment||fragment instanceof SuccessEmailFragment)
        {
            getSupportFragmentManager().popBackStack();
        }
        else{
            super.onBackPressed();
        }
    }
}
