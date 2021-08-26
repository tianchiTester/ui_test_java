/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.utility.system;

import com.creditSuisse.utility.LogManager;

import java.util.List;

/**
 * Common functions dealing with system registries
 * @author Edward Guo
 * @since 11/26/2019
 */
public class Registry {

    /**
     * query the registry by a path and name
     * @param path targeted path to query
     * @param name name of value
     * @return string output of the query
     */
    public static String queryRegistry(String path, String name) {
        String command = "REG QUERY \"" + path + "\" /v "+name;
        List<String> properties = CommandPrompt.getCommandResult(command);
        String values = properties.get(properties.size()-1);
        String[] valueArr = values.split(" ");
        return valueArr[valueArr.length - 1];
    }

    /**
     * add a key value to the windows registry
     * @param key key to add to registry
     * @param valueName name of value to add to registry
     * @param valueType type of value to add
     * @param valueData data value to add
     * @return true if successful in adding to registry, otherwise false
     */
    public static boolean addValue(String key, String valueName, String valueType, String valueData) {
        if(valueName == null || valueData == null || valueType == null){
            LogManager.logProduct.info("Registry key will not be added because one of the parameters is null: valueName, valueType, valueData.");
            return false;
        }
        String addCommand = "reg add \"" + key + "\" /v " + "\"" + valueName + "\"" + " /t " + "\"" + valueType + "\"" + " /d " + "\"" + valueData + "\"";
        return CommandPrompt.elevate(addCommand);
    }

    /**
     * add a key value to the windows registry: HKCU\Software\HP\Omen Ally
     * @param valueName name of value to add to registry
     * @param valueType type of value to add
     * @param valueData data value to add
     * @return true if successful in adding to registry, otherwise false
     *
     */
    public static boolean addValueToOMENAlly(String valueName, String valueData, String valueType) {
        if(valueName == null || valueData == null || valueType == null){
            LogManager.logProduct.info("Registry key " + valueName + " will not be added because one of the parameters is null: valueName, valueType, valueData.");
            return false;
        }

        String addCommand = "reg add \"HKCU\\Software\\HP\\Omen Ally\" /v " + "\"" + valueName + "\"" + " /t " + "\"" + valueType + "\"" + " /d " + "\"" + valueData + "\"";
        return CommandPrompt.elevate(addCommand);
    }

    /**
     * delete a key from the registry
     * @param key key of value to delete
     * @return true if delete successful, false otherwise
     */
    public static boolean deleteKey(String key) {
        String deleteCommand = "reg delete \"" + key + "\"" + " /f";
        return CommandPrompt.elevate(deleteCommand);
    }

    /**
     * delete a value from the registry by key and valueName
     * @param key targeted key of deletion
     * @param valueName name of the value to delete
     * @return true if delete successful, false otherwise
     */
    public static boolean deleteValue(String key, String valueName) {
        String deleteCommand = "reg delete \"" + key + "\" /v " + "\"" + valueName + "\"" + " /f";
        return CommandPrompt.elevate(deleteCommand);
    }

    /**
     * update an existing value from the registry
     * @param key targeted key of updating
     * @param valueName name of value to update
     * @param valueType type of value to update
     * @param valueData data of value to update
     * @return true if successful, otherwise false
     */
    public static boolean updateValue(String key, String valueName, String valueType, String valueData) {
        deleteValue(key, valueName);
        return addValue(key, valueName, valueType, valueData);
    }

    /**
     * check if a certain key exists in the registry
     * @param key targeted key to check
     * @return true if key in registry, false otherwise
     */
    public static boolean existKey(String key) {
        String queryCommand = "reg query \"" + key + "\"";
        List<String> result = CommandPrompt.getCommandResult(queryCommand);
        if (result.size() == 0)
            return false;
        for (String s : result) {
            if (s.contains("ERROR: The system was unable to find the specified registry key or value."))
                return false;
        }
        return true;
    }

    /**
     * get the output result of a registry key query
     * @param key targeted key to get output from
     * @return list of string output
     */
    public static List<String> getKey(String key) {
        String queryCommand = "reg query \"" + key + "\"";
        return CommandPrompt.getCommandResult(queryCommand);
    }

    /**
     * get teh value data from a registry query key
     * @param key targeted key to get output from
     * @param valueName name of value to get output from
     * @return String line output of value data
     */
    public static String getValueData(String key, String valueName) {
        String queryCommand = "reg query \"" + key + "\"" + " /v " + "\"" + valueName + "\"";
        String value = null;
        try {
            value = CommandPrompt.getCommandResult(queryCommand).get(1);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            LogManager.logProduct.info( key +"cannot found!");
        }
        return value.substring(value.lastIndexOf(" ") + 1).trim();
    }

    public static boolean addValueToOMENAllySettings(String valueName, String valueData, String valueType) {
        if (valueName == null || valueData == null || valueType == null) {
            LogManager.logProduct.info("Registry key " + valueName + " will not be added because one of the parameters is null: valueName, valueType, valueData.");
            return false;
        }
        String addCommand = "reg add \"HKCU\\Software\\HP\\Omen Ally\\Settings\" /v " + "\"" + valueName + "\"" + " /t " + "\"" + valueType + "\"" + " /d " + "\"" + valueData + "\"";
        return CommandPrompt.elevate(addCommand);
    }

}
