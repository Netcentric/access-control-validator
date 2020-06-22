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
import javax.jcr.Session;
import javax.jcr.ValueFactory;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biz.netcentric.aem.tools.acvalidator.api.TestResult;
import biz.netcentric.aem.tools.acvalidator.model.SimulatableTest;
import biz.netcentric.aem.tools.acvalidator.model.Testable;

public class ModifyUserTest extends AuthorizableTestCase implements SimulatableTest, Testable{
	
	private static final Logger LOG = LoggerFactory.getLogger(ModifyUserTest.class);
	private boolean simulate;
	private String userID; // ID of group to be modified
	
	/**
	 * Constructor
	 * 
	 * @param userID id of user subject to modification
	 * @param isAllow if this action is expected to be allowed
	 */
	public ModifyUserTest(String userID, boolean isAllow) {
		super("",isAllow);
		this.userID = userID;
		this.simulate = true;
	}

	@Override
	public TestResult isOk(ResourceResolver serviceResourceResolver, ResourceResolver testUserResolver,
			Authorizable authorizable) throws RepositoryException, LoginException {
		
		if(this.simulate){
			isSimulateSuccess = modifyGroup(serviceResourceResolver, testUserResolver);
		}

		// TODO: align which parameters do we need to add
		return new TestResult(authorizable.getID(), "User Modify Test", " userId: " + this.userID + ", isSimulate:" + this.simulate + " isSimulateSuccess: " + this.isSimulateSuccess() + ", additionalErrorMessage: " +additionalErrorMessage, isOk(testUserResolver, authorizable) , additionalErrorMessage);
	}

	private boolean modifyGroup(ResourceResolver serviceResourceResolver, ResourceResolver testUserResolver) {
		UserManager userManager = this.getUserManager(testUserResolver);
		try{
			User groupToMofify = (User) userManager.getAuthorizable(this.userID);
			Session testUserSession = testUserResolver.adaptTo(Session.class);
			if(testUserSession == null){
				throw new IllegalStateException("Could not adapt testUserResolver to Session!");
			}
			ValueFactory vf = testUserSession.getValueFactory();
			groupToMofify.setProperty("profile/testproperty", vf.createValue("test"));
		}catch(Exception e){
			LOG.error("Exception: {}", e);
			this.additionalErrorMessage = "error: " + e.toString();
			return false;
		}
		return true;
	}

	@Override
	public boolean isSimulateSuccess() {
		return this.isSimulateSuccess;
	}

}
