package com.example.a303com_laukuansin.domains;

import com.google.gson.annotations.SerializedName;

public class BrandedFood {
    @SerializedName("food_name")
    private String name;

    @SerializedName("nix_item_id")
    private String ID;

    @SerializedName("brand_name")
    private String brandName;

    public BrandedFood() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }
}
