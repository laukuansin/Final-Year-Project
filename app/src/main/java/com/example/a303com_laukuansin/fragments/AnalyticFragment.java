package com.example.a303com_laukuansin.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.cores.BaseFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AnalyticFragment extends BaseFragment{
    public AnalyticFragment() {
    }
    public static AnalyticFragment newInstance()
    {
        return new AnalyticFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analytic, container, false);
        return view;
    }
}