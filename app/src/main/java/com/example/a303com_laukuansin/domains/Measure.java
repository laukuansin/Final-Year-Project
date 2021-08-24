package com.example.a303com_laukuansin.domains;

import com.google.gson.annotations.SerializedName;

public class Measure{
    @SerializedName("serving_weight")
    public double servingWeight;

    @SerializedName("measure")
    public String servingUnit;

    @SerializedName("qty")
    private double quantity;

    public double getGramPerServing()
    {
        return servingWeight/quantity;
    }
}
