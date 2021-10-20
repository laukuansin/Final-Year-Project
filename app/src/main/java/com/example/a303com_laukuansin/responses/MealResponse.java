package com.example.a303com_laukuansin.responses;

import com.example.a303com_laukuansin.domains.BrandedFood;
import com.example.a303com_laukuansin.domains.CommonFood;
import com.example.a303com_laukuansin.domains.Meal;
import com.google.gson.annotations.SerializedName;

public class MealResponse {
    @SerializedName("common")
    public CommonFood[] commonFood;

    @SerializedName("branded")
    public BrandedFood[] brandedFood;
}

