/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.utility.file;

/**
 * Filtering class for determining names of different files
 * @author Edward Guo
 * @since 11/20/2019
 */
public class FileFilterByName {

    public String name;
    public boolean include;

    /**
     * saves an object containing a file name and boolean inclusion tag
     * @param name name of file
     * @param include boolean inclusion tag
     */
    public FileFilterByName(String name, boolean include) {
        this.name = name;
        this.include = include;
    }
}
