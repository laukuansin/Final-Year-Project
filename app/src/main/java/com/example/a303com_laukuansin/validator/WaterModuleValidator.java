package com.example.a303com_laukuansin.validator;

public class WaterModuleValidator {
    public boolean onClickMinusButton(int glassOfWater)
    {
        if (glassOfWater <= 1) {
            return false;
        } else {//otherwise higher than 1
           return true;
        }
    }
}
