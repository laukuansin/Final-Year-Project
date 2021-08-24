package com.example.a303com_laukuansin.requests;

import com.google.gson.annotations.SerializedName;

public class MealDetailRequest {
    @SerializedName("query")
    private String foodName;

    public MealDetailRequest(String foodName) {
        this.foodName = foodName;
    }

}
