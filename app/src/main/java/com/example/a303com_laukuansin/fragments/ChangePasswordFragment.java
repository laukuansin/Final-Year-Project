package com.example.a303com_laukuansin.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.a303com_laukuansin.R;
import com.example.a303com_laukuansin.cores.BaseFragment;
import com.example.a303com_laukuansin.domains.User;
import com.example.a303com_laukuansin.utilities.OnSingleClickListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class ChangePasswordFragment extends BaseFragment {
    private TextInputLayout _inputOldPassword,_inputNewPassword;
    private final User user;
    public ChangePasswordFragment() {
        user = getSessionHandler().getUser();
    }

    public static ChangePasswordFragment newInstance() {
        return new ChangePasswordFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        initialization(view);
        return view;
    }

    private void initialization(View view)
    {
        //bind view with id
        _inputOldPassword = view.findViewById(R.id.oldPasswordLayout);
        _inputNewPassword = view.findViewById(R.id.newPasswordLayout);
        Button _changePasswordButton = view.findViewById(R.id.changePasswordButton);

        //when click change password button
        _changePasswordButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                checkInputAndChangePassword();
            }
        });
    }

    private void checkInputAndChangePassword()
    {
        SweetAlertDialog _progressDialog = showProgressDialog("Loading...",getResources().getColor(R.color.colorPrimary));
        boolean check = true;
        String oldPassword = _inputOldPassword.getEditText().getText().toString().trim();
        if(oldPassword.isEmpty())//if old password is empty
        {
            _inputOldPassword.setError("Old Password cannot be empty!");
            check = false;
        }
        else{//else no error
            _inputOldPassword.setError(null);
        }
        String newPassword = _inputNewPassword.getEditText().getText().toString().trim();
        if(newPassword.isEmpty())//if new password is empty
        {
            _inputNewPassword.setError("New Password cannot be empty!");
            check = false;
        }
        else if(newPassword.length()<8)//if password length is less than 8
        {
            _inputNewPassword.setError("New Password should be more than 8 characters long.");
            check = false;
        }
        else if(oldPassword.equals(newPassword))//if both password is same
        {
            _inputNewPassword.setError("New Password should not same as Old Password.");
            check = false;
        }
        else{//else no error
            _inputNewPassword.setError(null);
        }

        if(check)
        {
            _progressDialog.show();
            //get the user credential
            AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmailAddress(),oldPassword);
            //get the user
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            //re-authenticate is to check the old password user enter is correct
            firebaseUser.reauthenticate(authCredential).addOnCompleteListener(task -> {
                //if success only update the user password
                if(task.isSuccessful())
                {
                    firebaseUser.updatePassword(newPassword).addOnCompleteListener(updatePasswordTask -> {
                        if(_progressDialog.isShowing())
                            _progressDialog.dismiss();

                        if(updatePasswordTask.isSuccessful())
                        {
                            Toast.makeText(getContext(), "Password Successfully Modified", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        }
                        else{
                            ErrorAlert(updatePasswordTask.getException().getMessage(), sweetAlertDialog -> sweetAlertDialog.dismiss(),true).show();
                        }
                    });
                }
                else
                {
                    if(_progressDialog.isShowing())
                        _progressDialog.dismiss();

                    ErrorAlert("The old password is incorrect", sweetAlertDialog -> sweetAlertDialog.dismiss(),true).show();
                }
            });
        }
    }
}
