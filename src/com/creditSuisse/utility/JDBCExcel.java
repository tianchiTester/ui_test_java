/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.utility;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Java Database connectivity for Excel sheets within Framework
 * @author Edward Guo
 * @since 12/03/2019
 */
public class JDBCExcel {
    // Global variables used for SqlSheet API
    private Connection connection = null;
    private ResultSet resultSet = null;
    private Statement statement = null;

    // Global variables for POI
    private Workbook workBook = null;
    private FileInputStream fileInputStream = null;
    private String filePath = null;

    /**
     * load excel file from path. Based on extensions .xls/.xlsx
     * @param path of File including file name
     */
    public void loadExcel(String path) {
        try {
            // For POI---------------------
            // Read file stream from taken parameter File
            filePath = path;
            fileInputStream = new FileInputStream(new File(filePath));
            // check extension of file to determine workbook type
            String fileExt = path.substring(path.indexOf(".")).toLowerCase();
            // create workbook class based on file extension
            if (fileExt.equals(".xls"))
                workBook = new HSSFWorkbook(fileInputStream);
            else if (fileExt.equals(".xlsx"))
                workBook = new XSSFWorkbook(fileInputStream);
            Class.forName("com.googlecode.sqlsheet.Driver");
            this.connection = DriverManager.getConnection("jdbc:xls:file:" + path);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /** TODO - update the comments on this function
     * getRowId method to get particular row identified by ID value in ID column
     * in Last-to-First manner. This will be handled by SqlSheet API.
     * @param sheet
     * @param refValue
     * @param refColumn
     * @return Map of Strings of??
     * @throws SQLException
     */
    public Map<String, String> getRowByID(String sheet, String refColumn, String refValue) throws SQLException {
        Map<String, String> map = new HashMap<>();
        // TODO.. how to execute Where in SQL Query in JAVA 8
        String strQuery = "SELECT * FROM " + this.getSheetFormat(sheet);
        statement = connection.createStatement();
        resultSet = statement.executeQuery(strQuery);
        ResultSetMetaData rmd = resultSet.getMetaData();
        while (resultSet.next()) {
            // drawback to read cell again..so this is used.
            String idCell = resultSet.getString(refColumn);
            // Temporary solution until Where clause working in SQL Query
            if (refValue.equalsIgnoreCase(idCell)) {
                int columntCount = rmd.getColumnCount();
                for (int i = 1; i <= columntCount; i++) {
                    map.put(rmd.getColumnName(i), resultSet.getString(i));
                }
                break;
            }
        }
        resultSet.close();
        connection.close();
        return map;
    }

    /**
     * writingExcel method will be used with inline calling from method where
     * needed to update excel sheet after updating by POI. By Mitul
     * @throws SQLException to do
     */
    public void writingExcel() throws SQLException {
        try {
            // closing connection and file input Stream for already opened excel file
            this.connectionClose();
            // creating file output Stream and writing
            FileOutputStream fileOutput;
            fileOutput = new FileOutputStream(new File(filePath));
            workBook.write(fileOutput);
            // closing file output stream
            fileOutput.close();
            // reloading excel file using file input Stream
            this.loadExcel(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes a connection of a Connection Object along with the input stream
     * @throws SQLException
     * @throws IOException
     */
    public void connectionClose() throws SQLException, IOException {
        if (connection != null) {
            connection.close();
            fileInputStream.close();
        }
    }

    private String getSheetFormat(String sheetName) {
        if (System.getProperty("java.version").contains("1.8"))
            return sheetName;
        else
            return "[" + sheetName + "$]";
    }

    /**
     *
     * @param sheet
     * @param refColumn
     * @param refKey
     * @param targetCol
     * @return
     * @throws SQLException
     */
    public String getValueByAnyColumn(String sheet, String refColumn, String refKey, String targetCol) throws SQLException {
        String strValue = "";
        String strQuery = "SELECT * FROM " + this.getSheetFormat(sheet);
        statement = connection.createStatement();
        resultSet = statement.executeQuery(strQuery);
        while (resultSet.next()) {
            if (refKey.equalsIgnoreCase(resultSet.getString(refColumn))) {
                strValue = resultSet.getString(targetCol);
                strValue = strValue.equalsIgnoreCase("$Null") ? "" : strValue;
                break;
            }
        }
        resultSet.close();
        return strValue;
    }

    public List<String> getSelectedToRunList(String sheet, String refColumn, String refValue, String targetCol) throws SQLException {
        List<String> listReturn = new ArrayList<String>();

        String strQuery = "SELECT * FROM " + this.getSheetFormat(sheet);
        /* + " WHERE CreateAutomationPreData='Yes' ORDER BY Ranking"; */

        statement = connection.createStatement();
        resultSet = statement.executeQuery(strQuery);
        while (resultSet.next()) {
            // Temporary solution until Where clause working in SQL Query
            if (refValue.equalsIgnoreCase(resultSet
                    .getString(refColumn)))
                listReturn.add(resultSet.getString(targetCol));
        }
        resultSet.close();
        return listReturn;
    }
}