/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.utility.file;

import com.creditSuisse.utility.system.Time;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * File monitor for waiting for file creation, sorting files, etc.
 * @author Eric Tyree
 * @since 11/20/2019
 */
public class Monitor {

    /**
     * waits for a timeout period until specified file/extension appears
     * @param folderPath path to specified folder
     * @param fileNameOrExtension file name/extension
     * @param timeout time to wait in seconds
     * @return true if file appears in folderPath before timeout, else false
     */
    public static boolean waitForFileInFolder(String folderPath, String fileNameOrExtension, int timeout) {
        for (int i = 0; i < timeout / 2; i++) {
            List<File> currentFiles = Reader.getFolderFiles(folderPath);
            List<File> targetFiles = filterFilesByName(currentFiles, new FileFilterByName(fileNameOrExtension, true));
            if (targetFiles.size() > 0)
                return true;
            else
                Time.suspend(2);
        }
        return false;
    }

    /**
     * waits for a timeout period until folder count is passed
     * @param folderPath path to specified folder
     * @param count expected file count
     * @param timeout time to wait in seconds
     * @param filter specified file filter instance/array
     * @return true if file count is reached before timeout, else false
     */
    public static boolean waitForFilesInFolderReachCount(String folderPath, int count, int timeout, FileFilterByName... filter) {
        for (int i = 0; i < timeout; i++) {
            List<File> currentFiles = Reader.getFolderFiles(folderPath);
            List<File> targetFiles = filterFilesByName(currentFiles, filter);
            if (targetFiles.size() >= count)
                return true;
            else
                Time.suspend(1);
        }
        return false;
    }

    /**
     * waits for a timeout period and then folder count is equal
     * @param folderPath path to specified folder
     * @param count expected file count
     * @param timeout time to wait in seconds
     * @param filter specified file filter instance/array
     * @return true if file count is equal to count, else false
     */
    public static boolean waitForFilesInFolderEqualCount(String folderPath, int count, int timeout, FileFilterByName... filter) {
        for (int i = 0; i < timeout; i++) {
            Time.suspend(1);
        }
        List<File> currentFiles = Reader.getFolderFiles(folderPath);
        List<File> targetFiles = filterFilesByName(currentFiles, filter);
        return targetFiles.size() == count;
    }

    /**
     * waits for a timeout period for a file to appear
     * @param filePath path to specified file
     * @param appear appear boolean
     * @param timeout time to wait in seconds
     */
    public static void waitForFile(String filePath, boolean appear, int timeout) {
        int duration = 10;
        int times = timeout / duration + 1;
        if (times <= 1) times = 2;
        for (int i = 0; i < times; i++) {
            File file = new File(filePath);
            if (file.exists() ^ appear)
                Time.suspend(duration);
            else
                break;
        }
    }

    /**
     * filters files list by filters paramater
     * @param files List of files to be filtered
     * @param filters provided filters
     * @return filtered List of files
     */
    public static List<File> filterFilesByName(List<File> files, FileFilterByName... filters) {
        if (filters.length == 0)
            return files;
        List<File> result = new ArrayList<>();
        for (File file : files) {
            for (FileFilterByName filter : filters) {
                String fileName = file.getName();
                if (fileName.contains(filter.name) ^ filter.include)
                    continue;
                else
                    result.add(file);
            }
        }
        return result;
    }

    /**
     * sorts all file paths by ascending/descending creation time
     * @param toSort file paths' list to sort
     * @param ascending a boolean indicates ascendingly or descendingly
     * @return the sorted files' path
     */
    public static List<String> sortFilesPathByCreationTime(List<String> toSort, boolean ascending) {
        toSort.sort(new Comparator() {
            @Override
            public int compare(Object firstObj, Object secondObj) {
                BasicFileAttributes basicAttributesOne = null;
                BasicFileAttributes basicAttributesTwo = null;
                File fileOne = new File(firstObj.toString());
                File fileTwo = new File(secondObj.toString());
                try {
                    basicAttributesOne = Files.readAttributes(fileOne.toPath(), BasicFileAttributes.class);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
                try {
                    basicAttributesTwo = Files.readAttributes(fileTwo.toPath(), BasicFileAttributes.class);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
                if (ascending) {
                    if (basicAttributesOne.creationTime().toMillis() > basicAttributesTwo.creationTime().toMillis())
                        return 1;
                    else if (basicAttributesOne.creationTime().toMillis() == basicAttributesTwo.creationTime().toMillis())
                        return 0;
                    else
                        return -1;
                } else {
                    if (basicAttributesOne.creationTime().toMillis() < basicAttributesTwo.creationTime().toMillis())
                        return 1;
                    else if (basicAttributesOne.creationTime().toMillis() == basicAttributesTwo.creationTime().toMillis())
                        return 0;
                    else
                        return -1;
                }
            }
        });
        return new ArrayList<>(toSort);
    }

    /**
     * sorts all files by ascending/descending creation time
     * @param toSort file paths List
     * @param ascending ascending (true) or descending (false)
     * @return List of sorted files by creation time
     */
    public static List<File> sortFilesByCreationTime(List<File> toSort, boolean ascending) {
        toSort.sort(new Comparator() {
            @Override
            public int compare(Object firstObj, Object secondObj) {
                BasicFileAttributes basicAttributesOne = null;
                BasicFileAttributes basicAttributesTwo = null;
                File fileOne = (File) firstObj;
                File fileTwo = (File) secondObj;
                try {
                    basicAttributesOne = Files.readAttributes(fileOne.toPath(), BasicFileAttributes.class);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
                try {
                    basicAttributesTwo = Files.readAttributes(fileTwo.toPath(), BasicFileAttributes.class);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
                if (ascending) {
                    if (basicAttributesOne.creationTime().toMillis() > basicAttributesTwo.creationTime().toMillis())
                        return 1;
                    else if (basicAttributesOne.creationTime().toMillis() == basicAttributesTwo.creationTime().toMillis())
                        return 0;
                    else
                        return -1;
                } else {
                    if (basicAttributesOne.creationTime().toMillis() < basicAttributesTwo.creationTime().toMillis())
                        return 1;
                    else if (basicAttributesOne.creationTime().toMillis() == basicAttributesTwo.creationTime().toMillis())
                        return 0;
                    else
                        return -1;
                }
            }
        });
        return new ArrayList<>(toSort);
    }
}
