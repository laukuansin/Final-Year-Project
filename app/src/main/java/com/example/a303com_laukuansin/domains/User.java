package com.example.a303com_laukuansin.domains;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class User implements Serializable {
    private String emailAddress;
    private String name;
    private String UID;
    private int yearOfBirth=0;// The reason of get the years of birth instead of the age, because when today is 31/12/2021 and user is 21 years old, then if get the age instead of years of birth, so when tomorrow user open the app, then the age will still display 21 years old
    private String gender;
    private int height=0;
    private double weight=0;
    private double targetWeight=0;
    private double startWeight=0;
    private double activityLevel=0;
    private Date dateCreated;

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

    public double getStartWeight() {
        return startWeight;
    }

    public void setStartWeight(double startWeight) {
        this.startWeight = startWeight;
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

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreateAccount(Date dateCreated) {
        this.dateCreated = dateCreated;
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
    public int getAge()
    {
        return Calendar.getInstance().get(Calendar.YEAR)-getYearOfBirth();
    }

    public double getBasalMetabolicRate()
    {
        //Mifflin-St Jeor Formula common use and latest
        double BMR = 0;
        if(getGender().equals("Male"))
        {
            BMR = (10*getWeight())+(6.25*getHeight())-(5* getAge())+5;
        }
        else if(getGender().equals("Female")){
            BMR = (10*getWeight())+(6.25*getHeight())-(5* getAge())-161;
        }

        return BMR;
    }

    public double getDailyCaloriesEaten()
    {
        double BMR =getBasalMetabolicRate();//get BMR
        double calories = BMR*getActivityLevel();//Calories = BMR * activity level, then round off it
        if(getStartWeight()>getTargetWeight())//if user target is lost weight
        {
            calories-=250;//calories decrease 250
        }
        else if(getStartWeight()<getTargetWeight()){//if user target is gain weight
            calories+=250;//calories increase 250
        }
        //user started weight is same as the target weight
        return calories;
    }

    public double getDailyCaloriesBurnt()
    {
        return getDailyCaloriesEaten()*0.2;//20% of calories is for physical activity which mean, human should burn 20% of calories daily
    }

    public double getSuggestBreakfastCalorieEaten()
    {
        //book suggest is 22% for breakfast
        return getDailyCaloriesEaten()*0.22;
    }
    public double getSuggestLunchCalorieEaten()
    {
        //book suggest is 31% for lunch
        return getDailyCaloriesEaten()*0.31;
    }
    public double getSuggestDinnerCalorieEaten()
    {
        //book suggest is 35% for breakfast
        return getDailyCaloriesEaten()*0.35;
    }
    public double getSuggestSnackCalorieEaten()
    {
        //book suggest is 12% for breakfast
        return getDailyCaloriesEaten()*0.12;
    }

    public int getSuggestStepWalk()
    {
        return 10000;
    }

    public int getSuggestWaterIntakeInGlass()
    {
        int waterInMilliliter = 0;
        if(getGender().equals("Male"))
        {
            if(getAge()>18)
            {
                waterInMilliliter = 3700;
            }
            else{
                waterInMilliliter = 3300;
            }
        }
        else if(getGender().equals("Female"))
        {
            if(getAge()>18)
            {
                waterInMilliliter = 2700;
            }
            else{
                waterInMilliliter = 2300;
            }
        }
        return (int)(Math.ceil(waterInMilliliter/250));//for easier user tracking, i will use 250Ml per glass to record
    }

    public String getTargetGoal()
    {
        String goal = "";
        if(getStartWeight()>getTargetWeight())//if started weight higher than target weight
        {
            goal = "Lose Weight";
        }
        else if(getStartWeight()<getTargetWeight())
        {
            goal = "Gain Weight";
        }
        else{
            goal = "Maintain Weight";
        }
        return goal;
    }

    public double getCaloriesBurnedPerStepWalked()
    {
        double stepMultipliersPerKG = 0;
        //when the user height is higher or equal than 183
        if(getHeight()>=183)
        {
            stepMultipliersPerKG = 0.0006;
        }
        else if(getHeight()<=165)//when the user height is lower or equal than 165
        {
            stepMultipliersPerKG = 0.0005;
        }
        else{//when the user height is in between 166 to 182
            stepMultipliersPerKG = 0.00055;
        }
        return getWeight()*stepMultipliersPerKG;
    }
}
