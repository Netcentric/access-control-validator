/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.aem.tools.acvalidator.healthcheck;

import java.util.Dictionary;
import java.util.List;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.hc.annotations.SlingHealthCheck;
import org.apache.sling.hc.api.HealthCheck;
import org.apache.sling.hc.api.Result;
import org.apache.sling.hc.util.FormattingResultLog;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biz.netcentric.aem.tools.acvalidator.api.ACValidatorException;
import biz.netcentric.aem.tools.acvalidator.api.ACValidatorService;
import biz.netcentric.aem.tools.acvalidator.api.TestRun;

/**
 * Health check for AC Validator tests.
 * 
 * @author Roland Gruber
 */
@SlingHealthCheck(name = "AC Validator Tests Health Check",
		mbeanName = "acValidatorTestsHCmBean",
		description = "This health check runs test files.",
		tags = "acvalidator",
		configurationFactory = true,
		configurationPolicy = ConfigurationPolicy.REQUIRE)
public class ACValidatorHealthCheck implements HealthCheck {
	
	private final Logger LOG = LoggerFactory.getLogger(ACValidatorHealthCheck.class);

	private static final String PROP_PATH = "path";

	@Property(name = PROP_PATH, label = "Path", description = "Path (file or folder) that is searched for test files.")
	private String path;
	
	@Reference
	private ACValidatorService	acvService;

	@Activate
	public void activate(final ComponentContext componentContext) {
		final Dictionary<?, ?> properties = componentContext.getProperties();
		path = (String) properties.get(PROP_PATH);
	}

	@Override
	public Result execute() {
		final FormattingResultLog resultLog = new FormattingResultLog();
		try {
			List<String> files = acvService.getFiles(path);
			if (files.isEmpty()) {
				resultLog.warn("No test files found");
			}
			else {
				for (String file : files) {
					TestRun run = acvService.runTests(file, false);
					if (run.isOk()) {
						resultLog.info(file + " ok");
					}
					else {
						resultLog.critical(file + " failed with errors");
					}
				}
			}
		} catch (ACValidatorException e) {
			resultLog.critical(e.getMessage());
			LOG.error("Exception: ", e);
		}
		return new Result(resultLog);
	}

}
