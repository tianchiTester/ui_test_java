/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.utility.file;

import com.creditSuisse.utility.system.Time;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Comparator class for parsing JSON objects and comparing JSON arrays
 * and objects
 * @author Edward Guo
 * @since 11/26/2019
 */
public class JSON {

    /**
     * parses JSON object from a specified JSON file path
     * @param filePath the specified JSON file path
     * @return the parsed JSON object
     */
    public static JSONObject parseObject(String filePath) {
        Time.suspend(3);
        String jsonTxt = null;
        try {
            InputStream inputStrem = new FileInputStream(filePath);
            jsonTxt = IOUtils.toString(inputStrem, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONObject(jsonTxt);
    }

    /**
     * reads JSON object and re-write it (with indent) to a file
     * @param filePath the specified JSON file path
     * @param indent format specifier indent
     * @return the parsed and formatted JSON object
     */
    public static JSONObject formatFile(String filePath, int indent) {
        JSONObject source = parseObject(filePath);
        Writer.writeContentToFile(filePath, source.toString(indent), false);
        return source;
    }

    /**
     * checks whether two JSONObject instances have the same format of keys,
     * if identicalProperties provided, it requires the values of identity keys are equal
     * @param actualFormat actual JSONObject to compare its format
     * @param expectedFormat expected JSONObject to compare its format
     * @param identicalProperties the keys of properties requiring identical values
     * @return boolean - whether two JSON objects have same format
     */
    public static boolean matchObjectFormat(JSONObject actualFormat, JSONObject expectedFormat, String... identicalProperties) {
        boolean matched = true;
        ArrayList<String> actualList = new ArrayList<String>();
        ArrayList<String> expectedList = new ArrayList<String>();
        Iterator<String> expectedKeys = expectedFormat.keys();
        Iterator<String> actualKeys = actualFormat.keys();
        while (actualKeys.hasNext()) {
            actualList.add(actualKeys.next());
        }
        while (expectedKeys.hasNext()) {
            expectedList.add(expectedKeys.next());
        }
        if (actualList.size() != expectedList.size() && identicalProperties.length == 0)
            return false;
        else {
            for (String currentKey : actualList) {
                for (String expected : expectedList) {
                    if (currentKey.equals(expected)&&identicalProperties.length > 0) {
                        for (String identicalProperty : identicalProperties) {
                            if (identicalProperty.equals(currentKey)&&
                                    !(actualFormat.get(currentKey).toString().equals(expectedFormat.get(currentKey).toString())))
                                return false;
                        }
                        matched = true;
                        break;
                    }
                    matched = false;
                }
            }
        }
        return matched;
    }

    /**
     * checks whether two JSON objects or arrays are equal
     * @param actualBody the actual JSON object or array
     * @param expectedBody the expected JSON object or array
     * @return boolean - true if same, false if different
     */
    public static boolean matchIdentical(Object actualBody, Object expectedBody) {
        if (actualBody == null)
            return false;
        else if (!(actualBody instanceof JSONObject) && !(actualBody instanceof JSONArray))
            return false;
        else if (!(expectedBody instanceof JSONObject) && !(expectedBody instanceof JSONArray))
            return false;
        else if (actualBody instanceof JSONObject && expectedBody instanceof JSONArray)
            return ((JSONArray)expectedBody).length() == 1&&((JSONArray)expectedBody).getJSONObject(0).similar(actualBody);
        else if (actualBody instanceof JSONObject && expectedBody instanceof JSONObject)
            return ((JSONObject) actualBody).similar(expectedBody);
        else if (actualBody instanceof JSONArray && expectedBody instanceof JSONObject) {
            for (int i = 0; i < ((JSONArray) actualBody).length(); i++) {
                if (!((JSONArray) actualBody).getJSONObject(i).similar(expectedBody))
                    return false;
            }
            return true;
        }
        else if (actualBody instanceof JSONArray && expectedBody instanceof JSONArray)
            return ((JSONArray)actualBody).similar(expectedBody);
        return false;
    }

    /**
     * determines whether two JSONObjects or JSONArrays have matching formats
     * @param actualFormat actual JSON object or array to compare
     * @param expectedFormat expected JSON format example
     * @param identicalProperties optional parameter to match the property variables of each json
     * @return true if format is same, false otherwise
     */
    public static boolean matchFormat(Object actualFormat, Object expectedFormat, String... identicalProperties) {
        if (actualFormat == null)
            return false;
        else if (!(actualFormat instanceof JSONObject) && !(actualFormat instanceof JSONArray))
            return false;
        else if (!(expectedFormat instanceof JSONObject) && !(expectedFormat instanceof JSONArray))
            return false;
        else if (actualFormat instanceof JSONObject && expectedFormat instanceof JSONArray)
            return false;
        else if (actualFormat instanceof JSONObject && expectedFormat instanceof JSONObject)
            return matchObjectFormat((JSONObject) actualFormat, (JSONObject)expectedFormat, identicalProperties);
        else if (actualFormat instanceof JSONArray && expectedFormat instanceof JSONObject)
            return matchArrayFormatWithObject((JSONArray)actualFormat, (JSONObject)expectedFormat, identicalProperties);
        else if (actualFormat instanceof JSONArray && expectedFormat instanceof JSONArray)
            return matchArrayFormat((JSONArray)actualFormat, (JSONArray)expectedFormat);
        return false;
    }

    /**
     * compares the format of two json arrays and optionally the property vars
     * @param actualFormat array targeted for comparison
     * @param expectedFormat expected array format
     * @param identicalProperties optional check to see if variables of arrays match
     * @return true if array format matches, false otherwise
     */
    public static boolean matchArrayFormat(JSONArray actualFormat, JSONArray expectedFormat, String... identicalProperties) {
        if (actualFormat.length() != expectedFormat.length())
            return false;
        for (int i = 0; i < actualFormat.length(); i++) {
            if (!matchObjectFormat(actualFormat.getJSONObject(i), expectedFormat.getJSONObject(i), identicalProperties))
                return false;
        }
        return true;
    }

    /**
     * compares a json array and json object formats and optionally property vars
     * @param actualFormat targeted array for comparison
     * @param expectedFormat expected json object format
     * @param identicalProperties optional check for identical variables within format
     * @return true if equal formats, false otherwise
     */
    public static boolean matchArrayFormatWithObject(JSONArray actualFormat, JSONObject expectedFormat, String... identicalProperties) {
        for (int i = 0; i < actualFormat.length(); i++) {
            if (!matchObjectFormat(actualFormat.getJSONObject(i), expectedFormat, identicalProperties))
                return false;
        }
        return true;
    }

    /**
     * converts a provided instance to a JSON object
     * @param <T> T obj
     * @param instance object instance targeted
     * @param lowerFieldsName convert object variables to lowercase
     * @param all add all fields to new JSON instance
     * @return converted JSON obj instance
     */
    public static <T> JSONObject convertInstanceToObject(T instance, boolean lowerFieldsName, boolean all) {
        JSONObject result = new JSONObject();
        Field[] allFields = instance.getClass().getFields();
        try {
            for (Field field : allFields) {
                if (all&&lowerFieldsName)
                    result.put(field.getName().toLowerCase(), field.get(instance) == null ? "" : field.get(instance).toString());
                else if (all)
                    result.put(field.getName(), field.get(instance) == null ? "" : field.get(instance).toString());
                else if (!Modifier.isPrivate(field.getModifiers()))
                    result.put(field.getName(), field.get(instance) == null ? "" : field.get(instance).toString());
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * converts a list to JSON array
     * @param <T> list object
     * @param instances instance of the object
     * @param lowerFieldsName converts all list items to lowercase
     * @param all adds all fields to array
     * @return returns the JSON array
     */
    public static <T> JSONArray convertListToArray(List<T> instances, boolean lowerFieldsName, boolean all) {
        JSONArray result = new JSONArray();
        for (T instance : instances) {
            result.put(convertInstanceToObject(instance, lowerFieldsName, all));
        }
        return result;
    }

    /**
     * combines two JSON arrays into one
     * @param source first JSON array
     * @param dest second JSON array
     * @return combined array of source and dest
     */
    public static JSONArray combineJSONArray(JSONArray source, JSONArray dest) {
        JSONArray comb = new JSONArray();
        for (Object obj : source) {
            comb.put( obj);
        }
        for (Object obj : dest) {
            comb.put( obj);
        }
        return comb;
    }

    /**
     * finds the difference in JSON objects provided and returns them
     * @param actual first targeted JSON obj
     * @param expected second targeted JSON obj
     * @return different JSON obj of all differences in the JSON objects
     */
    public static JSONObject findDifference(JSONObject actual, JSONObject expected) {
        Iterator expectedIterator = expected.keys();
        JSONObject difference = new JSONObject();
        while (expectedIterator.hasNext()) {
            String expectedKey = (String) expectedIterator.next();
            if (actual.has(expectedKey)&&
                    actual.get(expectedKey).getClass().getName().equals(expected.get(expectedKey).getClass().getName())&&
                    actual.get(expectedKey).equals(expected.get(expectedKey))) {
                expectedIterator.remove();
                actual.remove(expectedKey);
            }
        }
        difference.put("actual", actual);
        difference.put("expected", expected);
        return difference;
    }
}
