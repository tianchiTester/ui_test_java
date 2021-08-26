/*
 *Copyright (c) 2020, HP Development Company, L.P. All rights reserved.
 *This software contains confidential and proprietary information of HP.
 *The user of this software agrees not to disclose, disseminate or copy
 *such Confidential Information and shall use the software only in accordance
 *with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.*;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import com.fasterxml.jackson.databind.ObjectReader;
import com.creditSuisse.utility.constants.Constants;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.util.Strings;

/**
 * SendMail is toMail send the report in email toMail respective users
 * Attach the data toMail email boday
 * @author Ramakant Sharma
 * @since 02/03/2020
 */
public class SendMail {

	public static String environment = null;
	public static String browser = null;
	public static String[] toMail = new String[5];
	private final static Logger LOGGER = Logger.getLogger(SendMail.class);
	public static Properties emailConfigProperties = new Properties();
	public static File emailConfigFile = new File(Constants.PATH_EMAIL_CONFIG + "EmailConfiguration.properties");
	public static Properties emailRawDataProperties = new Properties();
	public static File emailRawDataFile = new File(Constants.PATH_EMAIL_CONFIG + "EmailRawData.properties");

	/**
	 * This method is used toMail read properties file for email configuration.
	 *
	 * @throws Exception
	 */
	public final static void readEmailConfigFile() throws Exception {
		InputStream fileInput = null;
		try {
			if (emailConfigFile.exists()) {
				fileInput = new FileInputStream(emailConfigFile);
			} else {
				fileInput = ObjectReader.class.getClassLoader().getResourceAsStream("properties/" + "EmailConfiguration" + ".properties");
			}

			emailConfigProperties.load(fileInput);
		} catch (Exception e) {
			LOGGER.error("Error occured during reading email configuration file.");
			e.printStackTrace();
		}
	}

	/**
	 * This method is used toMail read properties file for email configuration.
	 *
	 * @throws Exception
	 */
	public final static void readEmailRawDataFile() throws Exception {
		InputStream fileInput = null;
		try {
			if (emailRawDataFile.exists()) {
				fileInput = new FileInputStream(emailRawDataFile);
			} else {
				fileInput = ObjectReader.class.getClassLoader().getResourceAsStream("test-Email/" + "EmailRawData" + ".properties");
			}

			emailRawDataProperties.load(fileInput);
		} catch (Exception e) {
			LOGGER.error("Error occured during reading email raw data file.");
			e.printStackTrace();
		}
	}

