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

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.oak.spi.security.principal.PrincipalImpl;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;

import biz.netcentric.aem.tools.acvalidator.api.TestResult;
import biz.netcentric.aem.tools.acvalidator.model.SimulatableTest;
import biz.netcentric.aem.tools.acvalidator.model.Testable;

/**
 * Checks if a group can(not) be created.
 * 
 * @author jochen koschorke
 */
public class GroupCreateTest extends AuthorizableTestCase implements SimulatableTest, Testable {

	
	/**
	 * Constructor
	 * 
	 * @param path intermediate path for creating temporary testgroup used for this test
	 * @param isAllow if this action is expected to be allowed
	 */
	public GroupCreateTest(String path, boolean isAllow) {
		super(path,isAllow);
	}

	@Override
	public TestResult isOk(ResourceResolver serviceResourcerResolver, ResourceResolver testUserResolver, Authorizable authorizable) throws RepositoryException, LoginException {
			isSimulateSuccess = createGroup(serviceResourcerResolver, testUserResolver);
		String errorString = additionalErrorMessage;
		return new TestResult(authorizable.getID(), "Group Create Test", " intermediate path: " + this.path + ", isSimulate:" + true + " isSimulateSuccess: " + this.isSimulateSuccess() , isOk(testUserResolver, authorizable) , errorString);
	}

	@Override
	public boolean isSimulateSuccess() {
		return isSimulateSuccess;
	}
	
	private boolean createGroup(ResourceResolver serviceResourcerResolver, ResourceResolver testUserResolver)  {
		UserManager userManager = this.getUserManager(testUserResolver);
		try{
			String groupID = "testgroup_" + System.currentTimeMillis();
			PrincipalImpl principalForNewGroup = new PrincipalImpl(groupID);
			userManager.createGroup(principalForNewGroup, this.path);
		}catch(Exception e){
			this.additionalErrorMessage = e.getLocalizedMessage();
			return false;
		}
		return true;

	}
}
