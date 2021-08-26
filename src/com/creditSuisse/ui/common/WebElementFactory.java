/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.ui.common;

import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

/**
 * instantiates WebElements for different element platforms
 * @author Mitul Thesiya
 * @since 11/20/2019
 */
public class WebElementFactory {

	/**
	 * to do
	 * @param driver to do
	 * @param clazz to do
	 * @param <T> to do
	 * @return to do
	 */
	public static <T> T getInstance(WebDriver driver, Class<T> clazz) {
	    return PageFactory.initElements(driver, clazz);
	}

	/**
	 * to do
	 * @param driver to do
	 * @param clazz to do
	 * @param <T> to do
	 * @return to do
	 */
	public static <T> T getInstanceForMobile(WebDriver driver, Class<T> clazz) {
		T test = null;
		try {
			test = clazz.newInstance();
		} catch (Exception e) {
			// DO Nothing
		}
		PageFactory.initElements(new AppiumFieldDecorator(driver), test);
		return test;
	}
}
