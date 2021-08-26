/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 *  The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 *
 */

package com.creditSuisse.ui.common;

import com.creditSuisse.utility.constants.Constants;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Common class to get environment properties.
 * @author Eric Tyree
 * @since 12/13/2019
 */
public class UIEnvVariables {

    private static UIEnvVariables envVariables = null;
    private static Object lockObject = new Object();
    private String openReport;
    private String resourcePath;
    private String windowsUrl;
    private String proxy;
    private String platform;
    private String appPackageName;

    private String stack;
    private String language;
    private String isChina;
    private String currentUserPath;
    private String environment;
    Properties properties = null;

    public UIEnvVariables() {
        FileInputStream fileInputStream = null;
        try {
            if(Constants.platSlash.equals("\\"))
                fileInputStream = new FileInputStream(new File(Constants.PATH_ENVIRONMENT));
            else
                fileInputStream = new FileInputStream(new File(Constants.APPLE_ENV_PATH));
            properties = new Properties();
            properties.load(fileInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        currentUserPath = properties.getProperty("UserPath");
        windowsUrl = properties.getProperty("WindowsURL");
        platform = properties.getProperty("Platform");
        openReport = properties.getProperty("OpenReport");
        proxy = properties.getProperty("Proxy");
        resourcePath = properties.getProperty("ResourcePath");
        appPackageName = properties.getProperty("WinApp_Package_Name");

        stack=properties.getProperty("Stack");


    }

    public static UIEnvVariables inst() {
        if (envVariables == null) {
            synchronized (lockObject) {
                if (envVariables == null) {
                    envVariables = new UIEnvVariables();
                }
            }
        }
        return envVariables;
    }

    /*-------------Oasis---------------------*/
    public String getOasisBuildPath()
    {
        return properties.getProperty("OasisBuildPath");
    }


    public String getUserPath() {
        return currentUserPath;
    }

    public String getPlatform() {
        return platform;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public String getOpenReport() {
        return openReport;
    }

    public String getProxy() {
        return proxy;
    }

    /******************Windows****************************/
    public String getWindowsUrl() {
        return windowsUrl;
    }



    /******************Web*****************************/


    public String getStack() {
        return stack;
    }


    /******************Android****************************/


    public String getAppPackageName() {
        return appPackageName;
    }



    /****************Network Related****************************/





    public String getEnvironmentName(){return properties.getProperty("Environment");}

    public String getStackVal(){
        if(System.getProperty("Stack")==null || System.getProperty("Stack").isEmpty()){
            stack = properties.getProperty("Stack");
            getStack();
        }else{
            stack = System.getProperty("Stack");
        }
        return stack;
    }
    public String getEnvironmentVal(){
        if(System.getProperty("Environment")==null || System.getProperty("Environment").isEmpty()){
            environment = properties.getProperty("Environment");
            getEnvironmentName();
        }else{
            environment = System.getProperty("Environment");
        }
        return environment;
    }
}
