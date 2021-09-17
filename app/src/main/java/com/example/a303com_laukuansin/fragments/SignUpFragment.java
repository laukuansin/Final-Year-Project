package com.example.a303com_laukuansin.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.activities.PersonalInformationActivity;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.domains.User;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class SignUpFragment extends BaseFragment {
    private CheckBox _checkBox;
    private TextInputLayout _inputEmail,_inputPassword;
    private TextView _termPolicy,_loginView;
    private Button _signUpButton;
    public SignUpFragment() {
    }
    public static SignUpFragment newInstance()
    {
        return new SignUpFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        //initialize
        initialization(view);

        return view;
    }

    private void checkInputAndSignUp()
    {
        String email = _inputEmail.getEditText().getText().toString().trim();//get the email
        boolean check = true;
        if(email.isEmpty())//if email is empty
        {
            _inputEmail.setError("Email address cannot be empty!");
            check = false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())//if email is invalid format
        {
            _inputEmail.setError("Invalid Email address format!");
            check = false;
        }
        else{//else no error
            _inputEmail.setError(null);
        }
        String password = _inputPassword.getEditText().getText().toString().trim();
        if(password.isEmpty())//if password is empty
        {
            _inputPassword.setError("Password cannot be empty!");
            check = false;
        }
        else if(password.length()<8)//if password length is less than 8
        {
            _inputPassword.setError("Password should be more than 8 characters long.");
            check = false;
        }
        else{//else no error
            _inputPassword.setError(null);
        }
        if(check)//all input is correct
        {
            //progress dialog
            SweetAlertDialog _progressDialog = showProgressDialog("Creating account...",getResources().getColor(R.color.colorPrimary));
            _progressDialog.show();
            //firebase instance
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(getActivity(), task -> {
                //if register success
                if(task.isSuccessful())
                {
                    //close progress dialog
                    if (_progressDialog.isShowing())
                        _progressDialog.dismiss();

                    FirebaseUser firebaseUser = auth.getCurrentUser();//get user
                    User user = new User();
                    user.setEmailAddress(firebaseUser.getEmail());
                    user.setUID(firebaseUser.getUid());
                    //save user detail in preferences
                    getSessionHandler().setUser(user);
                    Intent intent = new Intent(getContext(), PersonalInformationActivity.class);
                    // Closing all the Activities
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    // Add new Flag to start new Activity
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else{//if register fail

                    //close progress dialog
                    if (_progressDialog.isShowing())
                        _progressDialog.dismiss();

                    //appear alert dialog
                    ErrorAlert(task.getException().getMessage(), (sweetAlertDialog) -> sweetAlertDialog.cancel(),true).show();
                }
            });
        }
    }

    private void initialization(View view)
    {
        _checkBox = view.findViewById(R.id.checkBox);
        _termPolicy = view.findViewById(R.id.termPolicy);
        _signUpButton = view.findViewById(R.id.signUpButton);
        _loginView = view.findViewById(R.id.loginButton);
        _inputEmail = view.findViewById(R.id.emailLayout);
        _inputPassword = view.findViewById(R.id.passwordLayout);

        //when click the check box action
        _checkBox.setOnClickListener(v -> {
            if(_checkBox.isChecked())
            {
                _signUpButton.setAlpha(1f);
                _signUpButton.setEnabled(true);
            }
            else{
                _signUpButton.setAlpha(0.3f);
                _signUpButton.setEnabled(false);
            }
        });

        //when click sign up button action
        _signUpButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                checkInputAndSignUp();
            }
        });

        setTextViewClickAndStyle();
    }

    private void setTextViewClickAndStyle()
    {
        //set clickable for few substring
        SpannableString spanString = new SpannableString("By signing up, I agree to the Terms & Conditions and Privacy Policy");
        SpannableString spanStringLogin = new SpannableString("Already have an account? Login");

        //when click Login text
        ClickableSpan clickableLogin = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                //change fragment
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout,LoginFragment.newInstance());
                fragmentTransaction.commit();
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        //when click Terms & Conditions text
        ClickableSpan clickableTerms = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                //link to the terms and conditions link
                String url = "https://www.websitepolicies.com/policies/view/4KvzuEdd";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        //when click Privacy Policy text
        ClickableSpan clickablePolicy = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                //link to the privacy policy link
                String url = "https://www.termsfeed.com/live/0a3ce8d7-89f8-46ba-8b28-00d69656d822";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        spanString.setSpan(clickableTerms,30,48, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanString.setSpan(clickablePolicy,52,67, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanStringLogin.setSpan(clickableLogin,24,30,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        _termPolicy.setText(spanString);
        _termPolicy.setMovementMethod(LinkMovementMethod.getInstance());
        _termPolicy.setHighlightColor(Color.TRANSPARENT);

        _loginView.setText(spanStringLogin);
        _loginView.setMovementMethod(LinkMovementMethod.getInstance());
        _loginView.setHighlightColor(Color.TRANSPARENT);
    }
}
