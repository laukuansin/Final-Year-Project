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

public class FillActivityLevelFragment extends BaseFragment{
    private User user;
    private OnReturnActivityListener _listener;
    private LinearLayout _sedentaryLayout,_lightlyLayout,_moderatelyLayout,_veryActiveLayout;
    private double activityLevel = 0;
    private TextView _doneButton;

    public FillActivityLevelFragment() {
        user = getSessionHandler().getUser();//get the user from preferences
    }
    public static FillActivityLevelFragment newInstance()
    {
        return new FillActivityLevelFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_fill_activity_level, container, false);

        //initialize
        initialization(view);

        return view;
    }
    private void initialization(View view)
    {
        _doneButton = view.findViewById(R.id.doneButton);
        TextView _backButton = view.findViewById(R.id.backButton);
        _listener = (OnReturnActivityListener) getContext();
        _sedentaryLayout = view.findViewById(R.id.sedentaryLayout);
        _lightlyLayout = view.findViewById(R.id.lightlyLayout);
        _moderatelyLayout = view.findViewById(R.id.moderatelyLayout);
        _veryActiveLayout =view.findViewById(R.id.veryActiveLayout);

        if(user.getActivityLevel()!=0)//if the activity is not 0 means user choose activity level before
        {
            setActivityLevel(user.getActivityLevel());//set activity level
        }

        //when back button is clicked action
        _backButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                _listener.backPressed();
            }
        });

        //when done button is clicked
        _doneButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                _listener.completed();//done fill in personal information
            }
        });
        //set animation
        setAnimation(view);


        //when user is choose sedentary
        _sedentaryLayout.setOnClickListener(v -> {
            setActivityLevel(1.2);
        });

        //when user is choose lightly active
        _lightlyLayout.setOnClickListener(v -> {
            setActivityLevel(1.375);
        });

        //when user is choose moderately active
        _moderatelyLayout.setOnClickListener(view1 -> {
            setActivityLevel(1.55);
        });

        //when user is choose very active
        _veryActiveLayout.setOnClickListener(view12 -> {
            setActivityLevel(1.725);
        });
    }

    private void setActivityLevel(double level)
    {
        this.activityLevel = level;//set level

        //update activity level
        updateActivityLevel();

        //on the default set all layout to grey
        _sedentaryLayout.setBackground(getResources().getDrawable(R.drawable.border_grey_rectangle_shadow_layout));
        _lightlyLayout.setBackground(getResources().getDrawable(R.drawable.border_grey_rectangle_shadow_layout));
        _moderatelyLayout.setBackground(getResources().getDrawable(R.drawable.border_grey_rectangle_shadow_layout));
        _veryActiveLayout.setBackground(getResources().getDrawable(R.drawable.border_grey_rectangle_shadow_layout));

        //once user click which layout, only update the background
        if(activityLevel==1.2)//if click sedentary
        {
            _sedentaryLayout.setBackground(getResources().getDrawable(R.drawable.border_primary_color_rectangle_shadow_layout));
        }
        else if(activityLevel==1.375)//if click lightly active
        {
            _lightlyLayout.setBackground(getResources().getDrawable(R.drawable.border_primary_color_rectangle_shadow_layout));
        }
        else if(activityLevel==1.55)//if click moderately active
        {
            _moderatelyLayout.setBackground(getResources().getDrawable(R.drawable.border_primary_color_rectangle_shadow_layout));
        }
        else if(activityLevel==1.725)//if click very active
        {
            _veryActiveLayout.setBackground(getResources().getDrawable(R.drawable.border_primary_color_rectangle_shadow_layout));
        }

        //enable button
        _doneButton.setAlpha(1f);
        _doneButton.setEnabled(true);
    }

    private void updateActivityLevel()//update activity level
    {
        user = getSessionHandler().getUser();
        user.setActivityLevel(activityLevel);
        getSessionHandler().setUser(user);
    }

    private void setAnimation(View view)
    {
        LinearLayout _bottomLayout = view.findViewById(R.id.bottomLayout);
        Animation _slideUp = AnimationUtils.loadAnimation(getContext(),R.anim.bottom_animation_shorter);//bottom to up

        _bottomLayout.setAnimation(_slideUp);
    }

    public interface OnReturnActivityListener
    {
        void backPressed();
        void completed();
    }
}
