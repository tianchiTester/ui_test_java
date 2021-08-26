/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.utility.system;

import com.creditSuisse.utility.CommonUtility;
import com.creditSuisse.utility.PathEngine;
import com.creditSuisse.utility.constants.ComparisonCategory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.lang.Process;

/**
 * Helper for executing CMD commands
 * @author Edward Guo
 * @since 12/03/2019
 */
public class CommandPrompt {

    private static Runtime runtime;

    static { runtime = Runtime.getRuntime(); }

    /**
     * Runs a command from either mac or windows cmds and gets output
     * @param command command to run from cmd or terminal
     * @return String output of the command
     * @throws IOException if the String output from the cmd does not exist
     */
    public static String runCommand(String command) throws IOException {
        Process process;
        ProcessBuilder builder;
        String operatingSys = System.getProperty("os.name");
        // build cmd process according to os
        if(operatingSys.contains("Windows")) { // if windows
            builder = new ProcessBuilder("cmd.exe","/c", command);
            builder.redirectErrorStream(true);
            com.creditSuisse.utility.system.Time.suspend(1);
            process = builder.start();
        } else { // if Mac
            // TODO - Need RnD for mac machine
            String[] temp = { "Android/sdk/platform-tools/adb", "dummy_command"};
            temp[1] = command;
            process = Runtime.getRuntime().exec(temp);
        }
        // get std output
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        String allLine="";
        //int index=1;
        while((line=reader.readLine()) != null) {
            //LogManager.logFramework.info("Command Prompt: " + index + ". " + line);
            allLine = allLine + "" + line + "\n";
            if(line.contains("WSC Service"))
                break;
            //index++;
        }
        return allLine;
    }

    /**
     * executes a command for the CMD to run
     * @param command CMDPrompt command to run (i.e. 'dir' or 'cd Doc')
     * @return true if command successful, false otherwise
     */
    public static boolean execute(String command) {
        return run(command);
    }

    /**
     * executes an elevated scope command for the elevated CMD to run
     * @param command CMDPrompt command to run
     * @return true if elevated command successful, false otherwise
     */
    public static boolean elevate(String command) {
        return run(PathEngine.getNircmdToolFilePath() + " elevate " + command);
    }

    /**
     * executes a command and acquires the Processer result of that command
     * @param command command to execute and get the Processer result from
     * @return Processer result of the cmd that is executed
     */
    public static Process runWithReturnProcess(String command) {
        Process process = null;
        try {
            process = runtime.exec(command);
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return process;
    }

    /**
     * gets the output in a list of lines for the provided command
     * after executing it
     * @param command targeted command text to execute and get results for
     * @return List of string results from the command provided
     */
    public static List<String> getCommandResult(String command) {
        List<String> result = new ArrayList<>();
        Process process;
        String line;
        try {
            process = runWithReturnProcess(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = reader.readLine()) != null) {
                if (line.trim().equals(""))
                    continue;
                result.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * runs a provided command through the CMD and evaluates its success
     * @param command command to execute through Command Prompt
     * @return true if command executes successfully, false otherwise
     */
    private static boolean run(String command) {
        int result;
        try {
            runtime = Runtime.getRuntime();
            Process process = runtime.exec(command);
            result = process.waitFor();
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        //TODO evaluate sleep command necessity
        com.creditSuisse.utility.system.Time.suspend(2);
        return result == 0;
    }

    /**
     * waits for timeout period so running processes' count == expectedCount
     * @param comparisonCategory the comparison category
     * @param expectedCount the expected count
     * @param timeout time out seconds
     */
    public static void waitForCMDProcessCount(ComparisonCategory comparisonCategory, int expectedCount, int timeout) {
        int time = 0;
        while (time <= timeout) {
            List<String> runningProcesses = Processer.getRunningProcesses("cmd.exe");
            if (CommonUtility.checkCriteria(comparisonCategory, runningProcesses.size(), expectedCount))
                return;
            com.creditSuisse.utility.system.Time.suspend(5);
            time += 5;
        }
    }
}
