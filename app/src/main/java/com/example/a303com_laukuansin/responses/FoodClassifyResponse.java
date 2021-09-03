package com.example.a303com_laukuansin.responses;

import com.example.a303com_laukuansin.domains.FoodImages;
import com.google.gson.annotations.SerializedName;

public class FoodClassifyResponse {
    @SerializedName("images")
    public FoodImages[] foodImages;
}
