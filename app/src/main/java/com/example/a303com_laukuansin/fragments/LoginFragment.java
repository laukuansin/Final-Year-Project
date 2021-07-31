package com.example.a303com_laukuansin.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class LoginFragment extends BaseFragment {
    private TextInputLayout _inputEmail,_inputPassword;
    private Button _loginButton;
    private TextView _forgotPasswordView,_signUpView;

    public LoginFragment()
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
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        //initialize
        initialization(view);

        //when click login button action
        _loginButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                checkInput();
            }
        });

        //when click forgot password button action
        _forgotPasswordView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //load forgot password fragment
                loadFragment(new ForgotPasswordFragment());
            }
        });

        //when click sign up button action
        _signUpView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

            }
        });

        return view;
    }
    private void loadFragment(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    private void initialization(View view)
    {
        _inputEmail = view.findViewById(R.id.emailLayout);
        _inputPassword = view.findViewById(R.id.passwordLayout);
        _loginButton = view.findViewById(R.id.loginButton);
        _forgotPasswordView = view.findViewById(R.id.forgotPassword);
        _signUpView = view.findViewById(R.id.signUpButton);

        //change the specific text color
        String signUpText = "Don't have an account? Sign up now";
        SpannableString spannableString = new SpannableString(signUpText);
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        ForegroundColorSpan primaryColor = new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary));
        spannableString.setSpan(primaryColor, 23,34, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(boldSpan,23,34,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        _signUpView.setText(spannableString);

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
        String password = _inputPassword.getEditText().getText().toString().trim();
        if(password.isEmpty())
        {
            _inputPassword.setError("Password cannot be empty!");
            check = false;
        }
        else{
            _inputPassword.setError(null);
        }
        if(check)
        {
            Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
        }
    }
}
