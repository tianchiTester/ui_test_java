package com.creditSuisse.utility;

import com.creditSuisse.ui.testcase.web.BaseTest;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import io.qameta.allure.Attachment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;

public class TestngListener extends TestListenerAdapter {
    public void onTestFailure(ITestResult tr) {
        super.onTestFailure(tr);
        BaseTest bt = (BaseTest) tr.getInstance();
        WebDriver driver = bt.driver;
        takePhoto(driver);
        logOutput();
    }
    @Attachment(value = "fail picture：",type = "image/png")
    public byte[]  takePhoto(WebDriver driver){
        byte[] screenshotAs = ((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES);
        return screenshotAs;
    }


    /**
     * captures the log for each test-case that will be displayed in Allure
     * @return string output of log content
     */
    @Attachment
    private String logOutput() {
        StringBuilder content = new StringBuilder();
        Enumeration enumeration = Logger.getRootLogger().getAllAppenders();
        while (enumeration.hasMoreElements()) {
            Appender app = (Appender) enumeration.nextElement();
            if (app instanceof FileAppender) {
                File log = new File(((FileAppender) app).getFile()).getAbsoluteFile();
                try {
                    content.append(new String(Files.readAllBytes(Paths.get(log.getPath()))));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return content.toString();
    }
}