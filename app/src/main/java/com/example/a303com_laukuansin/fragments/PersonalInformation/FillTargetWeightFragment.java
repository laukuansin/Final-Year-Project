package com.example.a303com_laukuansin.fragments.PersonalInformation;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;

public class FillTargetWeightFragment extends BaseFragment{
    private User user;
    private OnReturnTargetWeightListener _listener;
    private NumberPickerView _weightPicker,_weightDecimalPicker;
    private String []displayedValueWeight,displayedValueWeightDecimal;
    private double BMI;
    private int minimumIdealWeight,maximumIdealWeight;

    public FillTargetWeightFragment() {
        user = getSessionHandler().getUser();//get the user from preferences
        BMI = user.getBMI();//get BMI
        minimumIdealWeight = user.getMinimumIdealWeight();//get Minimum Ideal weight
        maximumIdealWeight = user.getMaximumIdealWeight();//get Maximum Ideal weight
    }
    public static FillTargetWeightFragment newInstance()
    {
        return new FillTargetWeightFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_fill_target_weight, container, false);

        //initialize
        initialization(view);

        return view;
    }

    private void initialization(View view)
    {
        //bind view with id
        TextView _nextButton = view.findViewById(R.id.nextButton);
        TextView _backButton = view.findViewById(R.id.backButton);
        TextView _BMIView = view.findViewById(R.id.BMIView);
        TextView _idealRangeView = view.findViewById(R.id.idealWeightRangeView);
        _weightPicker = view.findViewById(R.id.weightPicker);
        _weightDecimalPicker = view.findViewById(R.id.weightDecimalPicker);

        _listener = (OnReturnTargetWeightListener) getContext();

        //setup the weight picker
        setupWeightPicker();
        //setup the weight decimal picker
        setupWeightDecimalPicker();

        _BMIView.append(String.valueOf(BMI));
        _idealRangeView.setText(String.format("%1$d-%2$d KG",minimumIdealWeight,maximumIdealWeight));

        //when back button is clicked action
        _backButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                getCurrentTargetWeightPickerValueAndUpdateUser();//get the current weight from the weight picker
                _listener.backPressed();//back to previous fragment
            }
        });

        //when next button is clicked action
        _nextButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                getCurrentTargetWeightPickerValueAndUpdateUser();//get the current weight from the weight picker
                loadActivityLevelFragment();//load next fragment which is activity level fragment
                _listener.nextStep();//go to next fragment
            }
        });
        //when click ideal body weight
        _idealRangeView.setOnClickListener(v -> {
            createBottomDialog();//pop out bottom dialog
        });

        //set animation
        setAnimation(view);
    }

    private void createBottomDialog()
    {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(),R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.bottom_dialog_bmi);
        TextView _BMIView = bottomSheetDialog.findViewById(R.id.dialogBMIView);

        String category="";
        if(BMI<18.5)//if BMI less than 18.5
        {
            category = "Underweight";
        }
        else if(BMI<25)//if BMI in between 18.5 to 24.9
        {
            category = "Ideal";
        }
        else if(BMI<30)//if BMI in between 25 to 29.9
        {
            category = "Overweight";
        }
        else// else BMI is bigger or equal to 30
        {
            category = "Obesity";
        }
        String BMIText= String.format("According to National Institutes of Health, your BMI is %.1f, which is consider as %2$s",BMI,category);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                //link to the NIH article
                String url = "https://www.nih.gov/news-events/news-releases/nih-study-identifies-ideal-body-mass-index";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(getResources().getColor(R.color.colorPrimary));
            }
        };
        SpannableString spannableString = new SpannableString(BMIText);
        spannableString.setSpan(clickableSpan, 0,42, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        _BMIView.setText(spannableString);
        _BMIView.setMovementMethod(LinkMovementMethod.getInstance());
        _BMIView.setHighlightColor(Color.TRANSPARENT);

        bottomSheetDialog.show();
    }

    private void setupWeightPicker()
    {
        displayedValueWeight = new String[271];//create display values for weight, the weight is limit between 30 KG until 300 KG
        for(int i=0;i<271;i++)
        {
            displayedValueWeight[i] = String.valueOf(i+30);//assign value into string array, array start from 0, so the minimum weight is 30, then must +30
        }
        _weightPicker.setDisplayedValues(displayedValueWeight);//set string array into number picker view
        //the range between 30 to 300 is 270, so minimum value is 0, maximum value is 270
        _weightPicker.setMinValue(0);
        _weightPicker.setMaxValue(270);

        //on default the user's target weight is 0
        if(user.getTargetWeight()==0)
        {
            _weightPicker.setValue(minimumIdealWeight-30);//set minimum ideal weight for default
            user.setTargetWeight(Double.parseDouble(displayedValueWeight[minimumIdealWeight-30]));
            getSessionHandler().setUser(user);//update user
        }
        else//if the user has set the target weight before
        {
            _weightPicker.setValue((int)Math.floor(user.getTargetWeight()-30));//use Math.floor is because get the front number
        }
    }

    private void setupWeightDecimalPicker()//setup weight decimal picker
    {
        displayedValueWeightDecimal = new String[10];//create display values for weight decimal, the weight is limit between 0 until 9
        for(int i=0;i<10;i++)
        {
            displayedValueWeightDecimal[i] = String.valueOf(i);//assign value into string array
        }
        _weightDecimalPicker.setDisplayedValues(displayedValueWeightDecimal);//set string array into number picker view
        _weightDecimalPicker.setMinValue(0);
        _weightDecimalPicker.setMaxValue(9);

        //on default the user's target weight is 0
        if(user.getWeight()==0)
        {
            _weightDecimalPicker.setValue(0);//set 0 for default
        }
        else
        {
            //if the user has set the target weight before
            int decimalNum = (int)((user.getTargetWeight()*10)-(Math.floor(user.getTargetWeight())*10)); //to get the decimal number
            _weightDecimalPicker.setValue(decimalNum);//set decimal value
        }
    }

    private void loadActivityLevelFragment()
    {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left, R.anim.slide_out_right);//set animation
        fragmentTransaction.replace(R.id.frameLayout,FillActivityLevelFragment.newInstance());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void setAnimation(View view)
    {
        TextView _units =view.findViewById(R.id.units);
        Animation _slideUp = AnimationUtils.loadAnimation(getContext(),R.anim.bottom_animation_shorter);//bottom to up

        _weightPicker.setAnimation(_slideUp);
        _weightDecimalPicker.setAnimation(_slideUp);
        _units.setAnimation(_slideUp);
    }

    private void getCurrentTargetWeightPickerValueAndUpdateUser()
    {
        user = getSessionHandler().getUser();
        _weightPicker.stopScrollingAndCorrectPosition();//stop the scrolling the number picker and get correct position
        _weightDecimalPicker.stopScrollingAndCorrectPosition();//stop the scrolling the number picker and get correct position
        double frontNumber = Double.parseDouble(displayedValueWeight[_weightPicker.getValue()]);//get the front number.
        double decimalNumber = Double.parseDouble(displayedValueWeightDecimal[_weightDecimalPicker.getValue()]);//get decimal number.
        user.setTargetWeight(frontNumber+(decimalNumber/10));//set weight
        getSessionHandler().setUser(user);//update user
    }

    public interface OnReturnTargetWeightListener
    {
        void backPressed();
        void nextStep();
    }

}

