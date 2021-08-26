/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.ui.common;

import com.creditSuisse.utility.constants.Constants;
import com.creditSuisse.utility.LogManager;
import com.creditSuisse.utility.system.Time;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServerHasNotBeenStartedLocallyException;
import io.appium.java_client.service.local.AppiumServiceBuilder;

/**
 * Appium service controller for launching and closing appium
 * @author Nicole Zhang
 * @since 11/20/2019
 */
public class AppiumHelper {

    public static AppiumDriverLocalService appiumService;

    /**
     * initializer for starting an appium service instance
     */
    public static void initiateAppiumServer() {
        AppiumServiceBuilder appiumServiceBuilder = new AppiumServiceBuilder();
        appiumServiceBuilder.withIPAddress("0.0.0.0");
        appiumServiceBuilder.usingAnyFreePort();
        appiumServiceBuilder.withArgument(() -> "--log-level", "error");
        appiumService = AppiumDriverLocalService.buildService(appiumServiceBuilder);
        appiumService.start();
        //Mechanism to wait for 30 seconds for getting response from service
        for(int i=1; i<Constants.APPIUM_TIMEOUT; i++) {
            LogManager.logFramework.info("Attempt[" + i + "] to start appium server");
            Time.suspend((int) Constants.Sec_1);
            if(appiumService.isRunning())
                break;
        }
        if (appiumService == null || !appiumService.isRunning())
            throw new AppiumServerHasNotBeenStartedLocallyException("An appium server node has not been started!");
        else
            LogManager.logFramework.info("Appium server started at: " +appiumService.getUrl());
    }

    /**
     * terminator for the active appium service instance
     */
    public static void terminateAppiumServer() {
        if(appiumService!=null)
            appiumService.stop();
    }
}
