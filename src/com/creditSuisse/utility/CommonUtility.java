/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.utility;

import org.apache.log4j.Logger;
import com.creditSuisse.utility.constants.ComparisonCategory;

/**
 * Common functions for miscellaneous activities
 * @author Edward Guo
 * @since 11/26/2019
 * @todo refactor and migrate methods to avoid Common (bland and confusing)
 */
public class CommonUtility {

    private final static String ENGLISH_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static long convToMseconds(long seconds)
    {
        return seconds * 1000;
    }

    public static String getCurrentMethodName() {
        return new Throwable().getStackTrace()[2].getMethodName();
    }

    public static void logCurrentMethodName(Logger logger) {
        logger.info("Entering method:" + getCurrentMethodName());
    }

    /**
     * attempts to change a string value to an integer value
     * @param value target string value
     * @return return true if successful, false otherwise
     */
    public static boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * acquires a random integer from a minimum-maximum range provided
     * @param min minimum range to consider
     * @param max maximum range to consider
     * @return random integer generated
     */
    public static int getARandomInteger(int min, int max) {
        return min + (int) (Math.random() * max);
    }

    /**
     * acquires a string of random characters of length provided
     * @param length total length of random characters in the string
     * @return returns the random string of characters
     */
    public static String getRandomCharacters(int length) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(ENGLISH_ALPHABET.charAt(getARandomInteger(0, ENGLISH_ALPHABET.length() - 1)));
        }
        return result.toString();
    }

    /**
     * checks whether the actual integer meets comparison category for expected
     * @param comparisonCategory the specified comparison category
     * @param actual the actual integer
     * @param expected the expected integer
     * @return true if category is accurate, else false
     */
    public static boolean checkCriteria(ComparisonCategory comparisonCategory, int actual, int expected) {
        switch (comparisonCategory) {
            case GT:
                return actual > expected;
            case GE:
                return actual >= expected;
            case E:
                return actual == expected;
            case LE:
                return actual <= expected;
            case LT:
                return actual < expected;
        }
        return false;
    }
}
