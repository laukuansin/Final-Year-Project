package com.example.a303com_laukuansin.domains;

public class BodyWeight {
    private String bodyWeightRecordID;
    private double bodyWeight;
    private String date;

    public BodyWeight() {
    }

    public String getBodyWeightRecordID() {
        return bodyWeightRecordID;
    }

    public void setBodyWeightRecordID(String bodyWeightRecordID) {
        this.bodyWeightRecordID = bodyWeightRecordID;
    }

    public double getBodyWeight() {
        return bodyWeight;
    }

    public void setBodyWeight(double bodyWeight) {
        this.bodyWeight = bodyWeight;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
