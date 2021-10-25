package com.example.a303com_laukuansin.validator;

public class ExerciseModuleValidator {
    public boolean saveExercise(int duration) {
        String durationStr = String.valueOf(duration);
        if (durationStr.isEmpty()) {//if duration is empty
            return false;
        } else {
            duration = Integer.parseInt(durationStr);
            if (duration <= 0) {//if duration is 0
               return false;
            }
        }

        return true;
    }
}
