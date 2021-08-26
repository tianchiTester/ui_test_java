/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.utility;

import com.creditSuisse.utility.constants.Constants;
import org.apache.log4j.FileAppender;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;
import java.io.IOException;

/**
 * Configured logger for framework/product related logging
 * Use by: LogManager.logProduct.info("some info");
 * OR LogManager.logFramework.info("some info");
 * @author Nicole Zhang
 * @since 11/20/2019
 */
public class LogManager {

    static {
        productLogger();
        frameworkLogger();
    }
    private static ThreadLocal<Logger> threadLog4J = new ThreadLocal<>();
    public static Logger logFramework;
    public static Logger logProduct;

    /**
     * getter for the classic log4j logger
     * @return default logger object (recommended to use framework/product loggers)
     */
    public static Logger getLogger() {
        return threadLog4J.get();
    }

    /**
     * acquires the line in a class that was executed
     * @param classFilter class name to find the targeted line
     * @return the int running line in the class, if none: 0
     */
    public static int getRunningLine(String classFilter) {
        StackTraceElement[] traceElements = new Throwable().getStackTrace();
        for (StackTraceElement traceElement: traceElements) {
            if (traceElement.getClassName().contains(classFilter))
                return traceElement.getLineNumber();
        }
        return 0;
    }

    /**
     * logs specific framework actions based on the log4j.properties file
     */
    public static void frameworkLogger() {
        PropertyConfigurator.configure(Constants.PATH_LOG);
        PatternLayout pLayout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}: %M(%F:%L) - %m%n");
        logFramework = Logger.getLogger("framework");
        FileAppender faFrame = null;
        try {
            String frameworkLogFilename = "frameworkLog.log";
            faFrame = new FileAppender(pLayout, "logs/" + frameworkLogFilename, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logFramework.addAppender(faFrame);
    }

    /**
     * logs specific product actions based ont he log4j.properties file
     */
    public static void productLogger() {
        PropertyConfigurator.configure(Constants.PATH_LOG);
        PatternLayout pLayout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}: %M(%F:%L) - %m%n");
        logProduct = Logger.getLogger("product");
        FileAppender faFrame = null;
        try {
            String productLogFilename = "product.log";
            faFrame = new FileAppender(pLayout, "logs/" + productLogFilename, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logProduct.addAppender(faFrame);
    }
}
