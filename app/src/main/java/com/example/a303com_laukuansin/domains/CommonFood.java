package com.example.a303com_laukuansin.domains;

import com.google.gson.annotations.SerializedName;

public class CommonFood {
    @SerializedName("food_name")
    private String name;
    @SerializedName("tag_id")
    private String ID;

    public CommonFood() {
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
}
