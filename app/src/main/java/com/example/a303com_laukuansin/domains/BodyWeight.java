package com.example.a303com_laukuansin.domains;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

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

    private Date getDateInTime()
    {
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
        try{
            Date date = format.parse(getDate());
            return date;
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    //comparator
    public static Comparator<BodyWeight> bodyWeightDescComparator = (b1, b2) -> b2.getDateInTime().compareTo(b1.getDateInTime());

}
