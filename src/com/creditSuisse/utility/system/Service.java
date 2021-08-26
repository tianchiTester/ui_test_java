/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.utility.system;

import java.io.IOException;
import java.util.Scanner;
import java.lang.Process;

/**
 * Helper for checking services installed and running
 * @author Edward Guo
 * @since 11/20/2019
 */
public class Service {

    private static Runtime runtime;
    static {
        runtime = Runtime.getRuntime();
    }

    /**
     * checks if a service is installed on the system
     * @param serviceName targeted Service
     * @return if installed true, else false
     */
    public static boolean isInstalled(String serviceName){
        Process process = null;
        try {
            process = runtime.exec("sc query " + serviceName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scanner reader = new Scanner(process.getInputStream(), "UTF-8");
        try {
            while (reader.hasNextLine()) {
                if (reader.nextLine().contains(serviceName))
                    return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * checks if a service is running on system
     * @param serviceName targeted Service
     * @return if running true, else false
     */
    public static boolean isRunning(String serviceName) {
        Process process = null;
        try {
            process = runtime.exec("sc query " + serviceName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scanner reader = new Scanner(process.getInputStream(), "UTF-8");
        try {
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (line.contains("STATE              : 4  RUNNING"))
                    return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
}
