/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.api.common;

import com.creditSuisse.utility.constants.Constants;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * API Environment Variables for API and UI related testing
 * @author Edward Guo
 * @since 12/17/2019
 */
public class APIEnvVariables {
    private static Object lockObject = new Object();
    private static APIEnvVariables apiEnvVariables = null;
    private String baseUrl = null;
    private String currentStack = null;
    private String protocol = null;

    private Properties prop = null;

    private APIEnvVariables() {

        FileInputStream fileInputStream = null;
        try {
            if(Constants.platSlash.equals("\\"))
                fileInputStream = new FileInputStream(new File(Constants.PATH_APIENVIRONMENT));
            else
                fileInputStream = new FileInputStream(new File(Constants.APPLE_APIENV_PATH));
            prop = new Properties();
            prop.load(fileInputStream);
            currentStack = getCurrentStack();
            if (currentStack == null) {
                throw new Exception("No stack is got, please check environment.properties");
            }
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
    }

    public String getBaseURL() {
        baseUrl = prop.getProperty(currentStack);
        return baseUrl;
    }

    public String getProtocol() {
        protocol = prop.getProperty("Protocol");
        return protocol;
    }

    public boolean getUseProxy() {
        return Boolean.parseBoolean(prop.getProperty("UseProxy"));
    }

    public String getCurrentStack() {
        currentStack = prop.getProperty("CurrentStack").toUpperCase();
        return currentStack;
    }

    public String getHpbpHost(){
        return prop.getProperty("HPBP_Host");
    }
    public String getHPBPHpbpClientId(){
        return prop.getProperty("HPBP_ClientId_" + prop.getProperty("CurrentStack").toUpperCase());
    }

    public String getRedirectUrl(){
        return prop.getProperty("RedirectUrl_" + prop.getProperty("CurrentStack").toUpperCase());
    }


    public static APIEnvVariables inst() {
        if (apiEnvVariables == null) {
            synchronized (lockObject) {
                if (apiEnvVariables == null) {
                    apiEnvVariables = new APIEnvVariables();
                }
            }
        }
        return apiEnvVariables;
    }
}
