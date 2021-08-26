/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 *  with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.ui.common;

import com.creditSuisse.utility.LogManager;
import com.creditSuisse.utility.PathEngine;
import com.creditSuisse.utility.constants.Constants;
import com.creditSuisse.utility.constants.FeatureToggleConstant;
import com.creditSuisse.utility.system.Registry;
import com.creditSuisse.utility.system.Time;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.awt.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.text.SimpleDateFormat;

import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Common Functions for UI activities in OCC
 * @author Nicole Zhang
 * @since 12/13/2019
 */
public class CommomFunc {

    /**
     * Validate element displayed.
     * @param element element to validate displaying
     */
    public static boolean validateElementDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException e) {
            LogManager.logProduct.fatal("Expected element didn't display!");
            return false;
        }
    }

    public static boolean validateElementListDisplayed(List<WebElement> elements) {
        for (WebElement element : elements) {
            if (!element.isDisplayed()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Verify two ArrayList<String> instances have the same content,
     * if checkSequence is set to true, their contents' sequence should also be the same
     * @param arrayListA the first instance
     * @param arrayListB the second instance
     * @param checkSequence indicator of whether their sequences are also required to be checked
     * @return true if same; false if not
     */
    public static boolean arrayListIdentical(List<String> arrayListA, List<String> arrayListB, boolean checkSequence) {
        if (arrayListA.size() != arrayListB.size()) {
            return false;
        }

        if (checkSequence) {
            for (int i = 0; i < arrayListA.size(); i++) {
                if (!arrayListA.get(i).equals(arrayListB.get(i))) {
                    return false;
                }
            }
        } else {
            for (String s : arrayListA) {
                boolean found = false;
                for (int j = 0; j < arrayListB.size(); j++) {
                    if (arrayListB.get(j).equals(s)) {
                        found = true;
                        arrayListB.remove(j);
                        break;
                    }
                }

                if (!found) {
                    return false;
                }
            }

        }

        return true;
    }

    /***
     * @description Click element
     * @param element
     * @throws Exception
     */
    public static void clickElement(WebElement element) throws Exception {
        try {
            element.click();
        } catch (Exception var2) {
            throw new Exception(element + " is not displayed");
        }
    }

    /***
     *
     * @param element target element
     * @param timeout timeout
     * @return return turn if element exist, otherwise return false
     */
    public static boolean isElementExist(WebElement element, long timeout) {
        int index = 0;
        try {
            while (element.isDisplayed()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                index++;
                if (index > timeout) {
                    return true;
                }
            }
            return false;

        } catch (NoSuchElementException e) {
            return false;
        }


    }

    /***
     * Filter the element which have the same automationId but not visible.
     * @return return the element which is visible.
     */
    public static List<WebElement> filterDisplayedElements(List<WebElement> list) {
        List<WebElement> visibleElement = new ArrayList<>();
        for (WebElement element : list) {
            if (element.isDisplayed()) {
                visibleElement.add(element);
            }
        }
        return visibleElement;
    }


    /***
     *
     * @param element the element you want to check
     * @param string the expected string for the element
     * @return flag
     */
    public static boolean verifyElementDisplayAndString(WebElement element, String string) {
        LogManager.logProduct.fatal("element-----"+element.getText());
        LogManager.logProduct.fatal("string-----"+string);

        boolean flag = true;
        try {
            if (!validateElementDisplayed(element)) {
                LogManager.logProduct.fatal("Expected element didn't show or not exist!");
                flag = false;
            } else {
                if (!element.getText().equals(string)) {
                    LogManager.logProduct.fatal("Expected element string is incorrect!");
                    LogManager.logProduct.fatal("\nExpected: " + string + "\nActual: " + element.getText());
                    flag = false;
                }
            }
        } catch (NoSuchElementException e) {
            flag = false;
        }
        return flag;
    }
    /***
     *
     * @param element the element you want to check
     * @param string the contained string you expected for the element
     * @return flag
     */
    public static boolean verifyElementDisplayAndConstainedString(WebElement element, String string) {
        boolean flag = true;
        try {
            if (!validateElementDisplayed(element)) {
                LogManager.logProduct.fatal("Expected element didn't show or not exist!");
                flag = false;
            } else {
                if (!element.getText().contains(string)) {
                    LogManager.logProduct.fatal("Expected element string is not contained!");
                    LogManager.logProduct.fatal("\nExpected: " + string + "\nActual: " + element.getText());
                    flag = false;
                }
            }
        } catch (NoSuchElementException e) {
            flag = false;
        }
        return flag;
    }


    /***
     * Wait element to show
     * @param wait WebDriverWait
     * @param element element in the page
     * @return true/false
     */
    public static boolean waitForElement(WebDriverWait wait, WebElement element) {
        try {
            wait.until(ExpectedConditions.visibilityOf(element));
        } catch (NoSuchElementException | TimeoutException exception) {
            LogManager.logProduct.warn("Failed to find expected element!!!");
            return false;
        }
        return element.isDisplayed();
    }

    /***
     * @Description offsetSystemTime(Calendar.DATE, - 1) change time to yestoday
     *              offsetSystemTime(Calendar.DATE, 2)   change time to the day after tomorrow
     */
    public static void offsetSystemTime(int calendarEnum, int offset) throws IOException, InterruptedException {
        Calendar calendar=Calendar.getInstance();
        calendar.add(calendarEnum,offset);
        long timeInMillis = calendar.getTimeInMillis();
        SimpleDateFormat dt = new SimpleDateFormat("MM-dd-yy hh:mm:ss");
        String timeToChange = dt.format(timeInMillis);
        LogManager.logProduct.info("Change system date to " + timeToChange );
        Process process = Runtime.getRuntime().exec("cmd /c date " + timeToChange);
        process.waitFor(Constants.Sec_10, TimeUnit.SECONDS);
        process.destroy();
    }

    /***
     * @Description offsetSystemTime(timestamp) change system date to input timeSheet
       @param timestamp  timestamp
     */
    public static void setSystemDateByTimeStamp(long timestamp) throws IOException, InterruptedException{
        Objects.requireNonNull(timestamp,"timeSheet is null");
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat dt = new SimpleDateFormat("MM-dd-yy hh:mm:ss");
        String timeToChange = dt.format(timestamp);
        LogManager.logProduct.info("Change system date to " + timeToChange );
        Process process = Runtime.getRuntime().exec("cmd /c date " + timeToChange);
        process.waitFor(Constants.Sec_10, TimeUnit.SECONDS);
        process.destroy();
    }
        
     /***   
     * @description Modify DeviceCheckOverride in User.config for Rewards Multi users check
     * @param valueOfSet value of DeviceCheckOverride, true or false
     */
    public static void setDeviceCheckOverrideInUserConfig(boolean valueOfSet)throws Exception{
        String filepath = PathEngine.getUIConfigFilePath();
        File file = new File(filepath);
        SAXReader reader = new SAXReader();
        Document xmlContent = reader.read(file);
        xmlContent.selectSingleNode("configuration/userSettings/HP.Omen.Background.RewardsBg.Properties.Settings/setting[@name='DeviceCheckOverride']/value").setText(String.valueOf(valueOfSet));
        Writer osWrite=new OutputStreamWriter(new FileOutputStream(filepath));
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        XMLWriter writer = new XMLWriter(osWrite,format);
        writer.write(xmlContent);
        writer.flush();
        writer.close();
        LogManager.logProduct.info("modified deviceCheckOverride value is "+valueOfSet);
        Time.suspend(Constants.Sec_3);
    }

    /***
     * @description
     * @param date value of DeviceCheckOverride, true or false
     */
    public static int getSecondTimestamp(Calendar date){
        if (null == date) {
            return 0;
        }
        String timestamp = String.valueOf(date.getTimeInMillis());
        int length = timestamp.length();
        if (length > 3) {
            return Integer.valueOf(timestamp.substring(0,length-3));
        } else {
            return 0;
        }
    }

    public static boolean stringHasChineseChar(String str){
        return !(str.length() == str.getBytes().length);
    }

    /***
     * @description Modify DeviceCheckOverride in Registry for Rewards Multi users check
     * @param valueOfSet value of DeviceCheckOverride, true or false
     */
    public static void setDeviceCheckOverrideInReg(boolean valueOfSet){
        deleteDeviceCheckOverrideInReg();
        if(valueOfSet == true){
            Registry.addValueToOMENAllySettings(Constants.Name_DeviceCheckOverride,"1",Constants.DEG_DWORD);
        }else{
            Registry.addValueToOMENAllySettings(Constants.Name_DeviceCheckOverride,"0",Constants.DEG_DWORD);
        }
    }

    public static void deleteDeviceCheckOverrideInReg(){
        Registry.deleteValue(Constants.Path_OmenAllySettings,Constants.Name_DeviceCheckOverride);
    }

    public static void updateWallpaper(String picPath){
        Registry.updateValue(Constants.Path_Desktop,Constants.Name_WallPaper,Constants.REG_SZ,picPath);
    }

    public static String getWallPaper(){
        return Registry.getValueData(Constants.Path_Desktop,Constants.Name_WallPaper);
    }

    public static boolean containsAnyIgnoreCase(String str,String subStr){
        String strUpper = str.toUpperCase();
        String subStrUpper = subStr.toUpperCase();
        return strUpper.contains(subStrUpper);

    }

    public static boolean isNeedHpbpLogin(){

        String input = null;
        try {
            input = Registry.getValueData(FeatureToggleConstant.OMEN_ALLY_PATH,FeatureToggleConstant.NAME_TESTRELEASEVERSON);
        } catch (IndexOutOfBoundsException e) {
            LogManager.logProduct.info("use HPID account");
            return false;
        }
        if(UIEnvVariables.inst().getEnvironmentName().toUpperCase().equals("Production")){
            LogManager.logProduct.info("use HPID account");
            return false;
        }
        BigInteger bigint = new BigInteger(input.substring(2,input.length()),16);
        int numb=bigint.intValue();
        int version2101 = Integer.valueOf(FeatureToggleConstant.VALUE_2101VERSION);
        if(numb >= version2101){
            LogManager.logProduct.info("use HPBP account");
        }else{
            LogManager.logProduct.info("use HPBP account");
        }
        return numb >= version2101;
    }

    public static boolean verifyElementsShow(Object page, List<Method> findElementMethods)  {
        boolean flag = true;
        for (Method method : findElementMethods ){
            if (!isElementShow(page,method)) {
                flag = false;
            }
        }
        return flag;
    }

    public static boolean isElementShow(Object page, Method method)  {
        boolean flag;
        LogManager.logProduct.info("check "+method.getName()+" is displayed");
        try {
            String type = method.getAnnotatedReturnType().getType().getTypeName();
            if ("java.util.List<org.openqa.selenium.WebElement".equals(type)) {
                List<WebElement> elements = (List<WebElement>)method.invoke(page);
                flag = elements.get(0).isDisplayed();
                LogManager.logProduct.info(method.getName()+" displayed status = "+flag);
                return flag;
            }
            WebElement element = (WebElement)method.invoke(page);
            flag =  element.isDisplayed();
            LogManager.logProduct.info(method.getName()+" displayed status = "+flag);
            return flag;
        } catch (IllegalAccessException| InvocationTargetException e1) {
            LogManager.logProduct.error("find "+method.getName()+" element failed!!");
            return false;
        }
    }

    public static String generateChars(int maxNum){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MMdd");
        String day = formatter.format(date);

        char[] str = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
                'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
                'X', 'Y', 'Z', 'q','w','e','r','t','y','u','i','o','p','a','s','d',
                'f','g','h','j','k','l','z','x','c','v','b','n','m','+','_'};
        char[] num = {'0','1', '2', '3', '4', '5', '6', '7', '8',
                '9'};
        StringBuffer gameHandle = new StringBuffer("");
        Random random = new Random();
        if(maxNum> day.length()){
            gameHandle.append(day);
        }
        int index;
        int indexNum;
        while(gameHandle.length() < maxNum){
            index = Math.abs(random.nextInt(str.length));
            indexNum = Math.abs(random.nextInt(num.length));
            if (index >= 0 && index < str.length && indexNum >= 0 && indexNum < num.length) {
                if(gameHandle.length()%2==0){
                    gameHandle.append(str[index]);
                }else{
                    gameHandle.append(num[indexNum]);
                }
            }
        }
        return gameHandle.toString();
    }
    public static boolean isElementShow(Object page, Method method,String text)  {
        LogManager.logProduct.info("check '"+text+"' is displayed");
        try {
            WebElement element = (WebElement)method.invoke(page,text);
            boolean flag =  element.isDisplayed();
            if (flag) {
                LogManager.logProduct.info(text+"text is displayed");
            }else{
                LogManager.logProduct.error("check text:"+text+" failed!!");
            }
            return flag;
        } catch (IllegalAccessException| InvocationTargetException e1) {
            LogManager.logProduct.error("check text:"+text+" failed!!");
            return false;
        }
    }

    public static boolean verifyTextsShow(Object page, Method method, List<String> textList)  {
        boolean flag = true;
        for (String text : textList){
            if (!isElementShow(page,method,text)) {
                flag = false;
            }
        }
        return flag;
    }

    public static ArrayList<String> readCsv(String filepath) {
        File csv = new File(filepath);
        csv.setReadable(true);
        csv.setWritable(true);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(csv));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = "";
        String everyLine = "";
        ArrayList<String> allString = new ArrayList<>();
        try {
            while ((line = br.readLine()) != null) {
                everyLine = line;
                System.out.println(everyLine);
                allString.add(everyLine);
            }
            //LogManager.logProduct.info("row count of csv：" + allString.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allString;

    }

    public static java.awt.Point getCurrentMousePosition(){
        return MouseInfo.getPointerInfo().getLocation();
    }
}
