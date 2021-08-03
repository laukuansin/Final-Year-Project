package com.example.a303com_laukuansin.activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.cores.AppController;
import com.example.a303com_laukuansin.domains.User;
import com.example.a303com_laukuansin.fragments.FillGenderFragment;
import com.example.a303com_laukuansin.fragments.FillNameFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import kr.co.prnd.StepProgressBar;

public class PersonalInformationActivity extends AppCompatActivity implements FillNameFragment.OnReturnNameListener {
    public static final String KEY_USER="key-user";
    private StepProgressBar _progressBar;
    private FragmentTransaction fragmentTransaction;
    private FrameLayout _frameLayout;
    private User user;
    private int step;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);

        //initialize
        initialization();
    }

    private void initialization()
    {
        _progressBar = findViewById(R.id.progressBar);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        _frameLayout = findViewById(R.id.frameLayout);

        //initial step is 1
        step = 1;

        user = AppController.getInstance().getSessionHandler().getUser();
        //change status bar to gray color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.GRAY);
        }

        //load fill name fragment on the default
        loadFragment(new FillNameFragment(user));
    }

    private void loadFragment(Fragment fragment)
    {
        fragmentTransaction.add(_frameLayout.getId(),fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void returnUserWithName(User user) {
        this.user = user;
        _progressBar.setStep(step+1);
        loadFragment(new FillGenderFragment(user));
    }
}
