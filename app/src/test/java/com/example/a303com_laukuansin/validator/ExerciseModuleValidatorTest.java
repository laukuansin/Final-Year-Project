package com.example.a303com_laukuansin.validator;

import org.junit.Test;

import static org.junit.Assert.*;

public class ExerciseModuleValidatorTest {

    @Test
    public void saveExercise() {
        ExerciseModuleValidator validator = new ExerciseModuleValidator();
        boolean actual = validator.saveExercise(10);
        boolean expected = true;
        assertEquals(expected,actual);
    }
}