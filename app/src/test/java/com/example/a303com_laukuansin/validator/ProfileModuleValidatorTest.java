package com.example.a303com_laukuansin.validator;

import org.junit.Test;

import static org.junit.Assert.*;

public class ProfileModuleValidatorTest {

    @Test
    public void checkAndUpdateProfile() {
        ProfileModuleValidator validator = new ProfileModuleValidator();
        boolean actual = validator.checkAndUpdateProfile("kuansin","Male",1.25,21,69.7,65.0,183);
        boolean expected = true;
        assertEquals(expected,actual);
    }

    @Test
    public void checkAndUpdatePassword() {
        ProfileModuleValidator validator = new ProfileModuleValidator();
        boolean actual = validator.checkAndUpdatePassword("12345678","12345678");
        boolean expected = true;
        assertEquals(expected,actual);
    }
}