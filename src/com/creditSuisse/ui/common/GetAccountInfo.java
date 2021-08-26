/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 *  with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.ui.common;

import com.creditSuisse.utility.constants.Constants;
import com.creditSuisse.utility.JDBCExcel;
import java.sql.SQLException;

/**
 * @Author Nicole Zhang
 * @Date 2019/8/1
 * @Description
 */
public class GetAccountInfo {
    public static String getUserName(String identifier) throws SQLException {
        JDBCExcel jdbcExcel = new JDBCExcel();
        jdbcExcel.loadExcel(Constants.PATH_ACCOUNT_TABLE);
        return jdbcExcel.getValueByAnyColumn(UIEnvVariables.inst().getStack(), "id", identifier, "username");
    }

    public static String getPassword(String identifier) throws SQLException {
        JDBCExcel jdbcExcel = new JDBCExcel();
        jdbcExcel.loadExcel(Constants.PATH_ACCOUNT_TABLE);
        return jdbcExcel.getValueByAnyColumn(UIEnvVariables.inst().getStack(), "id", identifier, "password");
    }

    public static String getCoachingUserName(String identifier) throws SQLException {
        JDBCExcel jdbcExcel = new JDBCExcel();
        jdbcExcel.loadExcel(Constants.PATH_ACCOUNT_TABLE);
        return jdbcExcel.getValueByAnyColumn("gamecoaching", "id", identifier, "username");
    }

    public static String getCoachingPassword(String identifier) throws SQLException {
        JDBCExcel jdbcExcel = new JDBCExcel();
        jdbcExcel.loadExcel(Constants.PATH_ACCOUNT_TABLE);
        return jdbcExcel.getValueByAnyColumn("gamecoaching", "id", identifier, "password");
    }

    public static String getUserId(String identifier) throws SQLException {
        JDBCExcel jdbcExcel = new JDBCExcel();
        jdbcExcel.loadExcel(Constants.PATH_ACCOUNT_TABLE);
        return jdbcExcel.getValueByAnyColumn(UIEnvVariables.inst().getStack(), "id", identifier, "userid");
    }
    public static String getgameHandle(String identifier) throws SQLException {
        JDBCExcel jdbcExcel = new JDBCExcel();
        jdbcExcel.loadExcel(Constants.PATH_ACCOUNT_TABLE);
        return jdbcExcel.getValueByAnyColumn(UIEnvVariables.inst().getStack(), "id", identifier, "gameHandle");
    }
}
