/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 *
 * Copyright Gurock Software GmbH. See license.md for details.
 */
 
package com.creditSuisse.utility.testrail;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Base64;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * API class for calling testrail and making test result requests
 * @author TestRail - modified by Eric Tyree
 * @since 12/05/2019
 */
public class APIClient {

	private String mUser;
	private String mPassword;
	private String mUrl;

	/**
	 * Instance of the API client for calling testrail
	 * @param base_url URL to testrail instance
	 */
	public APIClient(String base_url) {
	    String url = base_url;
		if (!base_url.endsWith("/"))
			  url += "/";
		this.mUrl = url + "index.php?/api/v2/";
	}

	/**
	 * Get/Set User
	 * @return user used for authenticating the API requests.
	 */
	public String getUser() {
		return this.mUser;
	}

	public void setUser(String user) {
		this.mUser = user;
	}

	/**
	 * Get/Set Password
	 * @return the password used for authenticating the API requests.
	 */
	public String getPassword() {
		return this.mPassword;
	}

	public void setPassword(String password) {
		this.mPassword = password;
	}

	/**
	 * Issues a GET request (read) against the API and returns the result
	 * @param uri The API method to call including parameters (e.g. get_case/1)
	 * @param data The data to submit as part of the request (e.g. a map)
	 * @return parsed JSON response as standard object which can
	 * either be an instance of JSONObject or JSONArray
	 * @throws IOException if return of the POST request is null
	 * @throws APIException if POST request fails
	 */
	public Object sendGet(String uri, String data) throws IOException, APIException {
		return this.sendRequest("GET", uri, data);
	}

	public Object sendGet(String uri) throws IOException, APIException {
		return this.sendRequest("GET", uri, null);
	}

	/**
	 * Issues a POST request (write) against the API and returns the result
	 * @param uri The API method to call including parameter (e.g. add_case/1)
	 * @param data The data to submit as part of the request (e.g. a map)
	 * @return Returns the parsed JSON response as standard object which can
	 * either be an instance of JSONObject or JSONArray
	 * @throws IOException if return of the POST request is null
	 * @throws APIException if POST request fails
	 */
	public Object sendPost(String uri, Object data) throws IOException, APIException {
		return this.sendRequest("POST", uri, data);
	}

	private Object sendRequest(String method, String uri, Object data) throws IOException, APIException {
		URL url = new URL(this.mUrl + uri);
		// Create the connection object and set the required HTTP method
		// (GET/POST) and headers (content type and basic auth).
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		String auth = getAuthorization(this.mUser, this.mPassword);
		conn.addRequestProperty("Authorization", "Basic " + auth);
		if (method.equals("POST")) {
			conn.setRequestMethod("POST");
			// Add the POST arguments, if any. We just serialize the passed
			// data object (i.e. a dictionary) and then add it to the request body.
			if (data != null) {
				if (uri.startsWith("add_attachment")) { // add_attachment API requests
					String boundary = "TestRailAPIAttachmentBoundary"; //Can be any random string
					File uploadFile = new File((String)data);
					conn.setDoOutput(true);
					conn.addRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
					OutputStream ostreamBody = conn.getOutputStream();
					BufferedWriter bodyWriter = new BufferedWriter(new OutputStreamWriter(ostreamBody));
					bodyWriter.write("\n\n--" + boundary + "\r\n");
					bodyWriter.write("Content-Disposition: form-data; name=\"attachment\"; filename=\"" + uploadFile.getName() + "\"");
					bodyWriter.write("\r\n\r\n");
					bodyWriter.flush();
					//Read file into request
					InputStream istreamFile = new FileInputStream(uploadFile);
					int bytesRead;
					byte[] dataBuffer = new byte[1024];
					while ((bytesRead = istreamFile.read(dataBuffer)) != -1) {
						ostreamBody.write(dataBuffer, 0, bytesRead);
					}
					ostreamBody.flush();
					bodyWriter.write("\r\n--" + boundary + "--\r\n");
					bodyWriter.flush();
					istreamFile.close();
					ostreamBody.close();
					bodyWriter.close();
				} else { // Not an attachment
					conn.addRequestProperty("Content-Type", "application/json");
					byte[] block = JSONValue.toJSONString(data).
					getBytes("UTF-8");
					conn.setDoOutput(true);
					OutputStream ostream = conn.getOutputStream();
					ostream.write(block);
					ostream.close();
				}
			}
		}
		else	// GET request
			conn.addRequestProperty("Content-Type", "application/json");
		// Execute the actual web request (if it wasn't already initiated
		// by getOutputStream above) and record any occurred errors
		int status = conn.getResponseCode();
		InputStream istream;
		if (status != 200) {
			istream = conn.getErrorStream();
			if (istream == null)
				throw new APIException("TestRail API return HTTP " + status + " (No additional error message received)");
		} else
			istream = conn.getInputStream();
		// If 'get_attachment/' returned valid status code, save the file
		if (istream != null && uri.startsWith("get_attachment/")) {
			FileOutputStream outputStream = new FileOutputStream((String)data);
			int bytesRead = 0;
			byte[] buffer = new byte[1024];
			while ((bytesRead = istream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, bytesRead);
			}
			outputStream.close();
			istream.close();
			return data;
		}
		// Read the response body, if any, and deserialize it from JSON.
		String text = "";
		if (istream != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(istream, "UTF-8"));
			String line;
			while ((line = reader.readLine()) != null) {
				text += line;
				text += System.getProperty("line.separator");
			}
			reader.close();
		}
		Object result;
		if (!text.equals(""))
			result = JSONValue.parse(text);
		else
			result = new JSONObject();
		// Check for any occurred errors and add additional details to
		// the exception message, if any (e.g. the error message returned
		if (status != 200) {
			String error = "No additional error message received";
			if (result != null && result instanceof JSONObject) {
				JSONObject obj = (JSONObject) result;
				if (obj.containsKey("error"))
					error = '"' + (String) obj.get("error") + '"';
			}
			throw new APIException("TestRail API returned HTTP " + status + "(" + error + ")");
		}
		return result;
	}

	private static String getAuthorization(String user, String password) {
		try {
			return new String(Base64.getEncoder().encode((user + ":" + password).getBytes()));
		} catch (IllegalArgumentException e) {
			// Not thrown
		}
		return "";
	}
}
