package com.example.util;

/**
 * GreateConverter converts everything to gold
 */
public class GreateConverter {

    /**
     * converts everything to gold
     * @param str
     * @return gold
     */
    public static String convertString(String str) {
        return Midas.touch(str);
    }

}