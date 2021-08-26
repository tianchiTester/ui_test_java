/*
 *Copyright (c) 2020, HP Development Company, L.P. All rights reserved.
 *This software contains confidential and proprietary information of HP.
 *The user of this software agrees not to disclose, disseminate or copy
 *such Confidential Information and shall use the software only in accordance
 *with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.utility;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import com.creditSuisse.utility.constants.Constants;
import org.apache.log4j.Logger;

/**
 * MSTeam used for get the result in microsoft team
 * upload data to S3 and jenkins
 * @author Ramakant Sharma
 * @since 02/03/2020
 */
public class MSTeam extends AllureListener{
	private final static Logger LOGGER = Logger.getLogger(MSTeam.class);
	HttpURLConnection con;
	URL url;
	String S3Link;
	//public static String environmentName = System.getProperty("environment");
	public static String environmentName = Constants.ENVIRONMENT;
	public static long totalTimeInMinute;

	public final String getS3Link() {
		if (environmentName.equals("EU-PRODUCTION")) {
			S3Link = "https://s3.console.aws.amazon.com/s3/buckets/selenium-daas-automation/Selenium/EU-Production/" + System.getProperty("buildNumber");
		}else {
			S3Link = "https://s3.console.aws.amazon.com/s3/buckets/selenium-daas-automation/Selenium/US-Production/" + System.getProperty("buildNumber");
			//S3Link = "OMEN CC";
		}
		return S3Link;
	}

	public final void openMSteam() {
		getS3Link();
		try {
			String apiUrl = "https://outlook.office.com/webhook/5bbfc0f7-8961-4a21-b62b-ff71dbaecc33@ca7981a2-785a-463d-b82a-3db87dfc3ce6/IncomingWebhook/f024a6c6c15c4cf28b71c0aff4b7edc4/8da69cf4-40ac-4444-85a7-3823a8258fb9";
			url = new URL(apiUrl);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("accept-charset", "utf-8");
			con.setDoOutput(true);

		} catch (Exception e) {
			LOGGER.error("Exception occured in Test openMSteam : " + e.getMessage());
		}

	}

	public final void getResponse(String body) {
		try {
			byte[] outputInBytes = body.getBytes("UTF-8");
			OutputStream outputStream = con.getOutputStream();
			outputStream.write(outputInBytes);
			outputStream.close();
			int responseCode = con.getResponseCode();
			if (responseCode == 200) {
				LOGGER.info("Code Pass in getResponse method." + "Response for sending the message to MS team  : Success");
			} else {
				LOGGER.error("Code fail in getResponse method." + "Response for sending the message to MS team  : Error");
			}

		} catch (Exception e) {
			LOGGER.error("Exception occured in Test getResponse : " + e.getMessage());
		}

	}

	public void sendDataToMsTeamWhenSiteUpFailed(String color, String environment, String browser, String date) {
		try {
			this.openMSteam();
			String body = "{\r\n" + "\"themeColor\" : \"" + color + "\",\r\n" + "\"summary\" : \"Automation Testing Status\",\r\n" + "\"sections\" : [{\"activityTitle\" : \"Selenium Automation Update For Site Up Test\",\r\n" + "\"activitySubtitle\" : \"For HP DaaS Project\",\r\n" + "\"facts\": [{\"name\" : \"SELENIUM TESTS EXECUTION STATUS\",\r\n" + "\"value\" : \"Selenium Test cases skipped due to failure of site-up\"},\r\n" + "{\"name\" : \"SITE-UP STATUS\",\r\n" + "\"value\" : \"Failed\"},\r\n"
					+ "{\"name\" : \"ENVIRONMENT\",\r\n" + "\"value\" : \"" + environment + "\"},\r\n" + "{\"name\" : \"END TIME\",\r\n" + "\"value\" : \"" + date + "\"},\r\n" + "{\"name\" : \"BROWSER\",\r\n" + "\"value\" : \"" + browser + "\"}]\r\n" + "}],\r\n" + "\"potentialAction\" : [{" + "\"@type\" : \"OpenUri\",\r\n" + "\"name\" : \"VIEW BUILD\",\r\n" + "\"targets\" : [{" + "\"outputStream\" : \"default\",\r\n"
					+ "\"uri\" : \"https://jenkins.hppipeline.com/job/Automation/job/Selenium_DaaS_Automation/" + System.getProperty("buildNumber") + "/console\"\r\n" + "}]" + "}," + "{" + "\"@type\" : \"OpenUri\",\r\n" + "\"name\" : \"S3 LINK\",\r\n" + "\"targets\" : [{" + "\"outputStream\" : \"default\",\r\n" + "\"uri\" : \"" + S3Link + "/?region=us-west-2&tab=overview\"\r\n" + "}]" + "}]" + "}";
			this.getResponse(body);

		} catch (Exception e) {
			LOGGER.error("Exception occured in Test sendDataToMsTeamWhenSiteUpFailed : " + e.getMessage());
		}
	}
	public void sendStatusToMsTeam(String color, String environment, String browser, String status) {
		int passTest =AllureListener.passedtests.size();
		int failTest = AllureListener.failedtests.size();
		int skipTest = AllureListener.skippedtests.size();
		int totalTest = passTest+failTest+skipTest;
		final LocalDateTime executionStartTime =AllureListener.startTime;
		String startTime = dtf.format(executionStartTime);
		final LocalDateTime executionEndTime =AllureListener.endTime;
		String endTime = dtf.format(executionEndTime);
		totalTimeInMinute = timeMinutesDifference(startTime,endTime);
		try {
			this.openMSteam();
			String body = "{\r\n" + "\"themeColor\" : \"" + color + "\",\r\n" + "\"summary\" : \"Automation Testing Status\",\r\n" + "\"sections\" : [{\"activityTitle\" : \"Update From Selenium Tests Execution On " + environment + "\",\r\n" + "\"activitySubtitle\" : \"For Omen CC Project\",\r\n" + "\"facts\": [{\"name\" : \"EXECUTION STATUS\",\r\n" + "\"value\" : \"Finished\"},\r\n" + "{\"name\" : \"BROWSER\",\r\n" + "\"value\" : \"" + browser + "\"},\r\n" + "{\"name\" : \"ENVIRONMENT\",\r\n" + "\"value\" : \""
					+ environment + "\"},\r\n" + "{\"name\" : \"TOTAL TESTS\",\r\n" + "\"value\" : \"" + totalTest + "\"},\r\n" + "{\"name\" : \"TESTS PASSED\",\r\n" + "\"value\" : \"" + passTest + "\"},\r\n" + "{\"name\" : \"TESTS FAILED\",\r\n" + "\"value\" : \"" + failTest + "\"},\r\n" + "{\"name\" : \"TESTS SKIPPED\",\r\n" + "\"value\" : \"" + skipTest + "\"},\r\n" + "{\"name\" : \"DURATION IN MINUTES\",\r\n" + "\"value\" : \"" + totalTimeInMinute + "\"},\r\n"
					+ "{\"name\" : \"START TIME\",\r\n" + "\"value\" : \"" + startTime + "\"},\r\n" + "{\"name\" : \"END TIME\",\r\n" + "\"value\" : \"" + endTime + "\"},\r\n" + "{\"name\" : \"AUTOMATION STATUS\",\r\n" + "\"value\" : \"" + status + "\"}]\r\n" + "}],\r\n" + "\"potentialAction\" : [{" + "\"@type\" : \"OpenUri\",\r\n" + "\"name\" : \"VIEW BUILD\",\r\n" + "\"targets\" : [{" + "\"outputStream\" : \"default\",\r\n"
					+ "\"uri\" : \"https://jenkins.hppipeline.com/job/Automation/job/Selenium_DaaS_Automation/" + System.getProperty("buildNumber") + "/console\"\r\n" + "}]" + "}," + "{" + "\"@type\" : \"OpenUri\",\r\n" + "\"name\" : \"S3 LINK\",\r\n" + "\"targets\" : [{" + "\"outputStream\" : \"default\",\r\n" + "\"uri\" : \"" + S3Link + "/?region=us-west-2&tab=overview\"\r\n" + "}]" + "}]" + "}";
			this.getResponse(body);

		} catch (Exception e) {
			LOGGER.error("Exception occured in Test sendStatusToMsTeam : " + e.getMessage());
		}
	}

