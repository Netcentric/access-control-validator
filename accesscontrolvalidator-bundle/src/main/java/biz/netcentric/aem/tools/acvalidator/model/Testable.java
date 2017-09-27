/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.aem.tools.acvalidator.model;



import javax.jcr.RepositoryException;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;

import biz.netcentric.aem.tools.acvalidator.api.TestResult;

public interface Testable {
	TestResult isOk(ResourceResolver serviceResourceResolver, ResourceResolver testUserResolver, Authorizable authorizable) throws RepositoryException, LoginException;
}
