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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class LoginSignUpActivity extends AppCompatActivity{
    private FrameLayout _frameLayout;
    private int type;
    private FragmentTransaction fragmentTransaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide the action bar
        this.getSupportActionBar().hide();
        setContentView(R.layout.activity_login_signup);

        //initialize
        initialization();
        //received data from splash screen activity
        receivedData();
        //set the Fragment
        setFragment();
        //set animation
        setAnimation();


    }

    private void initialization()
    {
        _frameLayout = findViewById(R.id.frameLayout);
    }
    private void setFragment()
    {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(type==0)
        {
            fragmentTransaction.add(R.id.frameLayout,new LoginFragment());
            fragmentTransaction.commit();
        }
        else if(type==1)
        {

        }
        else
        {
            Toast.makeText(this, "Error occurs: Cannot received previous activity data", Toast.LENGTH_SHORT).show();
        }
    }
    private void receivedData()
    {
        Intent intent = getIntent();
        type = intent.getIntExtra(SplashScreenActivity.KEY,-1);
    }

    private void setAnimation()
    {
        Animation _bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation_faster);
        _frameLayout.setAnimation(_bottomAnim);
    }

    @Override
    public void onBackPressed() {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frameLayout);
        //check the fragment is ForgotPasswordFragment
        if(fragment instanceof ForgotPasswordFragment)
        {
            getSupportFragmentManager().popBackStack();
        }
        else{
            super.onBackPressed();
        }
    }
}
