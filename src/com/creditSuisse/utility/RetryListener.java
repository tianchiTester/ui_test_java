/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.utility;

import org.testng.IAnnotationTransformer;
import org.testng.IRetryAnalyzer;
import org.testng.annotations.ITestAnnotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Listener to retry test case that fail
 * @author Nicole Zhang
 * @since 11/20/2019
 */
public class RetryListener implements IAnnotationTransformer {

    /**
     * analyzer for determining failures/passes of retries
     * @param annotation any test annotations
     * @param testClass test-case class
     * @param testConstructor constructor for test
     * @param testMethod method of the test-case execution
     */
    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        IRetryAnalyzer retry = annotation.getRetryAnalyzer();
        if (retry == null)
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }
}