	/**
	 * This method is used toMail send email toMail recipients.
	 *
	 * @param reportFileName: This is the name of the report.
	 * @throws Exception
	 */
	public static void execute(String reportFileName, String suiteName) throws Exception {
		String groupNameText = emailRawDataProperties.getProperty("groupNames");
		int groupNameCount = Integer.parseInt(groupNameText);
		String mavenEmailRecipients = System.getProperty("email");
		String[] ccMail = {};
		String[] bccMail = {};
		if (!Strings.isNullOrEmpty(mavenEmailRecipients)) {
			if (mavenEmailRecipients.contains(",")) {
				toMail = mavenEmailRecipients.split(",");
			} else {
				toMail[0] = mavenEmailRecipients;
			}
		} else {
			if (emailConfigProperties.getProperty("mailTo").contains(",")) {
				toMail = emailConfigProperties.getProperty("mailTo").split(",");
			} else {
				toMail[0] = emailConfigProperties.getProperty("mailTo");
			}

		}

		if (groupNameCount > 0) {
			String mavenEmail = System.getProperty("email");
			HashMap<String, String> mailValues = null;
			if (Strings.isNullOrEmpty(mavenEmail)) {
				if (SendMail.emailConfigProperties.getProperty("sendEmail").equalsIgnoreCase("true") && !Strings.isNullOrEmpty(SendMail.emailConfigProperties.getProperty("mailTo"))) {
					mailValues = new HashMap<String, String>();
					mailValues.put("username", "omencctest@gmail.com");
					mailValues.put("password", "PNK9975@#1");
					mailValues.put("hostname", emailConfigProperties.getProperty("host"));
					mailValues.put("port", emailConfigProperties.getProperty("port"));
					mailValues.put("smtp", emailConfigProperties.getProperty("smtp"));
					mailValues.put("starttls", emailConfigProperties.getProperty("starttls"));
					mailValues.put("starttlsReq", emailConfigProperties.getProperty("starttlsReq"));
					mailValues.put("auth", emailConfigProperties.getProperty("auth"));
					mailValues.put("socketclass", emailConfigProperties.getProperty("socketFactoryClass"));
					SendMail.sendMail(mailValues, toMail, ccMail, bccMail, emailConfigProperties.getProperty("subject") + suiteName + " On " + emailRawDataProperties.getProperty("environment") + " Environment", reportFileName);
				} else {
					LOGGER.info("Send email flag is not set toMail true, please check email configuration properties file");
				}
			} else {
				LOGGER.info("Email is passed from maven parameter");
				SendMail.sendMail(mailValues, toMail, ccMail, bccMail, emailConfigProperties.getProperty("subject") + suiteName + " On " + emailRawDataProperties.getProperty("environment") + " Environment", reportFileName);
			}
					/*if (!(Strings.isNullOrEmpty(SetTestEnvironments.buildNumber))) {
						SetTestEnvironments.determinePassOrFail();	
					}	*/
		}
	}

	public  static void emailOMEN() throws Exception{
		init();
		readEmailConfigFile();
		readEmailRawDataFile();
		String reportName = emailRawDataProperties.getProperty("environment") + "-ZORA-OMEN-Result-Report-" + emailRawDataProperties.getProperty("timeStamp") + "" + ".html";
		execute(reportName, emailRawDataProperties.getProperty("suiteName"));
	}

