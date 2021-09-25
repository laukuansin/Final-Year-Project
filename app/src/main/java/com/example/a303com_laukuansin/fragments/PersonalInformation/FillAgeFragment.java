package com.example.a303com_laukuansin.fragments.PersonalInformation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.domains.User;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;

public class FillAgeFragment extends BaseFragment {
    private User user;
    private OnReturnAgeListener _listener;
    private NumberPickerView _agePicker;
    private String[] displayedValue;

    public FillAgeFragment() {
        user = getSessionHandler().getUser();//get the user from preferences
    }

    public static FillAgeFragment newInstance() {
        return new FillAgeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fill_age, container, false);

        //initialize
        initialization(view);

        return view;
    }

    private void initialization(View view) {
        //bind view with id
        TextView _nextButton = view.findViewById(R.id.nextButton);
        TextView _backButton = view.findViewById(R.id.backButton);
        _agePicker = view.findViewById(R.id.agePicker);

        //initialize listener
        _listener = (OnReturnAgeListener) getContext();

        //setup the age picker
        setupAgePicker();

        //when back button is clicked action
        _backButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                getCurrentAgePickerValueAndUpdateUser();//get the current age from the number picker
                _listener.backPressed();//back to previous fragment
            }
        });

        //when next button is clicked action
        _nextButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                getCurrentAgePickerValueAndUpdateUser();//get the current age from the number picker
                loadHeightFragment();//load next fragment which is height fragment
                _listener.nextStep();//go to next fragment
            }
        });

        //set animation
        setAnimation();
    }

    private void setupAgePicker() {
        displayedValue = new String[85];//create display values, the age is limit between 15 years old until 99 years old
        for (int i = 0; i < 85; i++) {
            displayedValue[i] = String.valueOf(i + 15);//assign value into string array, array start from 0, so the minimum age is 15, then must +15
        }
        _agePicker.setDisplayedValues(displayedValue);//set string array into number picker view

        //the range between 15 to 99 is 85, so minimum value is 0, maximum value is 84
        _agePicker.setMinValue(0);
        _agePicker.setMaxValue(84);

        //on default the user's year of birth is 0
        if (user.getYearOfBirth() == 0) {
            _agePicker.setValue(15);//set 30 years old for default
            user.setYearOfBirth(Calendar.getInstance().get(Calendar.YEAR) - 30);//set the years of birth
            getSessionHandler().setUser(user);//update user
        } else //if the user has set the year of birth before
        {
            _agePicker.setValue(user.getAge() - 15);//use current year deduct the age to get the user's year of birth
        }
    }

    private void getCurrentAgePickerValueAndUpdateUser()//get current age from the number picker
    {
        user = getSessionHandler().getUser();//get latest user
        _agePicker.stopScrollingAndCorrectPosition();//stop the scrolling the number picker and get correct position
        user.setYearOfBirth(Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt(displayedValue[_agePicker.getValue()]));//set the user's years of birth
        getSessionHandler().setUser(user);//update the user
    }

    private void setAnimation() {
        Animation _slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_animation_shorter);//bottom to up
        _agePicker.setAnimation(_slideUp);
    }

    private void loadHeightFragment()//load height fragment
    {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);//set animation
        fragmentTransaction.replace(R.id.frameLayout, FillHeightFragment.newInstance());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public interface OnReturnAgeListener {
        void backPressed();

        void nextStep();
    }
}
