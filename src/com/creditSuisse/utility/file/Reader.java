/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.utility.file;

import com.creditSuisse.utility.system.Time;
import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static java.lang.String.format;
import static org.testng.Assert.fail;

/**
 * Reads, finds, and evaluates files and directories
 * @author Eric Tyree
 * @since 11/20/2019
 */
public class Reader {

    /**
     * reads and returns all file contents
     * @param path path to the file
     * @return An array list containing all file contents
     */
    public static List<String> readFileContent(String path) {
        List<String> lines = new ArrayList<>();
        InputStreamReader inputReader = null;
        try {
            inputReader = new InputStreamReader(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader buffReader = new BufferedReader(inputReader);
        String line;
        try {
            line = buffReader.readLine();
            while (line != null) {
                lines.add(line);
                line = buffReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    /**
     * reads and returns all file bytes using the specified encoding format
     * @param path path to the file
     * @param encoding the specified encoding format
     * @throws IOException if the file bytes cannot be read or == null
     * @return content of file in encoded format
     */
    public static String readFileContent(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    /**
     * reads all file lines and return result in List of the specified encoding format
     * @param path path to the file
     * @param encoding the specified encoding format
     * @return List containing all the file lines
     */
    public static List<String> readFileLines(String path, Charset encoding) {
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get(path), encoding);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    /**
     * indicates whether the file exists on the specified path
     * @param filePath path to specified file
     * @return true if file exists, else false
     */
    public static boolean exists(String filePath) {
        return new File(filePath).exists();
    }

    /**
     * read content of the specified file into bytes
     * @param filePath path to specified file
     * @return bytes of file content
     */
    public static byte[] getFileBytes(String filePath) {
        Path path = Paths.get(filePath);
        if (path == null)
            fail(format("Couldn't find resource '%s'", filePath));
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * open a specified file
     * @param filePath path to specified file
     * @throws IOException if filePath cannot be opened or == null
     */
    public static void openFile(String filePath) throws IOException {
        Desktop desk = Desktop.getDesktop();
        File reportFile = new File(filePath);
        desk.open(reportFile);
    }

    /**
     * returns all file(s) under provided path
     * @param path the specified file/folder path
     * @param filesList the list of files at the folder
     * @return List of file(s) in the specified folder
     */
    public static List<File> getAllFolderFiles(String path, List<File> filesList) {
        List<File> allFiles;
        if (filesList == null)
            allFiles = new ArrayList<>();
        else
            allFiles = filesList;
        File file = new File(path);
        if (!file.isDirectory())
            allFiles.add(file);
        else {
            File[] files = file.listFiles();
            for (File value : files) {
                if (value.isFile())
                    allFiles.add(value);
                if (value.isDirectory())
                    getAllFolderFiles(value.getPath(), allFiles);
            }
        }
        return allFiles;
    }

    /**
     * returns specified files under a path by filter
     * @param path the specified file/folder path
     * @param filesList all files returned
     * @param filter filter for folder
     * @return a file ArrayList or a single file
     */
    public static List<File> getSpecFolderFiles(String path, List<File> filesList, FileFilterByName filter) {
        List<File> allFiles;
        if (filesList == null)
            allFiles = new ArrayList<>();
        else
            allFiles = filesList;
        File file = new File(path);
        if (!file.isDirectory())
            allFiles.add(file);
        else {
            File[] files = file.listFiles();
            for (File value : files) {
                if (value.isFile())
                    allFiles.add(value);
                if (value.isDirectory() && value.getName().contains(filter.name) ^ !filter.include)
                        getAllFolderFiles(value.getPath(), allFiles);
            }
        }
        return allFiles;
    }

    /**
     * wait for filter files to appear in folderPath for a timeout period
     * @param folderPath path to specified folder
     * @param timeout specified timeout in seconds
     * @param filter (optional) specified file-filter instance/array
     * @return true if no filter-files exist, else false
     */
    public static boolean folderHasNoFiles(String folderPath, int timeout, FileFilterByName... filter) {
        for (int i = 0; i < timeout; i++) {
            List<File> currentFiles = new ArrayList<>();
            getFolderAndSubFolderFiles(folderPath, currentFiles);
            List<File> targetFiles = Monitor.filterFilesByName(currentFiles, filter);
            if (targetFiles.size() > 0)
                return false;
            else
                Time.suspend(1);
        }
        return true;
    }

    /**
     * gets all file(s) under one folder
     * @param folderPath path to specified folder
     * @return List of file(s) in folderPath
     */
    public static List<File> getFolderFiles(String folderPath) {
        List<File> result = new ArrayList<>();
        File folder = new File(folderPath);
        if (!folder.isDirectory())
            result.add(folder);
        else {
            File[] folderFiles = folder.listFiles();
            result.addAll(Arrays.asList(folderFiles));
        }
        return result;
    }

    /**
     * gets a List of files from the folder and all sub-folders
     * @param folderPath the specified file/folder path
     * @param files List of files in folderPath/sub-folders
     */
    public static void getFolderAndSubFolderFiles(String folderPath, List<File> files) {
        File folder = new File(folderPath);
        if (!folder.isDirectory())
            files.add(folder);
        else {
            File[] folderFiles = folder.listFiles();
            if (folderFiles != null) {
                for (File folderFile : folderFiles) {
                    if (folderFile.isFile())
                        files.add(folderFile);
                    if (folderFile.isDirectory())
                        getFolderAndSubFolderFiles(folderFile.getAbsolutePath(), files);
                }
            }
        }
    }
}
