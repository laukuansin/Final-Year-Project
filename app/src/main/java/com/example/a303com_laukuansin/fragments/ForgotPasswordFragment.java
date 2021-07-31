package com.example.a303com_laukuansin.fragments;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ForgotPasswordFragment extends BaseFragment {
    private TextInputLayout _inputEmail;
    private Button _submitButton;
    public ForgotPasswordFragment()
    {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        //initialize
        initialization(view);

        //when click submit button action
        _submitButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                checkInput();
            }
        });

        return view;
    }

    private void initialization(View view)
    {
        _inputEmail = view.findViewById(R.id.emailLayout);
        _submitButton = view.findViewById(R.id.submitButton);
    }

    private void checkInput()
    {
        String email = _inputEmail.getEditText().getText().toString().trim();
        boolean check = true;
        if(email.isEmpty())
        {
            _inputEmail.setError("Email address cannot be empty!");
            check = false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            _inputEmail.setError("Invalid Email address format!");
            check = false;
        }
        else{
            _inputEmail.setError(null);
        }

        if(check)
        {
            Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
        }
    }
}
