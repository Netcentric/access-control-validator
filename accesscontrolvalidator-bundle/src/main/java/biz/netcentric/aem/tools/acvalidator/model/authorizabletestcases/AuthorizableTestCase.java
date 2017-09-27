/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.aem.tools.acvalidator.model.authorizabletestcases;

import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.ResourceResolver;

public abstract class AuthorizableTestCase {

	protected boolean isSimulateSuccess;
	protected String additionalErrorMessage;
	protected String path;
	protected boolean isAllow;

	public AuthorizableTestCase(String path, boolean isAllow) {
		this.additionalErrorMessage = StringUtils.EMPTY;
		this.path = path;
		this.isAllow = isAllow;
	}

	protected UserManager getUserManager(ResourceResolver resolver){
		UserManager userManager = resolver.adaptTo(UserManager.class);
		if(userManager == null){
			throw new IllegalStateException("Could not adapt ResourceResolver to UserManager!");
		}
		return userManager;
	}

	protected boolean isOk(ResourceResolver testUserResolver, Authorizable authorizable) throws RepositoryException {
		return isSimulateSuccess == this.isAllow;
	}

}
