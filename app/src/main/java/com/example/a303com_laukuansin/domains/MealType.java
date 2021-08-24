package com.example.a303com_laukuansin.domains;

public class MealType{
    private String type;
    private double suggestCalorie=0;//default = 0
    private double currentCalorie=0;//default = 0

    public MealType(String type) {
        this.type=type;
    }

    public double getSuggestCalorie() {
        return suggestCalorie;
    }

    public void setSuggestCalorie(double suggestCalorie) {
        this.suggestCalorie = suggestCalorie;
    }

    public double getCurrentCalorie() {
        return currentCalorie;
    }

    public void setCurrentCalorie(double currentCalorie) {
        this.currentCalorie = currentCalorie;
    }

    public String getType() {
        return type;
    }
}
