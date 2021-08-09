package com.example.a303com_laukuansin.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.domains.User;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginFragment extends BaseFragment {
    private TextInputLayout _inputEmail,_inputPassword;
    private TextView _signUpView;
    private FirebaseAuth auth;
    private SweetAlertDialog _progressDialog;

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


        return view;
    }
    private void loadFragment(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        if(fragment instanceof ForgotPasswordFragment)
            fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void initialization(View view)
    {
        _inputEmail = view.findViewById(R.id.emailLayout);
        _inputPassword = view.findViewById(R.id.passwordLayout);
        Button _loginButton = view.findViewById(R.id.loginButton);
        TextView _forgotPasswordView = view.findViewById(R.id.forgotPassword);
        _signUpView = view.findViewById(R.id.signUpButton);

        //get auth instance
        auth = FirebaseAuth.getInstance();


        //create progress dialog
        _progressDialog = new SweetAlertDialog(getContext(),SweetAlertDialog.PROGRESS_TYPE);
        _progressDialog.setContentText("Logging in...");
        _progressDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimary));
        _progressDialog.setCancelable(false);


        //when click login button action
        _loginButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                //check input
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

        //change the few text color in textview
        setTextViewClickAndStyle();

    }
    private void setTextViewClickAndStyle()
    {
        //change the specific text color and bold
        String signUpText = "Don't have an account? Sign up now";
        ClickableSpan clickableSignUp = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                loadFragment(new SignUpFragment());
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        SpannableString spannableString = new SpannableString(signUpText);
        spannableString.setSpan(clickableSignUp, 23,34, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        _signUpView.setText(spannableString);
        _signUpView.setMovementMethod(LinkMovementMethod.getInstance());
        _signUpView.setHighlightColor(Color.TRANSPARENT);
    }

    private void checkInput()
    {
        String email = _inputEmail.getEditText().getText().toString().trim();//get email
        boolean check = true;
        if(email.isEmpty())// if email is empty
        {
            _inputEmail.setError("Email address cannot be empty!");
            check = false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())//if email does not meet the format
        {
            _inputEmail.setError("Invalid Email address format!");
            check = false;
        }
        else{//else email did not have any error
            _inputEmail.setError(null);
        }
        String password = _inputPassword.getEditText().getText().toString().trim();//get password
        if(password.isEmpty())//if password is empty
        {
            _inputPassword.setError("Password cannot be empty!");
            check = false;
        }
        else{//else password is correct
            _inputPassword.setError(null);
        }
        if(check)
        {
            _progressDialog.show();

            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {//checked by authentication firebase
                //if register success
                if(task.isSuccessful())
                {
                    FirebaseUser firebaseUser = auth.getCurrentUser();//get current user
                    User user = new User();
                    user.setEmailAddress(firebaseUser.getEmail());
                    user.setUID(firebaseUser.getUid());

                    //save user detail in preferences
                    getSessionHandler().setUser(user);
                    getSessionHandler().checkAuthorization();//check authorization, to determine which activity to go
                }
                else{//if register fail
                    //appear alert dialog
                    if(_progressDialog.isShowing())
                        _progressDialog.dismiss();
                    ErrorAlert(task.getException().getMessage(), sweetAlertDialog -> sweetAlertDialog.cancel()).show();
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(_progressDialog.isShowing())
            _progressDialog.dismiss();
    }

}
