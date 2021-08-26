/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.utility;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.*;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Listener for automated test cases that will generate screenshots and
 * videos on failed test-cases and capture test logs for Allure reports.
 * It also generates those reports after execution is complete.
 * @author Nicole Zhang
 * @since 11/26/2019
 */
public class AllureListener implements ITestListener,IHookable {
    public static java.util.List<ITestNGMethod> passedtests = new ArrayList<ITestNGMethod>();
    public static java.util.List<ITestNGMethod> failedtests = new ArrayList<ITestNGMethod>();
    public static java.util.List<ITestNGMethod> skippedtests = new ArrayList<ITestNGMethod>();
    //static long startTime;
    //static long endTime;
    static DateTimeFormatter dtf;
    static LocalDateTime startTime;
   // static DateTimeFormatter dtf_endTime;
    static LocalDateTime endTime;
    /**
     * executes during each test to capture logs and if failure occurs,
     * capture screenshots and video for that failure
     * @param callBack callback object for acquiring testResult info
     * @param testResult test-case result object for analysis
     */
    public void run(IHookCallBack callBack, ITestResult testResult) {
        callBack.runTestMethod(testResult);
        if (testResult.getThrowable() != null) {
            try {
//                File folder = new File(".\\recordings\\");
//                for (File name : folder.listFiles()) {
//                    if (name.getName().contains(testResult.getMethod().getMethodName())) {
//                        video(new File(name.getAbsolutePath()));
//                        break;
//                    }
//                }
                //Attach screenshot to allure when test failed.
                takeScreenShot(testResult.getMethod().getMethodName());
                logOutput();
            } catch (IOException | AWTException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * captures a video on a test-case failure and saves to path provided
     * @param video file name for video
     * @return byte stream of the video file
     */
    @Attachment(value = "video", type = "video/avi")
    private byte[] video(File video) {
        try {
            return Files.readAllBytes(Paths.get(video.getAbsolutePath()));
        } catch (IOException e) {
            return new byte[0];
        }
    }

    /**
     * uses a Robot class to screen capture what is occurring on a specific
     * failure and saves it using the methodName to output \logs\ path
     * @param methodName (test-case) when screenshot captured
     * @throws IOException if 'logs' directory/files within do not exist
     * @throws AWTException if a generic exception occurrs
     * @return byte stream of the screenshot png file
     */
    @Attachment(value = "Failure in method {0}", type = "image/png")
    private byte[] takeScreenShot(String methodName) throws IOException, AWTException {
        Robot robot = new Robot();
        Rectangle capture = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage image = robot.createScreenCapture(capture);
        File screenshot = new File("logs\\" + methodName + ".png");
        ImageIO.write(image, "png", screenshot);
        return image2byte(screenshot.getPath());
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

    /**
     * translates a picture capture into a byte data stream for Allure to read
     * @param path to the image file for transforming
     * @return byte stream of an image file
     */
    private byte[] image2byte(String path) {
        byte[] data = null;
        FileImageInputStream input;
        try {
            input = new FileImageInputStream(new File(path));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int numBytesRead ;
            while ((numBytesRead = input.read(buf)) != -1) {
                output.write(buf, 0, numBytesRead);
            }
            data = output.toByteArray();
            output.close();
            input.close();
        } catch (IOException ex1) {
            ex1.printStackTrace();
        }
        return data;
    }

    @Override
    public void onTestStart(ITestResult iTestResult) {

    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        passedtests.add(iTestResult.getMethod());
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        failedtests.add(iTestResult.getMethod());
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        skippedtests.add(iTestResult.getMethod());

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {

    }

    @Override
    public void onStart(ITestContext iTestContext) {
        dtf = DateTimeFormatter.ofPattern("yy/MM/dd HH:mm:ss");
        startTime = LocalDateTime.now();

    }

    @Override
    public void onFinish(ITestContext iTestContext) {
       dtf = DateTimeFormatter.ofPattern("yy/MM/dd HH:mm:ss");
        endTime = LocalDateTime.now();
    }

}
