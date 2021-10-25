package com.example.a303com_laukuansin.validator;

import android.util.Patterns;

import androidx.core.util.PatternsCompat;

public class SignUpModuleValidator {

    public boolean checkInputSignUp(String email,String password)
    {
        if (email.isEmpty())//if email is empty
        {
            return false;
        } else if (!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches())//if email is invalid format
        {
           return false;
        }
        if (password.isEmpty())//if password is empty
        {
           return false;
        } else if (password.length() < 8)//if password length is less than 8
        {
            return false;
        }
        return true;
    }
}
