package com.example.a303com_laukuansin.domains;

import java.util.Comparator;

public class Exercise{
    private String exerciseName;
    private String exerciseRecordID;
    private String exerciseID;
    private int duration;
    private double caloriesBurnedPerKGPerMin;
    private double calories;
    private String exerciseIcon;
    private String date;

    public Exercise() {
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getExerciseRecordID() {
        return exerciseRecordID;
    }

    public void setExerciseRecordID(String exerciseRecordID) {
        this.exerciseRecordID = exerciseRecordID;
    }

    public double getCaloriesBurnedPerKGPerMin() {
        return caloriesBurnedPerKGPerMin;
    }

    public void setCaloriesBurnedPerKGPerMin(double caloriesBurnedPerKGPerMin) {
        this.caloriesBurnedPerKGPerMin = caloriesBurnedPerKGPerMin;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public String getExerciseID() {
        return exerciseID;
    }

    public void setExerciseID(String exerciseID) {
        this.exerciseID = exerciseID;
    }

    public String getExerciseIcon() {
        return exerciseIcon;
    }

    public void setExerciseIcon(String exerciseIcon) {
        this.exerciseIcon = exerciseIcon;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    //comparator
    public static Comparator<Exercise> exerciseNameAZComparator = (e1, e2) -> e1.getExerciseName().compareTo(e2.getExerciseName());

    public static Comparator<Exercise> exerciseNameZAComparator = (e1, e2) -> e2.getExerciseName().compareTo(e1.getExerciseName());

    public static Comparator<Exercise> exerciseCaloriesLowToHighComparator = (e1, e2) -> Double.compare(e1.getCaloriesBurnedPerKGPerMin(),e2.getCaloriesBurnedPerKGPerMin());

    public static Comparator<Exercise> exerciseCaloriesHighToLowComparator = (e1, e2) -> Double.compare(e2.getCaloriesBurnedPerKGPerMin(),e1.getCaloriesBurnedPerKGPerMin());
}
