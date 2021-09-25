package com.example.a303com_laukuansin.fragments.PersonalInformation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.domains.User;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

public class FillGenderFragment extends BaseFragment {
    private User user;
    private TextView _nextButton;
    private OnReturnGenderListener _listener;
    private LinearLayout _maleLayout, _femaleLayout;
    private String gender = "";

    public FillGenderFragment() {
        user = getSessionHandler().getUser();//get the user from preferences
    }

    public static FillGenderFragment newInstance() {
        return new FillGenderFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fill_gender, container, false);

        //initialize
        initialization(view);

        return view;
    }

    private void initialization(View view) {
        //bind view with id
        _nextButton = view.findViewById(R.id.nextButton);
        TextView _backButton = view.findViewById(R.id.backButton);
        _maleLayout = view.findViewById(R.id.male_layout);
        _femaleLayout = view.findViewById(R.id.female_layout);

        //initial listener
        _listener = (OnReturnGenderListener) getContext();

        if (user.getGender() != null)//if user gender is not empty
        {
            setGender(user.getGender());//set user gender
        }
        //set animation
        setAnimation(view);

        //when back button is clicked
        _backButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                _listener.backPressed();//back listener is called
            }
        });


        //when next button is clicked
        _nextButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                updateGender();//update the gender
                loadAgeFragment();//load the next fragment which is age fragment
                _listener.nextStep();
            }
        });

        //when user is choose male
        _maleLayout.setOnClickListener(v -> {
            setGender("Male");//set gender to male
        });

        //when user is choose female
        _femaleLayout.setOnClickListener(v -> {
            setGender("Female");//set gender to female
        });
    }

    private void loadAgeFragment()//load age fragment
    {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);//set animation
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frameLayout, FillAgeFragment.newInstance());
        fragmentTransaction.commit();
    }

    private void setAnimation(View view) {
        LinearLayout _bottomLayout = view.findViewById(R.id.bottomLayout);
        Animation _slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_animation_shorter);//bottom to up
        _bottomLayout.setAnimation(_slideUp);
    }

    private void setGender(String gender)//set gender
    {
        this.gender = gender;

        //need to update the user
        updateGender();

        if (gender.equals("Male"))//if click male
        {
            _maleLayout.setBackground(getResources().getDrawable(R.drawable.border_primary_color_shadow_layout));
            _femaleLayout.setBackground(getResources().getDrawable(R.drawable.border_grey_shadow_layout));
        } else if (gender.equals("Female")) {//if click female
            _femaleLayout.setBackground(getResources().getDrawable(R.drawable.border_primary_color_shadow_layout));
            _maleLayout.setBackground(getResources().getDrawable(R.drawable.border_grey_shadow_layout));
        }
        //enable next button
        _nextButton.setAlpha(1f);
        _nextButton.setEnabled(true);
    }

    private void updateGender() {
        user = getSessionHandler().getUser();//get latest user
        user.setGender(gender);//set gender
        getSessionHandler().setUser(user);//update gender
    }

    public interface OnReturnGenderListener {
        void backPressed();

        void nextStep();
    }
}
