package com.app.shopfee.utils;

public class StringUtil {

    public static boolean isEmpty(String input) {
        return input == null || input.isEmpty() || ("").equals(input.trim());
    }

    public static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static String getDoubleNumber(int number) {
        if (number < 10) {
            return "0" + number;
        } else return "" + number;
    }
}
