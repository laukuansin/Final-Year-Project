package com.example.a303com_laukuansin.validator;

import org.junit.Test;

import static org.junit.Assert.*;

public class SignUpModuleValidatorTest {

    @Test
    public void checkInputSignUp() {
        SignUpModuleValidator validator = new SignUpModuleValidator();
        boolean actual = validator.checkInputSignUp("laukuansin@gmail.com","12345");
        boolean expected = true;
        assertEquals(expected,actual);
    }
}