/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.utility;

/**
 * Default Exception Handler for logging exceptions within framework
 * @author Nicole Zhang
 * @since 11/20/2019
 */
public class ExceptionHandling extends Exception {

    /**
     * constructs a exception error message to be logged
     * @param message text content for Logging the exception
     */
    public ExceptionHandling(String message) {
        super(message);
        LogManager.getLogger().error(message);
    }
}