	public void sendDataToMsTeamWhenSiteUpFailedForWorkflow(String color, String environment, String browser, String date) {
		try {
			this.openMSteamWorkflow();
			String body = "{\r\n" + "\"themeColor\" : \"" + color + "\",\r\n" + "\"summary\" : \"Automation Testing Status\",\r\n" + "\"sections\" : [{\"activityTitle\" : \"Selenium Automation Update For Site Up Test\",\r\n" + "\"activitySubtitle\" : \"For HP Workflow Project\",\r\n" + "\"facts\": [{\"name\" : \"SELENIUM TESTS EXECUTION STATUS\",\r\n" + "\"value\" : \"Selenium Test cases skipped due to failure of site-up\"},\r\n" + "{\"name\" : \"SITE-UP STATUS\",\r\n" + "\"value\" : \"Failed\"},\r\n"
					+ "{\"name\" : \"ENVIRONMENT\",\r\n" + "\"value\" : \"" + environment + "\"},\r\n" + "{\"name\" : \"END TIME\",\r\n" + "\"value\" : \"" + date + "\"},\r\n" + "{\"name\" : \"BROWSER\",\r\n" + "\"value\" : \"" + browser + "\"}]\r\n" + "}],\r\n" + "\"potentialAction\" : [{" + "\"@type\" : \"OpenUri\",\r\n" + "\"name\" : \"VIEW BUILD\",\r\n" + "\"targets\" : [{" + "\"outputStream\" : \"default\",\r\n"
					+ "\"uri\" : \"https://jenkins.hppipeline.com/job/Automation/job/Selenium_DaaS_Automation/" + System.getProperty("buildNumber") + "/console\"\r\n" + "}]" + "}]" + "}";
			this.getResponse(body);

		} catch (Exception e) {
			LOGGER.error("Exception occured in Test sendDataToMsTeamWhenSiteUpFailedForWorkflow : " + e.getMessage());
		}
	}
	
	public final void openMSteamWorkflow() {
		try {
			String apiUrl = "https://outlook.office.com/webhook/5bbfc0f7-8961-4a21-b62b-ff71dbaecc33@ca7981a2-785a-463d-b82a-3db87dfc3ce6/IncomingWebhook/f024a6c6c15c4cf28b71c0aff4b7edc4/8da69cf4-40ac-4444-85a7-3823a8258fb9";
			url = new URL(apiUrl);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("accept-charset", "utf-8");
			con.setDoOutput(true);

		} catch (Exception e) {
			LOGGER.error("Exception occured in Test openMSteamWorkflow : " + e.getMessage());
		}

	}
	public long timeMinutesDifference(String startTime,String endTime){
		SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
		Date dateStart = null;
		Date dateEnd = null;
		try {
			dateStart = format.parse(String.valueOf(startTime));
			dateEnd = format.parse(String.valueOf(endTime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
// Get msec from each, and subtract.
		long diff = dateEnd.getTime() - dateStart.getTime();
		long diffMinutes = diff / (60 * 1000);
		totalTimeInMinute = diffMinutes;
		return totalTimeInMinute;
	}

}
