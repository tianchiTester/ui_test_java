/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.utility.constants;

/**
 * This class is used to define constants file path and wait time.
 *
 * @author Nicole Zhang
 * @since 12/13/2019
 */
public class Constants {

    /*----------------------------WAIT TIME IN SECONDS---------------------------------*/
    public static final int Sec_1 = 1;

    public static final int Sec_2 = 2;
    public static final int Sec_3 = 3;
    public static final int Sec_5 = 5;
    public static final int Sec_10 = 10;
    public static final int Sec_30 = 30;
    public static final int Min_1 = 60;
    public static final int Min_2 = 120;
    public static final int APPIUM_TIMEOUT = 30;

    /*----------------------------PATH---------------------------------*/
    public static final String PATH_WIN_APP_DRIVER = "C:\\Program Files (x86)\\Windows Application Driver";
    public static final String PATH_CHROME_DOWNLOAD = "\\src\\com\\creditSuisse\\resources\\downloads";

    public static final String PATH_CMD = "C:\\Windows\\System32\\cmd.exe";
    public static final String PATH_ACCOUNT_TABLE = "src\\com\\creditSuisse\\resources\\testdata\\Account.xlsx";
    public static final String PATH_LOG = "src\\com\\creditSuisse\\resources\\config\\log4j.properties";


    /*----------------------------Configure File PATH---------------------------------*/
    public static final String PATH_LOG4J = "src\\com\\creditSuisse\\resources\\config\\log4j.properties";
    public static final String PATH_ENVIRONMENT = "src\\com\\creditSuisse\\resources\\config\\environment.properties";
    public static final String PATH_APIENVIRONMENT = "src\\com\\creditSuisse\\resources\\config\\api_environment.properties";
    public static final String PATH_EMAIL_CONFIG = "src\\com\\creditSuisse\\resources\\config\\";

    /*----------------------------APPLE File PATH---------------------------------*/
    public static final String APPLE_PATH_LOG4J = "src/com/creditSuisse/resources/config/log4j.properties";
    public static final String APPLE_ENV_PATH = "src/com/creditSuisse/resources/config/environment.properties";
    public static final String APPLE_APIENV_PATH = "src/com/creditSuisse/resources/config/api_environment.properties";
    // ---------------------------- ELEMENTS ----------------------------
    public static final String ENVIRONMENT = "TEST";
    /*---------Registry ----------------*/
    public static final String DEG_DWORD = "REG_DWORD";
    public static final String REG_SZ = "REG_SZ";
    public static final String Name_DeviceCheckOverride = "DeviceCheckOverride";
    public static final String Path_OmenAllySettings = "HKCU\\Software\\HP\\Omen Ally\\Settings";
    public static final String Path_Desktop = "HKCU\\Control Panel\\Desktop";
    public static final String Name_WallPaper = "WallPaper";


    public static String platSlash = "\\";

}
