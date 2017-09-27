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
import org.apache.jackrabbit.oak.spi.security.principal.PrincipalImpl;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;

import biz.netcentric.aem.tools.acvalidator.api.TestResult;
import biz.netcentric.aem.tools.acvalidator.model.SimulatableTest;
import biz.netcentric.aem.tools.acvalidator.model.Testable;

/**
 * Checks if a user can(not) be created.
 * 
 * @author jochen koschorke
 */
public class UserCreateTest extends AuthorizableTestCase implements SimulatableTest, Testable {

	private String path;
	private boolean simulate;

	/**
	 * Constructor
	 * 
	 * @param path page intermediate path for creating temporary testuser used for this test
	 */
	public UserCreateTest(String path, boolean isAllow) {
		super(path,isAllow);		
		this.simulate = true;
	}

	@Override
	public boolean isSimulateSuccess() {
		return isSimulateSuccess;
	}

	@Override
	public TestResult isOk(ResourceResolver serviceResourcerResolver, ResourceResolver testUserResolver, Authorizable authorizable) throws RepositoryException, LoginException {
		if(this.simulate){
			isSimulateSuccess = createUser(serviceResourcerResolver, testUserResolver);
		}
		String errorString = StringUtils.EMPTY;
		errorString = additionalErrorMessage;

		// TODO: align which parameters do we need to add
		return new TestResult(authorizable.getID(), "User Create Test", " path: " + this.path + ", isSimulate:" + this.simulate + " isSimulateSuccess: " + this.isSimulateSuccess(), isOk(testUserResolver, authorizable) , errorString);
	}
	
	private boolean createUser(ResourceResolver serviceResourcerResolver, ResourceResolver testUserResolver)  {
		UserManager userManager = this.getUserManager(testUserResolver);
		try{
			String userID = "testuser_" + System.currentTimeMillis();
			PrincipalImpl principalForNewUser = new PrincipalImpl(userID);
			userManager.createUser(userID, "password", principalForNewUser, this.path);
		}catch(Exception e){
			this.additionalErrorMessage = e.getLocalizedMessage();
			return false;
		}
		return true;

	}

	
}
