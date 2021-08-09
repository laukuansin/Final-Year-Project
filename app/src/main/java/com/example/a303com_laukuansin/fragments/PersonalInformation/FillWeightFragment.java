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
import cn.carbswang.android.numberpickerview.library.NumberPickerView;

public class FillWeightFragment extends BaseFragment{
    private User user;
    private OnReturnWeightListener _listener;
    private NumberPickerView _weightPicker,_weightDecimalPicker;
    private String []displayedValueWeight,displayedValueWeightDecimal;

    public FillWeightFragment() {
        user = getSessionHandler().getUser();//get the user from preferences
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_fill_weight, container, false);

        //initialize
        initialization(view);

        return view;
    }

    private void initialization(View view)
    {
        TextView _nextButton = view.findViewById(R.id.nextButton);
        TextView _backButton = view.findViewById(R.id.backButton);
        _listener = (OnReturnWeightListener) getContext();

        //setup the weight picker
        setupWeightPicker(view);

        //setup the weight decimal picker
        setupWeightDecimalPicker(view);

        //when back button is clicked action
        _backButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                getCurrentWeightPickerValueAndUpdateUser();//get the current weight from the weight picker
                _listener.backPressed();//back to previous fragment
            }
        });

        //when next button is clicked action
        _nextButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                getCurrentWeightPickerValueAndUpdateUser();//get the current weight from the weight picker
                loadTargetWeightFragment();//load next fragment which is target weight fragment
                _listener.nextStep();//go to next fragment
            }
        });

        ///set animation
        setAnimation(view);

    }

    private void setupWeightPicker(View view)//setup weight picker
    {
        _weightPicker = view.findViewById(R.id.weightPicker);

        displayedValueWeight = new String[271];//create display values for weight, the weight is limit between 30 KG until 300 KG
        for(int i=0;i<271;i++)
        {
            displayedValueWeight[i] = String.valueOf(i+30);//assign value into string array, array start from 0, so the minimum weight is 30, then must +30
        }
        _weightPicker.setDisplayedValues(displayedValueWeight);//set string array into number picker view

        //the range between 30 to 300 is 270, so minimum value is 0, maximum value is 270
        _weightPicker.setMinValue(0);
        _weightPicker.setMaxValue(270);

        //on default the user's weight is 0
        if(user.getWeight()==0)
        {
            _weightPicker.setValue(20);//set 50 KG for default
            user.setWeight(Double.parseDouble(displayedValueWeight[20]));//set weight
            getSessionHandler().setUser(user);//update user
        }
        else//if the user has set the weight before
        {
            _weightPicker.setValue((int)Math.floor(user.getWeight()-30));//use Math.floor is because get the front number. E.g. 54.8, Math.floor-> 54
        }
    }

    private void setupWeightDecimalPicker(View view)//setup weight decimal picker
    {
        _weightDecimalPicker = view.findViewById(R.id.weightDecimalPicker);

        displayedValueWeightDecimal = new String[10];//create display values for weight decimal, the weight is limit between 0 until 9
        for(int i=0;i<10;i++)
        {
            displayedValueWeightDecimal[i] = String.valueOf(i);//assign value into string array
        }
        _weightDecimalPicker.setDisplayedValues(displayedValueWeightDecimal);//set string array into number picker view

        _weightDecimalPicker.setMinValue(0);
        _weightDecimalPicker.setMaxValue(9);

        //on default the user's weight is 0
        if(user.getWeight()==0)
        {
            _weightDecimalPicker.setValue(0);//set 0 for default
        }
        else
        {
            //if the user has set the weight before
            int decimalNum = (int)((user.getWeight()*10)-(Math.floor(user.getWeight())*10)); //to get the decimal number. E.g. 54.8= 548 - 540 = 8
            _weightDecimalPicker.setValue(decimalNum);//set decimal value
        }
    }
    private void setAnimation(View view)
    {
        LinearLayout _upperLayout = view.findViewById(R.id.upperLayout);
        TextView _units =view.findViewById(R.id.units);
        Animation _slideLeft = AnimationUtils.loadAnimation(getContext(),R.anim.slide_in_right);//right to left
        Animation _slideUp = AnimationUtils.loadAnimation(getContext(),R.anim.bottom_animation_shorter);//bottom to up

        _weightPicker.setAnimation(_slideUp);
        _weightDecimalPicker.setAnimation(_slideUp);
        _units.setAnimation(_slideUp);
        _upperLayout.setAnimation(_slideLeft);
    }

    private void getCurrentWeightPickerValueAndUpdateUser()
    {
        user = getSessionHandler().getUser();//get latest user
        _weightPicker.stopScrollingAndCorrectPosition();//stop the scrolling the number picker and get correct position
        _weightDecimalPicker.stopScrollingAndCorrectPosition();//stop the scrolling the number picker and get correct position
        double frontNumber = Double.parseDouble(displayedValueWeight[_weightPicker.getValue()]);//get the front number. E.g. (54).8
        double decimalNumber = Double.parseDouble(displayedValueWeightDecimal[_weightDecimalPicker.getValue()]);//get decimal number. E.g. 54.(8)
        user.setWeight(frontNumber+(decimalNumber/10));//set weight. E.g. 54+(8/10)
        getSessionHandler().setUser(user);//update user
    }

    public interface OnReturnWeightListener
    {
        void backPressed();
        void nextStep();
    }
    private void loadTargetWeightFragment()
    {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,new FillTargetWeightFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