	/**
	 * This method is toMail read logger file.
	 */
	public final static void  init() {
		File log4jPropertiesFile = new File(Constants.APPLE_PATH_LOG4J);
		if (log4jPropertiesFile.exists()) {
			PropertyConfigurator.configure(Constants.APPLE_PATH_LOG4J);
		} else {
			Properties props = new Properties();

			try {
				props.load(SendMail.class.getClassLoader().getResourceAsStream("properties/log4j.properties"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			PropertyConfigurator.configure(props);
		}
	}

	public static boolean sendMail(HashMap<String, String> mailValues, String[] toMail, String[] ccMail, String[] bccMail, String subject, String attachmentName) throws Exception {
		environment = emailRawDataProperties.getProperty("environment");
		double passpercent = 0;
		double failpercent = 0;
		double skippercent = 0;
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		int totalPassedCount =AllureListener.passedtests.size();
		int totalFailedCount = AllureListener.failedtests.size();
		int totalSkippedCount = AllureListener.skippedtests.size();
		int totalTestCount = totalPassedCount+totalFailedCount+totalSkippedCount;
		if (totalTestCount > 0) {
			passpercent = (double) (totalPassedCount * 100) / (totalPassedCount + totalFailedCount + totalSkippedCount);
			failpercent = (double) (totalFailedCount * 100) / (totalPassedCount + totalFailedCount + totalSkippedCount);
			skippercent = (double) (totalSkippedCount * 100) / (totalPassedCount + totalFailedCount + totalSkippedCount);
		}
		String color = "green";
		String emailContent = "<table width=100% ><tr bgcolor=" + color + "  align='center'><FONT COLOR=white FACE=Arial SIZE=2.5><h3>Selenium Test Automation Execution Report for OMEN" + "</h3></tr></table>";
		emailContent = emailContent + "<br><h3><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b> Test Results Summary </b></h3>";
		emailContent = emailContent + "<table cellspacing=1 cellpadding=1  border=1 width=300><tr><td width=150 align=left>" + "<FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>Total Tests executed</b></td><td width=100 align='center'><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>" + String.valueOf(totalTestCount) + "</b></td>" + "<td><span><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b></b></span></td></tr><tr><td width=150 align=left><FONT COLOR=#153E7E FACE=Arial SIZE=2.75>"
				+ "<b>Total Passed</b></td><td  width=100 align='center'><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>" + String.valueOf(totalPassedCount) + "</b></td>" + "<td width=100 align='center' bgcolor=green><span><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>" + decimalFormat.format(passpercent) + "%</b></span></td></tr>" + "<tr><td width=100 align=left><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>Total Skipped</b></td><td width=50 align='center'><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>"
				+ String.valueOf(totalSkippedCount) + "</b></td>" + "<td width=50 align='center' bgcolor=orange><span><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>" + decimalFormat.format(skippercent) + "%</b></span></td></tr>" + "<tr><td width=100 align=left><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>Total Failed</b></td><td width=50 align='center'><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>" + String.valueOf(totalFailedCount) + "</b></td>"
				+ "<td width=50 align='center' bgcolor=red><span><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>" + decimalFormat.format(failpercent) + "%</b></span></td></tr>" + "<tr><td width=100 align=left><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>Environment</b></td><td width=150 align='center'><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>" + environment + "</b></td>"
				+ "<tr><td width=100 align=left><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>Browser</b></td><td width=150 align='center'><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>Windows</b></td>" + "<tr><td width=100 align=left><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>ToTal Execution Time (in Minutes)</b></td><td width=150 align='center'><FONT COLOR=#153E7E FACE=Arial SIZE=2.75><b>" + MSTeam.totalTimeInMinute + "</b></td></table>";

		emailRawDataProperties.setProperty("emailContent",emailContent);

		// Object Instantiation of a properties file.
		Properties props = new Properties();

		props.put("mail.transport.protocol", mailValues.get("smtp"));
		props.put("mail.smtp.starttls.required", mailValues.get("starttlsReq"));
		props.put("mail.debug", "false");
		props.put("mail.smtp.localhost", "false");

		props.put("mail.smtp.user", mailValues.get("username"));

		props.put("mail.smtp.host", mailValues.get("hostname"));

		if (!"".equals(mailValues.get("port"))) {
			props.put("mail.smtp.port", mailValues.get("port"));
		}
		props.put("mail.smtp.port", mailValues.get("port"));
		props.put("mail.smtp.socketFactory.class",mailValues.get("socketclass"));
		if (!"".equals(mailValues.get("starttls"))) {
			props.put("mail.smtp.starttls.enable", mailValues.get("starttls"));
			props.put("mail.smtp.auth", mailValues.get("auth"));
		}
		try {
			boolean sessionDebug=false;
			emailContent = emailRawDataProperties.getProperty("emailContent").replace('\\', ' ');
			Session mailSession=Session.getDefaultInstance(props,null);
			mailSession.setDebug(sessionDebug);

			Message msg=new MimeMessage(mailSession);
			msg.setFrom(new InternetAddress(emailConfigProperties.getProperty("mailFrom")));
			String[] toEmail = emailConfigProperties.getProperty("mailTo").split(",");
			//for (int i = 0;i<=toEmail.length-1;i++){
			InternetAddress [] address=new InternetAddress[toEmail.length];
			int counter =0;
			for (String email:toEmail) {
				address[counter] = new InternetAddress(email.trim());
				counter++;
			}
			msg.setRecipients(Message.RecipientType.TO,address);
			//}
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			msg.setText(emailContent);
			msg.setContent(emailContent, "text/html");

			Transport transport=mailSession.getTransport("smtp");
			transport.connect(mailValues.get("hostname"),mailValues.get("username"),mailValues.get("password"));
			msg.saveChanges();
			transport.sendMessage(msg,msg.getAllRecipients());
			transport.close();
			return true;
		} catch (Exception mex) {
			mex.printStackTrace();
			return false;
		}
	}
}

