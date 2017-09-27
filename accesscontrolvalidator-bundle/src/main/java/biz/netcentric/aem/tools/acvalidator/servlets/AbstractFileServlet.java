/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.aem.tools.acvalidator.servlets;

import java.io.IOException;

import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractFileServlet extends SlingSafeMethodsServlet {

	private final Logger LOG = LoggerFactory.getLogger(AbstractFileServlet.class);
	private static final long serialVersionUID = 1L;

	/**
	 * Prints an error object to the response.
	 * 
	 * @param message error message
	 * @param response response
	 */
	protected void printError(String message, SlingHttpServletResponse response) {
		JSONObject json = new JSONObject();
		try {
			json.put("error", message);
			json.write(response.getWriter());
		} catch (IOException | JSONException e) {
			LOG.error("Unable to write output", e);
		}
	}

	/**
	 * Prints an warning object to the response.
	 * 
	 * @param message warning message
	 * @param response response
	 */
	protected void printWarning(String message, SlingHttpServletResponse response) {
		JSONObject json = new JSONObject();
		try {
			json.put("warning", message);
			json.write(response.getWriter());
		} catch (IOException | JSONException e) {
			LOG.error("Unable to write output", e);
		}
	}

}
