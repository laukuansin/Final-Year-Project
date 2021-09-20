package com.example.a303com_laukuansin.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public class ActivityLevelDialogFragment extends DialogFragment {
    private Fragment _parentFragment;

    public ActivityLevelDialogFragment() {
    }
    public void setParentFragment(Fragment fragment) {
        _parentFragment = fragment;
    }

    public static ActivityLevelDialogFragment newInstance(Fragment parentFragment) {
        ActivityLevelDialogFragment fragment = new ActivityLevelDialogFragment();
        fragment.setParentFragment(parentFragment);
        return fragment;
    }

    @Override
    public int getTheme() {
        return R.style.RoundedCornersDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_activity_level, container,false);
        LinearLayout _sedentaryLayout = view.findViewById(R.id.sedentaryLayout);
        LinearLayout _lightlyLayout = view.findViewById(R.id.lightlyLayout);
        LinearLayout _moderatelyLayout = view.findViewById(R.id.moderatelyLayout);
        LinearLayout _veryActiveLayout = view.findViewById(R.id.veryActiveLayout);

         SelectActivityDialogListener _listener = (SelectActivityDialogListener) _parentFragment;

        //when click sedentary
        _sedentaryLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                ActivityLevelDialogFragment.this.dismiss();
                _listener.onReturnActivityLevel(1.2);
            }
        });

        //when click lightly
        _lightlyLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                ActivityLevelDialogFragment.this.dismiss();
                _listener.onReturnActivityLevel(1.375);
            }
        });

        //when click moderately
        _moderatelyLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                ActivityLevelDialogFragment.this.dismiss();
                _listener.onReturnActivityLevel(1.55);
            }
        });

        //when click very active
        _veryActiveLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                ActivityLevelDialogFragment.this.dismiss();
                _listener.onReturnActivityLevel(1.725);
            }
        });

        return view;
    }

    public interface SelectActivityDialogListener {
        void onReturnActivityLevel(double activityLevel);
    }
}
