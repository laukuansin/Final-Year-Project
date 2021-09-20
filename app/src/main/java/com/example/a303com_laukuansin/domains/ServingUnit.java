package com.example.a303com_laukuansin.domains;

public class ServingUnit {
    private String servingUnitName;
    private String imageUrl;
    private String description="";

    public ServingUnit() {
    }

    public String getServingUnitName() {
        return servingUnitName;
    }

    public void setServingUnitName(String servingUnitName) {
        this.servingUnitName = servingUnitName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
