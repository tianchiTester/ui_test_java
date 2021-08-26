/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.utility.system.information;

import com.creditSuisse.utility.file.Reader;
import com.creditSuisse.utility.system.CommandPrompt;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Information utility that retrieve system information
 * @author Edward Guo
 * @since 11/27/2019
 */
public class Information {

    /**
     * acquires system information from a registry table=
     * @return system info from registry table
     */
    public static SystemInformation getSystemInformation() {
        String command = "REG QUERY \"HKEY_LOCAL_MACHINE\\SOFTWARE\\HP\\system Properties\"";
        List<String> properties = CommandPrompt.getCommandResult(command);
        SystemInformation result = new SystemInformation();
        for (int i = 2; i < properties.size(); i++) {
            String[] splited = properties.get(i).split("REG_SZ");
            String itemName = splited[0].trim().toLowerCase();
            String itemValue = splited[1].trim();
            switch (itemName.trim()) {
                case "release":
                    result.releaseCycle = itemValue;
                    break;
                case "hardwaretype":
                    result.hardwareType = itemValue;
                    break;
                case "buildid":
                    result.buildId = itemValue;
                    break;
                case "platformcode":
                    result.productLineCode = itemValue;
                    break;
                case "productname":
                    result.productName = itemValue;
                    break;
                case "internalPlatformName":
                    result.internalPlatformName = itemValue;
                    break;
                case "internalPlatformVersion":
                    result.internalPlatformVersion = itemValue;
                    break;
                case "segment":
                    result.segment = itemValue;
                    break;
                case "regioncode":
                    result.regionShipped = itemValue;
                    break;
                default:
                    break;
            }
        }
        String rStoneFilePath = "c:\\hp\\bin\\rstone.ini";
        if (Reader.exists(rStoneFilePath)) {
            List<String> lines = Reader.readFileLines(rStoneFilePath, Charset.defaultCharset());
            for (String line : lines) {
                if (line.contains("FeatureByte")) {
                    result.featureByte = line.split("=")[1];
                    break;
                }
            }
        }
        return result;
    }

    /**
     * retrieves operating system information through wmic CMD
     * @return operating system information
     */
    public static OperatingSystemInformation getOSInformation(){
        OperatingSystemInformation result = new OperatingSystemInformation();
        result.name = CommandPrompt.getCommandResult("cmd /c wmic os get \"Caption\" | find /v \"Caption\"").get(0).trim();
        result.version = getWindowsVersionInfo().winVer;
        result.windowsID = CommandPrompt.getCommandResult("cmd /c wmic os get \"SerialNumber\" | find /v \"SerialNumber\"").get(0).trim();
        result.osType = CommandPrompt.getCommandResult("cmd /c wmic os get \"OSArchitecture\" | find /v \"OSArchitecture\"").get(0).split("-")[0].trim();
        result.operatingSystemSKU = CommandPrompt.getCommandResult("cmd /c wmic os get \"OperatingSystemSKU\" | find /v \"OperatingSystemSKU\"").get(0).trim();
        result.osRelease = CommandPrompt.getCommandResult("cmd /c powershell (Get-ItemProperty -Path ‘HKLM:\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion’).ReleaseId").get(0).trim();
        return result;
    }

    /**
     * retrieves windows version information through CMD
     * @return windows version information
     */
    public static WindowsVersionInformation getWindowsVersionInfo() {
        WindowsVersionInformation winVerInformation = new WindowsVersionInformation();
        Process process;
        String line = null;
        process = CommandPrompt.runWithReturnProcess("cmd /c ver");
        BufferedReader verReader = null;
        if (process != null) {
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            verReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        }
        while (true) {
            try {
                if ((line = verReader.readLine()) == null)
                    break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (line.contains("Version")) {
                line = line.split("Version")[1].trim();
                line = line.substring(0, line.length() - 1);
                winVerInformation.winVer = line;
            }
        }
        return winVerInformation;
    }
}
