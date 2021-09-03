package com.example.a303com_laukuansin.domains;

import java.util.Date;

public class Meal {
    private String mealRecordID;
    private String UID;
    private String mealName;
    private String nixItemID="";
    private String foodBarcode="";
    private double calories;
    private String foodType;
    private String mealType;
    private double quantity;
    private String servingUnit;
    private double foodWeightInGram;
    private Date date;

    public Meal() {
    }

    public String getMealRecordID() {
        return mealRecordID;
    }

    public void setMealRecordID(String mealRecordID) {
        this.mealRecordID = mealRecordID;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getNixItemID() {
        return nixItemID;
    }

    public void setNixItemID(String nixItemID) {
        this.nixItemID = nixItemID;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getServingUnit() {
        return servingUnit;
    }

    public void setServingUnit(String servingUnit) {
        this.servingUnit = servingUnit;
    }

    public String getFoodBarcode() {
        return foodBarcode;
    }

    public void setFoodBarcode(String foodBarcode) {
        this.foodBarcode = foodBarcode;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public double getFoodWeightInGram() {
        return foodWeightInGram;
    }

    public void setFoodWeightInGram(double foodWeightInGram) {
        this.foodWeightInGram = foodWeightInGram;
    }
}
