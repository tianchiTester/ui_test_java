/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.utility;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Analyzer for determining failures/passes of retries
 * @author Nicole Zhang
 * @since 11/20/2019
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;

    /**
     * retries test-case on any failures up to three times
     * @param result test-case result to log the retry attempts on it
     * @return true if <=3 tries, false otherwise
     */
    public boolean retry(ITestResult result) {
        int maxRetryCount = 3;
        if (retryCount < maxRetryCount) {
            retryCount++;
            LogManager.logFramework.info("RetryAnalyzer #" + retryCount + " for test: " + result.getMethod().getMethodName());
            return true;
        }
        return false;
    }
}
