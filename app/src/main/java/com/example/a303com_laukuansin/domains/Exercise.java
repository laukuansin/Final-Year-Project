package com.example.a303com_laukuansin.domains;


public class Exercise{
    private String exerciseName;
    private String exerciseRecordID;
    private String exerciseID;
    private int duration;
    private double caloriesBurnedPerKGPerMin;
    private double calories;
    private String exerciseIcon;

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
}
