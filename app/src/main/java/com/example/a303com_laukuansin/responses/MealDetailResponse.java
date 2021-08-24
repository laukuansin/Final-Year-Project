package com.example.a303com_laukuansin.responses;

import com.example.a303com_laukuansin.domains.FoodDetail;
import com.google.gson.annotations.SerializedName;

public class MealDetailResponse {
    @SerializedName("foods")
    public FoodDetail[] foodDetail;

    @SerializedName("message")
    public String message;
}
