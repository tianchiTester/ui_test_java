/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.utility.file;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * XML file parser
 * @author Edward Guo
 * @since 12/13/2019
 */
public class XMLReader {

    private static DocumentBuilder documentBuilder;

    static {
        try {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * parses an xml file and returns it in a Document object
     * @param path to the xml file
     * @return Document of parsed values
     * @throws IOException if the path is unreadable
     * @throws SAXException if the XML fails to be read
     */
    public static Document parse(String path) throws IOException, SAXException {
         return documentBuilder.parse(new File(path));
    }
}
