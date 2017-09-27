/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.aem.tools.acvalidator.healthcheck;

import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.hc.annotations.SlingHealthCheck;
import org.apache.sling.hc.api.HealthCheck;
import org.apache.sling.hc.api.Result;
import org.apache.sling.hc.util.FormattingResultLog;

import biz.netcentric.aem.tools.acvalidator.serviceuser.ServiceResourceResolverService;

/**
 * Health check for service resource resolver.
 * 
 * @author Roland Gruber
 */
@SlingHealthCheck(name = "AC Validator Service User Health Check",
		mbeanName = "acValidatorServiceUserHCmBean",
		description = "This health check tests the service resource resolver.",
		tags = "acvalidator")
public class ServiceUsersHealthCheck implements HealthCheck {

	@Reference
	private ServiceResourceResolverService	serviceResourceResolverService;

	@Override
	public Result execute() {
		final FormattingResultLog resultLog = new FormattingResultLog();
		try {
			serviceResourceResolverService.getServiceResourceResolver();
			resultLog.info("Service resource resolver OK");
		} catch (final LoginException e) {
			resultLog.critical("Unable to get resource resolver {}", e.getMessage());
		}
		return new Result(resultLog);
	}

}
