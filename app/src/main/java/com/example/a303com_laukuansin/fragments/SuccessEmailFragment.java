package com.example.a303com_laukuansin.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

public class SuccessEmailFragment extends BaseFragment {
    public SuccessEmailFragment() {
    }

    public static SuccessEmailFragment newInstance()
    {
        return new SuccessEmailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_success_email, container, false);

        //initialize
        initialization(view);
        return view;
    }

    private void initialization(View view)
    {
        Button _loginButton = view.findViewById(R.id.loginButton);

        //when click login button action
        _loginButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //pop fragment in stack
                getActivity().getSupportFragmentManager().popBackStack();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout,LoginFragment.newInstance());
                fragmentTransaction.commit();
            }
        });
    }
}
