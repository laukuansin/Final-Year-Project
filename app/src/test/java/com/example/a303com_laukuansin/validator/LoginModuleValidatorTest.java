package com.example.a303com_laukuansin.validator;

import org.junit.Test;

import static org.junit.Assert.*;

public class LoginModuleValidatorTest {

    @Test
    public void checkInputLogin() {
        LoginModuleValidator validator = new LoginModuleValidator();
        boolean actual = validator.checkInputLogin("laukuansin@gmail.com","12345678");
        boolean expected = true;
        assertEquals(expected,actual);
    }

    @Test
    public void checkInputForgetPassword() {
        LoginModuleValidator validator = new LoginModuleValidator();
        boolean actual = validator.checkInputForgetPassword("laukuansin@gmail.com");
        boolean expected = true;
        assertEquals(expected,actual);
    }
}
