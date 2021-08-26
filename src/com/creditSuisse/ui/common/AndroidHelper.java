/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.ui.common;

import com.creditSuisse.utility.ExceptionHandling;
import com.creditSuisse.utility.LogManager;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Group of common functions to get connected android device info
 * (dependencies - ADB installed and configured on local system path)
 * @author Nicole Zhang
 * @since 11/20/2019
 */
public class AndroidHelper {

    private static Process process;
    private static Runtime runTm = Runtime.getRuntime();

    /**
     * gets the UDID of connected android device
     * @throws IOException if reading the adb cmd fails or is null
     * @return Device Array including device version and model
     */
    public static JSONArray getConnectedAndroidDevice() throws IOException {
        ArrayList devices = new ArrayList();
        JSONArray deviceArray = new JSONArray();
        String commands = "adb devices";
        process = runTm.exec(commands);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String deviceUDID;
        while ((deviceUDID = stdInput.readLine()) != null && !deviceUDID.equals("")) {
            deviceUDID = deviceUDID.replace("\tdevice", "");
            devices.add(deviceUDID);
        }
        LogManager.logFramework.info("Devices: \n" + devices);
        while ((deviceUDID = stdError.readLine()) != null) {
            LogManager.logFramework.info(deviceUDID);
        }
        devices.remove(0);
        for (Object device : devices) {
            JSONObject deviceObject = new JSONObject();
            String getModel = "adb -s " + device.toString() + " shell getprop ro.product.model";
            process = runTm.exec(getModel);
            stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((deviceUDID = stdInput.readLine()) != null && !deviceUDID.equals("")) {
                deviceObject.put("model", deviceUDID);
            }
            String getVersion = "adb -s " + device.toString() + " shell getprop ro.build.version.release";
            process = runTm.exec(getVersion);
            stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((deviceUDID = stdInput.readLine()) != null && !deviceUDID.equals("")) {
                deviceObject.put("version", deviceUDID);
            }
            deviceArray.put(deviceObject);
        }
        LogManager.logFramework.info(deviceArray);
        return deviceArray;
    }

    /**
     * gets the first connected android device version
     * @throws IOException if reading adb cmd fails or is null
     * @throws ExceptionHandling if fails to get the device version (devices not connected)
     * @return connected android device version
     */
    public static String getDeviceVersion() throws IOException, ExceptionHandling {
        String version = "";
        String command = "adb shell getprop ro.build.version.release";
        process = runTm.exec(command);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = stdInput.readLine()) != null && !line.equals("")) {
            version = line;
        }
        LogManager.logFramework.info("Android device version is: " + version);
        if (version.equals(""))
            throw new ExceptionHandling("Failed to get android device version, or device not connected");
        return version;
    }

    /**
     * gets the first connected android device model
     * @throws IOException if reading the adb cmd fails or is null
     * @throws ExceptionHandling if fails to get the version (devices not connected)
     * @return connected android device model
     */
    public static String getDeviceModel() throws IOException, ExceptionHandling {
        String model = "";
        String command = "adb shell getprop ro.product.model";
        process = runTm.exec(command);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = stdInput.readLine()) != null && !line.equals("")) {
            model = line;
        }
        LogManager.logFramework.info("Android device model is: " + model);
        if (model.equals(""))
            throw new ExceptionHandling("Failed to get android device version, or device not connected");
        return model;
    }

    /**
     * gets first connected android device UDID
     * @throws ExceptionHandling if fails to get the UDID (devices not connected)
     * @throws IOException if reading the adb cmd fails or is null
     * @return connected android device UDID
     */
    public static String getUDID() throws ExceptionHandling, IOException {
        String command = "adb shell getprop ro.serialno";
        process = runTm.exec(command);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String uDID;
        while ((uDID = stdInput.readLine()) != null && !uDID.equals("")) {
            return uDID;
        }
        LogManager.logFramework.info("Android device UDID is: " + uDID);
        if (uDID.equals(""))
            throw new ExceptionHandling("Failed to get android device version, or device not connected");
        return uDID;
    }
}
