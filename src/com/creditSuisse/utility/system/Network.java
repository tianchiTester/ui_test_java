/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.utility.system;

import com.creditSuisse.utility.LogManager;

import java.io.IOException;

/**
 * Network utility for proxies, wifi connections, ethernet actions and network
 * interfaces on a windows system so that a user can modify network connections
 * @author Eric Tyree
 * @since 11/26/2019
 */
public class Network {

    /**
     * sets a network interface via CMD by netsh inteface name
     * @param name name of interface to set
     * @param enabled if the interface should be enabled/disabled
     * @return true if CMD successful setting interface, false otherwise
     */
    public static boolean setNetworkInterface(String name, boolean enabled) {
        String command = "netsh interface set interface " + name;
        if (enabled)
            command += " enable";
        else
            command += " disable";
        return CommandPrompt.elevate(command);
    }

    public static void setProxyAddress(String proxyAddress){
        String internetSettingsKey = "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings";
        Registry.updateValue(internetSettingsKey, "ProxyServer", "REG_SZ", proxyAddress);
        LogManager.logProduct.info("set Proxy Address:"+proxyAddress);
    }

    public static String getProxyAddress(){
        String internetSettingsKey = "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings";
        return Registry.queryRegistry(internetSettingsKey, "ProxyServer");
    }

    public static boolean proxyIsEnable(){
        String internetSettingsKey = "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings";
        String value = Registry.queryRegistry(internetSettingsKey, "ProxyEnable");
        return value.equals("0x1");
    }

    /**
     * enables a configured proxy on current network
     * Make sure you have the proxy properly configured in .properties
     */
    public static void enableProxy() {
        String internetSettingsKey = "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings";
        Registry.updateValue(internetSettingsKey, "ProxyEnable", "REG_DWORD", "1");
    }

    /**
     * disables a configured proxy on current network
     */
    public static void disableProxy() {
        String internetSettingsKey = "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings";
        Registry.updateValue(internetSettingsKey, "ProxyEnable", "REG_DWORD", "0");
    }





    /**
     * attempts to connect to wifi ssid provided
     * @param ssid wifi identifier to connect to
     * @param profileName name of wifi connection
     * @return boolean depending on the success of connection
     */
    public static boolean connectToWifi(String ssid, String profileName) {
        String[] ssids = ssid.split(",");
        String[] profiles = profileName.split(",");
        boolean result = false;
        for (int i = 0; i < ssids.length; i++) {
            String command = "netsh wlan connect ssid=" + ssids[i] + " name=" + profiles[i];
            result = CommandPrompt.elevate(command);
            if (!result)
                return result;
            Time.suspend(3);
        }
        return result;
    }





    /*Turn off internet connection*/
    public static void turnOffInternet() throws IOException {
        try {
            Runtime.getRuntime().exec("cmd /c ipconfig /release");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*Bring back the internet connection*/
    public static void bringBackInternet() throws IOException {
        try {
            Runtime.getRuntime().exec("cmd /c ipconfig /renew");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
