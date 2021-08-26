/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.utility.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Processer helper for killing and checking on any process
 * @author Nicole Zhang
 * @since 11/26/2019
 */
public class Processer {

    /**
     * checks if a certain process is running on system
     * @param processName targeted process
     * @return if the process is running return true, else return false
     */
    public static boolean checkRunning(String processName) {
        String line;
        Process process;
        try {
            process = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe");
            assert process != null;
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = input.readLine()) != null) {
                if (line.contains(processName))
                    return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * kills a specific process on system
     * @param processName name of targeted process
     * @throws IOException if kill CMD fails
     */
    public static void kill(String processName) {
        try {
            Runtime.getRuntime().exec("TASKKILL /F /IM " + processName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * executes specified process and returns a List of processes
     * @param processName specified process name
     * @return List of targeted running processes
     */
    public static List<String> getRunningProcesses(String processName) {
        String command = "tasklist /FI \"IMAGENAME eq " + processName + "\"";
        return CommandPrompt.getCommandResult(command);
    }

    public static void kills(String processName){
        for(String processes : getRunningProcesses(processName)){
            kill(processName);
        }
    }
}
