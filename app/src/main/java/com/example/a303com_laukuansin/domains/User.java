package com.example.a303com_laukuansin.domains;

import java.io.Serializable;

public class User implements Serializable {
    private String emailAddress;
    private String name;
    private String UID;
    private int yearOfBirth=0;// The reason of get the years of birth instead of the age, because when today is 31/12/2021 and user is 21 years old, then if get the age instead of years of birth, so when tomorrow user open the app, then the age will still display 21 years old
    private String gender;
    private int height=0;
    private double weight=0;
    private double targetWeight=0;
    private double activityLevel=0;

    public User() {
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public int getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(int yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(double targetWeight) {
        this.targetWeight = targetWeight;
    }

    public double getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(double activityLevel) {
        this.activityLevel = activityLevel;
    }

    public double getBMI()
    {
        double BMI = getWeight()/Math.pow((double)getHeight()/100,2.0);//BMI = Weight(KG)/ (Height(M)^2)
        BMI = Math.round(BMI*10.0)/10.0;//round off the BMI to 1 decimal digit
        return BMI;
    }

    public int getMinimumIdealWeight()
    {
        return (int)Math.round(Math.pow((double)getHeight()/100,2.0)*18.5);//formula to calculate the minimum ideal weight 18.4-24.9 BMI is ideal. Round off the weight.
    }

    public int getMaximumIdealWeight()
    {
        return (int)Math.round(Math.pow((double)getHeight()/100,2.0)*24.9);//formula to calculate the maximum ideal weight 18.4-24.9 BMI is ideal. Round off the weight.
    }
}
