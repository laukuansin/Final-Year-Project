package com.example.a303com_laukuansin.domains;

import com.google.gson.annotations.SerializedName;

public class FoodDetail {
    @SerializedName("food_name")
    public String foodName;

    @SerializedName("brand_name")
    public String brandName;

    @SerializedName("serving_qty")
    public double quantity;

    @SerializedName("serving_unit")
    public String servingUnit;

    @SerializedName("serving_weight_grams")
    public double weightGrams=1;

    @SerializedName("nf_calories")
    public double calories;

    @SerializedName("nf_total_fat")
    public double fats;

    @SerializedName("nf_total_carbohydrate")
    public double carbohydrate;

    @SerializedName("nf_dietary_fiber")
    public double fiber;

    @SerializedName("nf_protein")
    public double proteins;

    @SerializedName("alt_measures")
    public Measure[] measures;

    @SerializedName("photo")
    public FoodPhoto photo;
}
