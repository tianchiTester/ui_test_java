/*
 *
 *  * Copyright (c) 2020, HP Development Company, L.P. All rights reserved.
 *  * This software contains confidential and proprietary information of HP.
 *  * The user of this software agrees not to disclose, disseminate or copy
 *  * such Confidential Information and shall use the software only in accordance
 *  *  with the terms of the license agreement the user entered into with HP.
 *
 */

package com.creditSuisse.utility.constants;

/**
 * Pre-defined registry key and value that need to add before run the test case.
 * After feature released, if one registry key not needed, then set the value of the key as null.
 * Please add comments for the usage of the key you added.
 * Created by  : Yolanda
 * Created Date: 2020/9/25
 *
 * @todo add more key and value for different feature.
 */
public class FeatureToggleConstant {
    /*MyGamesVersion is used for My Games 3.0 feature testing.*/
    public static String NAME_MYGAMESVERSION = "MyGamesVersion";
    public static String VALUE_MYGAMESVERSION = "2";

    /*Use TestReleaseVersion key to toggle on the feature on that release.*/
    public static String NAME_TESTRELEASEVERSON = "TestReleaseVersion";
    public static String VALUE_TESTRELEASEVERSION = "2011";
    public static String VALUE_2101VERSION ="2101";

    /*Add more keys if you have other toggle for different testing purpose*/
    public static String OMEN_ALLY_PATH = "HKEY_CURRENT_USER\\Software\\HP\\OMEN Ally";

}
