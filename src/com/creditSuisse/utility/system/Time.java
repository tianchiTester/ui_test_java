/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.utility.system;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Timer functions for conversions, freezing execution, configuring Dates, etc.
 * @author Eric Tyree
 * @since 11/20/2019
 */
public class Time {

    /**
     * suspends all activities for a given time period
     * @param seconds time to suspend all operations
     */
    public static void suspend(int seconds) {
        long millSeconds = seconds * 1000;
        try {
            sleep(millSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * sleeps for a given time period
     * @param millSeconds time to sleep
     * @throws InterruptedException if sleeping is interrupted
     */
    private static void sleep(long millSeconds) throws InterruptedException {
        Thread.sleep(millSeconds);
    }

    /**
     * gets current data with specified format
     * @param format specified date format
     * @return current Date in specified format
     */
    public static Date getDate(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date current = null;
        try {
            current = sdf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return current;
    }

    /**
     * gets a format of a date by the format passed in
     * @param value value date to format
     * @param format type of format to use
     * @return formatted string date
     * @throws Exception if date cannot be formatted
     */
    public static String getDateFormat(String value, String format) throws Exception {
        String dateStr=value.substring(4, 6)+" "+value.substring(6, 8)+", "+value.substring(0, 4);
        SimpleDateFormat originalFormat = new SimpleDateFormat("MM dd, yyyy");
        Date date = originalFormat.parse(dateStr);
        SimpleDateFormat newFormat = new SimpleDateFormat(format);
        String formatedDate = newFormat.format(date);
        return formatedDate;
    }

    /**
     * gets the current date in string format
     * @param format format to use for returning the date
     * @return string formatted date
     */
    public static String getCurrentDate(String format) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    /**
     * converts an amount of milliseconds to minutes and seconds
     * @param milliSeconds millisecond time to convert
     * @return String of minutes and seconds conversion
     */
    public static String convertToStringTime(long milliSeconds) {
        long minutes = (milliSeconds / 1000) / 60;
        long seconds = (milliSeconds / 1000) % 60;
        if (minutes <= 0)
            return seconds + "s";
        else
            return minutes + "m" + seconds + "s";
    }
}
