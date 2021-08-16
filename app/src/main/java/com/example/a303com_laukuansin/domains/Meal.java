package com.example.a303com_laukuansin.domains;

public class Meal{
    private String type;
    private int suggestCalorie=0;//default = 0
    private int currentCalorie=0;//default = 0

    public Meal(String type) {
        this.type=type;
    }

    public int getSuggestCalorie() {
        return suggestCalorie;
    }

    public void setSuggestCalorie(int suggestCalorie) {
        this.suggestCalorie = suggestCalorie;
    }

    public int getCurrentCalorie() {
        return currentCalorie;
    }

    public void setCurrentCalorie(int currentCalorie) {
        this.currentCalorie = currentCalorie;
    }

    public String getType() {
        return type;
    }
}
