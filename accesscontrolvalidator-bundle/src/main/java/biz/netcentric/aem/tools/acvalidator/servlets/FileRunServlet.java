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
import biz.netcentric.aem.tools.acvalidator.api.FileResult;
import biz.netcentric.aem.tools.acvalidator.api.TestResult;
import biz.netcentric.aem.tools.acvalidator.api.TestRun;

@SlingServlet(methods="GET", selectors="acvrun", resourceTypes="granite/ui/components/shell/page", extensions="json")
public class FileRunServlet extends AbstractFileServlet {

	private static final String SKIP = "1";

	private static final long serialVersionUID = 1L;

	private final Logger LOG = LoggerFactory.getLogger(FileRunServlet.class);
	private static final String PARAM_PATH = "path";
	private static final String PARAM_SKIP_SIMULATION = "skipsimulation";

	@Reference
	ACValidatorService acValidatorService;

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json; charset=utf-8");
		String path = request.getParameter(PARAM_PATH);
		if (StringUtils.isEmpty(path)) {
			printError("No path selected", response);
			return;
		}
		ResourceResolver resolver = request.getResourceResolver();
		if (resolver.getResource(path) == null) {
			printError("Path not found", response);
			return;
		}
		boolean skipSimulation = SKIP.equals(request.getParameter(PARAM_SKIP_SIMULATION));
		try {
			TestRun run = acValidatorService.runTests(path, skipSimulation);
			printResults(response, run);
		} catch (ACValidatorException e) {
			printError("Error reading test files: " + e.getMessage(), response);
		}
	}

	/**
	 * Prints the test results to the response object.
	 * 
	 * @param response respons
	 * @param run test run
	 */
	private void printResults(SlingHttpServletResponse response, TestRun run) {
		FileResult result = run.getFileResults().get(0);
		JSONObject json = new JSONObject();
		try {
			json.put("ok", result.isOk());
			json.put("okPercentage", result.getPercentageOk());
			json.put("file", result.getFileName());
			json.put("results", getJsonResults(result.getResults()));
			json.put("okResults", getNrOfOkResults(result.getResults()));
			json.write(response.getWriter());
		} catch (JSONException | IOException e) {
			LOG.error("Unable to write output", e);
		}
	}

	private JSONArray getJsonResults(List<TestResult> results) throws JSONException {
		long okCounter = 0;
		JSONArray json = new JSONArray();
		for (TestResult singleResult : results) {
			JSONObject singleResultJson = new JSONObject();
			singleResultJson.put("authorizable", singleResult.getAuthorizable());
			singleResultJson.put("params", singleResult.getParameters());
			singleResultJson.put("test", singleResult.getTestName());
			singleResultJson.put("error", singleResult.getErrorMessage());
			singleResultJson.put("ok", singleResult.isOk());
			if(singleResult.isOk()){
				okCounter++;
			}
			json.put(singleResultJson);
		}
		return json;
	}
	private long getNrOfOkResults(List<TestResult> results) throws JSONException {
		long okCounter = 0;
		for (TestResult singleResult : results) {
			
			if(singleResult.isOk()){
				okCounter++;
			}
		}
		return okCounter;
	}

}
