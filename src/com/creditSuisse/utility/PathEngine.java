/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.utility;

import com.creditSuisse.ui.common.UIEnvVariables;
import com.creditSuisse.utility.constants.TemplateType;
import com.creditSuisse.utility.file.FileFilterByName;
import com.creditSuisse.utility.file.Reader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Path engine that helps to retrieve package and directory paths
 * @author Edward Guo
 * @since 11/4/2019
 * @todo Deprecate this class into Constants or .properties files as needed
 */
public class PathEngine {

    private static String executionDirectory = null;

    /**
     * retrieves the nircmd tool path
     * @return path to the nircmd tool
     */
    public static String getNircmdToolFilePath() {
         return getTestExecutionDirectory() + "\\src\\com\\hp\\resources\\tools\\nircmd.exe";
    }

    /**`
     * retrieves the test execution directory name
     * @return directory name of test execution
     */
    private static String getTestExecutionDirectory() {
        if (executionDirectory == null) {
                try {
                executionDirectory = new File(".").getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return executionDirectory;
    }

    /**
     * retrieves the automation package type directory path
     * @param packageType type of automation running
     * @return path to the package directory
     */
    public static String getPackageExecutionDirectory(TemplateType packageType) {
        return getTestExecutionDirectory() + "\\src\\com\\hp\\" + packageType.toString().toLowerCase();
    }

    /**
     * App package directory in string form
     * @return string of app package directory name on the system
     */
    public static String getOMENPackageDirectory() {
        return UIEnvVariables.inst().getUserPath() +
                "\\AppData\\Local\\Packages\\AD2F1837.OMENCommandCenter_v10z8vjag6ke6\\";
    }

    /**
     * returns an invalid HP Inc directory that is usually deleted
     * @return string name of invalid directory
     */
    public static String getInvalidHPIncDirectory() {
        return UIEnvVariables.inst().getUserPath() + "\\AppData\\Local\\HP_Inc\\";
    }

    /**
     * gets the string name of the Omen HP Inc directory within package
     * @return string of directory
     */
    public static String getValidHPIncDirectory() {
        return UIEnvVariables.inst().getUserPath() +
                "\\AppData\\Local\\Packages\\AD2F1837.OMENCommandCenter_v10z8vjag6ke6\\LocalCache\\Local\\HP_Inc\\";
    }

    /**
     * retrieves the testdata resource folder path
     * @return path to testdata resource directory
     */
    public static String getTestDataResourceFolderPath() {
        return executionDirectory + "\\src\\com\\hp\\resources\\testdata\\Resource";
    }

    /**
     * retrieves the testdata folder path
     * @return path to testdata directory
     */
    public static String getTestDataDirectory() {
        return getTestExecutionDirectory() + "\\src\\com\\hp\\resources\\testdata";
    }

    /**
     * gets the configuration file path for Omen
     * @return returns the path to config file
     */
    public static String getConfigFilePath() {
        List<File> configFiles = new ArrayList<>();
        Reader.getSpecFolderFiles(getValidHPIncDirectory(), configFiles, new FileFilterByName("OmenCommandCenterBackgrou_Url", true));
        for (File configFile : configFiles) {
            if (configFile.getName().equals("user.config"))
                return configFile.getAbsolutePath();
        }
        return null;
    }

    public static String getUIConfigFilePath() {
        List<File> configFiles = new ArrayList<>();
        Reader.getSpecFolderFiles(getValidHPIncDirectory(), configFiles, new FileFilterByName("OmenCommandCenterBackgrou", true));
        for (File configFile : configFiles) {
            if (configFile.getName().equals("user.config"))
                return configFile.getAbsolutePath();
        }
        return null;
    }
    /**
     * gets the HP Inc Directory folder
     * @return returns the file for the HP Inc background
     */
    public static File getBackFolder() {
        return new File(getValidHPIncDirectory() + "\\OmenCommandCenterBackgrou_Url_*");
    }

    /**
     * retrieves the environment.properties file of the specified package type
     * @param packageType the specified package type
     * @return environment property file path
     */
    public static String getEnvironmentPropertyFilePath(TemplateType packageType) {
        String configDirectory = getTestExecutionDirectory() + "\\src\\com\\hp\\resources\\config";
        switch (packageType) {
            case Analytics:
                return configDirectory + "\\hpa_environment.properties";
            case API:
                return configDirectory + "\\api_environment.properties";
            case Performance:
                return configDirectory + "\\performance_environment.properties";
            case UI:
                return configDirectory + "\\ui_environment.properties";
            case Log:
                return configDirectory + "\\log4j.properties";
            default:
                return null;
        }
    }

    public static String getOMENDB(){
        return "jdbc:sqlite:" + UIEnvVariables.inst().getUserPath() +
                "\\AppData\\Local\\Packages\\AD2F1837.OMENCommandCenter_v10z8vjag6ke6\\LocalCache\\Local\\HPOmenServices\\omen.db";
    }
}