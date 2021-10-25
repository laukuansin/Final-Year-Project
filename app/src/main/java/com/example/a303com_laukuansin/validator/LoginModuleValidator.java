package com.example.a303com_laukuansin.validator;

import android.util.Patterns;

import androidx.core.util.PatternsCompat;

public class LoginModuleValidator {

    public boolean checkInputLogin(String email,String password) {
        if (email.isEmpty())// if email is empty
        {
            return false;
        } else if (!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches())//if email does not meet the format
        {
            return false;
        }

        if (password.isEmpty())//if password is empty
        {
            return false;
        }
        return true;
    }

    public boolean checkInputForgetPassword(String email) {
        if (email.isEmpty())// if email is empty
        {
            return false;
        } else if (!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches())//if email does not meet the format
        {
            return false;
        }
        return true;
    }
}
