package com.example.a303com_laukuansin.validator;

public class ProfileModuleValidator {
    public boolean checkAndUpdateProfile(String name,String gender,double activityLevel, double age, double currentWeight,double targetWeight, int height) {
        if (name.isEmpty())//if name is empty
        {
           return false;
        }
        if (gender.isEmpty())//if gender is empty
        {
            return false;
        }
        String ageString = String.valueOf(age);
        if (ageString.isEmpty())//if age is empty
        {
            return false;
        } else {
            if (age < 15 || age > 99)//if age less than 15 or more than 99
            {
                return false;
            }
        }
        if (activityLevel == 0)//if daily activity is empty
        {
           return false;
        }
        String heightString = String.valueOf(height);
        if (heightString.isEmpty())//if height is empty
        {
            return false;
        } else {
            if (height < 100 || height > 270)//if height less than 100 or more than 270 cm
            {
                return false;
            }
        }
        String weightString = String.valueOf(currentWeight);
        if (weightString.isEmpty())//if weight is empty
        {
            return false;
        } else {
            if (currentWeight < 30 || currentWeight > 300.9)//if current weight less than 30 or more than 300.9 kg
            {
                return false;
            }
        }
        String targetWeightString = String.valueOf(targetWeight);
        if (targetWeightString.isEmpty())//if target weight is empty
        {
            return false;
        } else {
            if (targetWeight < 30 || targetWeight > 300.9)//if target weight less than 30 or more than 300.9 kg
            {
               return false;
            }
        }

        return true;
    }

    public boolean checkAndUpdatePassword(String oldPassword,String newPassword)
    {
        if(oldPassword.isEmpty())//if old password is empty
        {
            return false;
        }

        if(newPassword.isEmpty())//if new password is empty
        {
            return false;
        }
        else if(newPassword.length()<8)//if password length is less than 8
        {
            return false;
        }
        else if(oldPassword.equals(newPassword))//if both password is same
        {
            return false;
        }
        return true;

    }
}
