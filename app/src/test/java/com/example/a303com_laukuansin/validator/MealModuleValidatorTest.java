package com.example.a303com_laukuansin.validator;

import org.junit.Test;

import static org.junit.Assert.*;

public class MealModuleValidatorTest {

    @Test
    public void saveUpdateMeal() {
        MealModuleValidator validator = new MealModuleValidator();
        boolean actual = validator.saveUpdateMeal(2,"g");
        boolean expected = true;
        assertEquals(expected,actual);
    }
    @Test
    public void convertUPCE_To_UPCA() {
        MealModuleValidator validator = new MealModuleValidator();
        String actual = validator.convertUPCE_To_UPCA("UPC_E","04252610");
        String expected = "042100005264";
        assertEquals(expected,actual);
    }
}