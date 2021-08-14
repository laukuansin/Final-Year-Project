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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;

public class FillHeightFragment extends BaseFragment{
    private User user;
    private OnReturnHeightListener _listener;
    private NumberPickerView _heightPicker;
    private String []displayedValue;


    public FillHeightFragment() {
        user = getSessionHandler().getUser();//get the user from preferences
    }
    public static FillHeightFragment newInstance()
    {
        return new FillHeightFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_fill_height, container, false);

        //initialize
        initialization(view);
        return view;
    }
    private void initialization(View view)
    {
        TextView _nextButton = view.findViewById(R.id.nextButton);
        TextView _backButton = view.findViewById(R.id.backButton);
        _listener = (OnReturnHeightListener) getContext();

        setupHeightPicker(view);

        _backButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                getCurrentHeightPickerValueAndUpdateUser();
                _listener.backPressed();
            }
        });
        _nextButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                getCurrentHeightPickerValueAndUpdateUser();
                loadWeightFragment();
                _listener.nextStep();
            }
        });
        setAnimation();
    }
    private void setupHeightPicker(View view)
    {
        _heightPicker = view.findViewById(R.id.heightPicker);
        displayedValue = new String[171];//create display values
        for(int i=0;i<171;i++)
        {
            displayedValue[i] = String.valueOf(i+100);//assign value into string array
        }
        _heightPicker.setDisplayedValues(displayedValue);//set string array into number picker view
        _heightPicker.setMinValue(0);
        _heightPicker.setMaxValue(170);

        //on default the user's height is 0
        if(user.getHeight()==0)
        {
            _heightPicker.setValue(50);//set 50 for default
            user.setHeight(Integer.parseInt(displayedValue[50]));
            getSessionHandler().setUser(user);
        }
        else
        {
            //if the user has set the height before
            _heightPicker.setValue(user.getHeight()-100);
        }
    }
    private void setAnimation()
    {
        Animation _slideUp = AnimationUtils.loadAnimation(getContext(),R.anim.bottom_animation_shorter);//bottom to up
        _heightPicker.setAnimation(_slideUp);

    }
    private void getCurrentHeightPickerValueAndUpdateUser()
    {
        user = getSessionHandler().getUser();
        _heightPicker.stopScrollingAndCorrectPosition();//stop the scrolling the number picker and get correct position
        user.setHeight(Integer.parseInt(displayedValue[_heightPicker.getValue()]));
        getSessionHandler().setUser(user);
    }

    private void loadWeightFragment()
    {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left, R.anim.slide_out_right);//set animation
        fragmentTransaction.replace(R.id.frameLayout,FillWeightFragment.newInstance());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    public interface OnReturnHeightListener
    {
        void backPressed();
        void nextStep();
    }
}
