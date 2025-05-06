package me.funky.praxi.util;

public class NumberUtil {

    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isShort(String input) {
        try {
            Short.parseShort(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
