package com.example.a303com_laukuansin.validator;

public class MealModuleValidator {
    public boolean saveUpdateMeal(double quantity,String servingUnit)
    {
        if (servingUnit.isEmpty()) {//if serving unit is empty
            return false;
        }
        String quantityStr = String.valueOf(quantity);
        if (quantityStr.isEmpty()) {//if quantity is empty
            return false;
        } else {
            quantity = Double.parseDouble(quantityStr);
            if (quantity <= 0) {//if(quantity is 0
                return false;
            }
        }
        return true;
    }

    public String convertUPCE_To_UPCA(String barcodeType,String barcode)
    {
        if(barcodeType.equals("UPC_E"))
        {
            String UPC_A = helper(barcode);
            return UPC_A;
        }
        else {
            return "";
        }
    }
    private String helper(String barcode)
    {
        int checkDigit = barcode.charAt(7);
        switch (barcode.charAt(6)) {
            case '0':
            case '1':
            case '2': {
                barcode = barcode.substring(1, 3) + barcode.charAt(6) + "0000"  + barcode.substring(3, 6);
                break;
            }
            case '3': {
                barcode = barcode.substring(1, 4) + "00000" + barcode.substring(4,6);
                break;
            }
            case '4': {
                barcode = barcode.substring(1, 5) + "00000" + barcode.charAt(5);
                break;
            }
            case '5':
            case '6':
            case '7':
            case '8':
            case '9': {
                barcode = barcode.substring(1, 6) + "0000" + barcode.charAt(6);
                break;
            }
        }
        return "0" + barcode + checkDigit;
    }
}
