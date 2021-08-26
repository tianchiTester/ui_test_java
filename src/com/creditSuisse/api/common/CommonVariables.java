/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.api.common;

/**
 * @Author Edward Guo
 * @Date 7/29/2019
 * @Description Used as convention to store and retrieve JSON property
 **/
public class CommonVariables {
    public static String MPVC = "Method passed verifications count";
    public static String MFVEC = "Method failed verifications count(expected)";
    public static String MFVNEC = "Method failed verifications count(not expected)";
    public static String MPV = "Method passed verifications";
    public static String MFVE = "Method failed verifications(expected)";
    public static String MFVNE = "Method failed verifications(not expected)";
    public static String CPVC = "Class passed verifications count";
    public static String CFVEC = "Class failed verifications count(expected)";
    public static String CFVNEC = "Class failed verifications count(not expected)";
    public static String CPV = "Class passed verifications";
    public static String CFVE = "Class failed verifications(expected)";
    public static String CFVNE = "Class failed verifications(not expected)";
}
