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
import java.util.List;

import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biz.netcentric.aem.tools.acvalidator.api.ACValidatorException;
import biz.netcentric.aem.tools.acvalidator.api.ACValidatorService;

@SlingServlet(methods="GET", selectors="acvfiles", resourceTypes="granite/ui/components/shell/page", extensions="json")
public class FileListServlet extends AbstractFileServlet {

	private static final String PARAM_ROOT = "root";
	private static final long serialVersionUID = 1L;
	private final Logger LOG = LoggerFactory.getLogger(FileListServlet.class);
	
	@Reference
	ACValidatorService acValidatorService;

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json; charset=utf-8");
		String root = request.getParameter(PARAM_ROOT);
		if (StringUtils.isEmpty(root)) {
			printError("No root directory selected", response);
			return;
		}
		ResourceResolver resolver = request.getResourceResolver();
		if (resolver.getResource(root) == null) {
			printError("File or directory not found", response);
			return;
		}
		try {
			List<String> testFiles = acValidatorService.getFiles(root);
			printFilesToResponse(testFiles, response);
		} catch (ACValidatorException e) {
			printError("Error reading test files: " + e.getMessage(), response);
		}
	}

	/**
	 * Prints the found files to the response.
	 * 
	 * @param testFiles files found
	 * @param response response
	 */
	private void printFilesToResponse(List<String> testFiles, SlingHttpServletResponse response) {
		if (testFiles.isEmpty()) {
			printWarning("No test files found", response);
			return;
		}
		JSONArray files = new JSONArray();
		for (String file : testFiles) {
			files.put(file);
		}
		JSONObject json = new JSONObject();
		try {
			json.put("files", files);
			json.write(response.getWriter());
		} catch (JSONException | IOException e) {
			LOG.error("Unable to write output", e);
		}
	}

}
