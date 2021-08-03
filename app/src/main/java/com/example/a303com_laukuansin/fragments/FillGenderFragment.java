package com.example.a303com_laukuansin.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.activities.PersonalInformationActivity;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.domains.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FillGenderFragment extends BaseFragment{
    private User user;
    public FillGenderFragment(User user) {
        Bundle args = new Bundle();
        args.putSerializable(PersonalInformationActivity.KEY_USER,user);
        setArguments(args);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(PersonalInformationActivity.KEY_USER);
        }
        setHasOptionsMenu(false);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_fill_name, container, false);

        //initialize
        initialization(view);

        return view;

    }

    private void initialization(View view)
    {

    }
}
