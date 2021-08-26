/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.utility.file;

import com.creditSuisse.utility.system.CommandPrompt;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * File writer for creating and appending files
 * @author Eric Tyree
 * @since 11/20/2019
 */
public class Writer {

    /**
     * creates a file at the target location, remove the existing file
     * @param filePath path to file to be created
     * @return true indicates success and false indicates failure
     */
    public static boolean createFile(String filePath) {
        boolean result = false;
        File accountFile = new File(filePath);
        try {
            if (accountFile.exists()) {
                accountFile.delete();
                result = accountFile.createNewFile();
            } else
                result = accountFile.createNewFile();
        } catch (IOException e) {
                e.printStackTrace();
        }
        return result;
    }

    /**
     * create a file directory in a specified path
     * @param directoryPath the target directory path
     * @return true indicates success and false indicates failure
     */
    public static boolean createDirectory(String directoryPath) {
        if (new File(directoryPath).exists())
            return true;
        else
            return new File(directoryPath).mkdirs();
    }

    /**
     * adds content to specified file
     * @param filePath path to targeted file
     * @param newContent content to add to file
     * @param isAppend appending (true) or re-write (false)
     */
    public static void writeContentToFile(String filePath, String newContent, boolean isAppend) {
        FileWriter writer;
        try {
            writer = new FileWriter(filePath, isAppend);
            writer.write(newContent);
            writer.write("\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * deletes a directory of a specified path
     * @param directoryPath path to delete
     * @return true if successful, else false
     */
    public static boolean removeDirectory(String directoryPath) {
        try {
            FileUtils.forceDelete(new File(directoryPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * deletes all sub-directories/files under one directory path
     * @param directoryPath path to delete
     * @return true if successful, else false
     */
    public static boolean removeDirectoryContents(String directoryPath) {
        String command = "cmd /c del /q \"" + directoryPath + "\\*\"" + " && for /d %x in (\"" + directoryPath + "\\*\") do @rd /s /q \"%x\"";
        return CommandPrompt.elevate(command);
    }

    /**
     * moves a file from src path to dest path
     * @param sourceFilePath path to src file
     * @param destPath path to dest folder
     * @return true if successful, else false
     */
    public static boolean moveFile(String sourceFilePath, String destPath) {
        String command = "cmd /c move \"" + sourceFilePath + "\"" + " \"" + destPath + "\"";
        return CommandPrompt.elevate(command);
    }

    /**
     * copies all files or sub-directories from src to dest
     * @param sourceFilePath path to src directory
     * @param destFolderPath path to dest directory
     * @return true if successful, else false
     */
    public static boolean copyFiles(String sourceFilePath, String destFolderPath) {
        String command = "cmd /c xcopy \"" + sourceFilePath + "\"" + " \"" + destFolderPath + "\"" + " /E /H /K /C /Y";
        return CommandPrompt.elevate(command);
    }
}
