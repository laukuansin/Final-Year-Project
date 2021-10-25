package com.example.a303com_laukuansin.validator;

import org.junit.Test;

import static org.junit.Assert.*;

public class WaterModuleValidatorTest {

    @Test
    public void onClickMinusButton() {
        WaterModuleValidator validator = new WaterModuleValidator();
        boolean actual = validator.onClickMinusButton(1);
        boolean expected = false;
        assertEquals(expected,actual);
    }
}