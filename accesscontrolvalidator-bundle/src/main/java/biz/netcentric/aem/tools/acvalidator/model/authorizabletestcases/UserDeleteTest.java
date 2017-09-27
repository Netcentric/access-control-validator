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
import org.apache.jackrabbit.api.security.user.AuthorizableExistsException;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.oak.spi.security.principal.PrincipalImpl;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biz.netcentric.aem.tools.acvalidator.api.TestResult;
import biz.netcentric.aem.tools.acvalidator.model.SimulatableTest;
import biz.netcentric.aem.tools.acvalidator.model.Testable;

/**
 * Checks if a user can(not) be deleted.
 * 
 * @author jochen koschorke
 */
public class UserDeleteTest extends AuthorizableTestCase implements SimulatableTest, Testable {

	private static final Logger LOG = LoggerFactory.getLogger(UserDeleteTest.class);

	private boolean simulate;

	/**
	 * Constructor
	 * 
	 * @param path intermediate path for creating temporary testuser used for this test
	 */
	public UserDeleteTest(String path, boolean isAllow) {
		super(path, isAllow);
		this.simulate = true;
	}

	@Override
	public boolean isSimulateSuccess() {
		return isSimulateSuccess;
	}

	@Override
	public TestResult isOk(ResourceResolver serviceResourcerResolver, ResourceResolver testUserResolver, Authorizable authorizable) throws RepositoryException, LoginException {
		if(this.simulate){
			isSimulateSuccess = deleteUser(serviceResourcerResolver, testUserResolver);
		}
		String errorString = StringUtils.EMPTY;
		errorString = additionalErrorMessage;

		// TODO: align which parameters do we need to add
		return new TestResult(authorizable.getID(), "User Delete Test", " path: " + this.path + ", isSimulate:" + this.simulate, isOk(testUserResolver, authorizable) , errorString);
	}
	
	private boolean deleteUser(ResourceResolver serviceResourcerResolver, ResourceResolver testUserResolver)  {
		UserManager adminUserManager = this.getUserManager(serviceResourcerResolver);
		User temporaryUser = null;
		try{
			temporaryUser = createTestUser(adminUserManager);
			// Since we have to make the user visible to the underlying session of testUserResolver we have to persist the 
			// created testuser at this point (will be purged again in finally block)
			serviceResourcerResolver.commit();

			testUserResolver.refresh();
			Resource testUserResource = testUserResolver.getResource(temporaryUser.getPath());
			if(testUserResource == null){
				this.additionalErrorMessage = "Could not get testuser using testUserResolver!";
				return false;
			}
			testUserResolver.delete(testUserResource);
		}catch(Exception e){
			this.additionalErrorMessage = "Exception: " + e.getClass() +" :" + e.getLocalizedMessage();
			LOG.error("Exception: {}", e);
			return false;
		}finally{
			String temporaryUserPath = StringUtils.EMPTY;
			try {
				if(temporaryUser != null){
					temporaryUserPath = temporaryUser.getPath();
					Resource temporaryUserResource = serviceResourcerResolver.getResource(temporaryUserPath);
					if(temporaryUserResource != null){
						serviceResourcerResolver.delete(temporaryUserResource);
						serviceResourcerResolver.commit();
					}else{
						LOG.warn("Did not find temporaryUserResource while trying to delete temporary user: {}", temporaryUserPath);
					}
				}
			} catch (RepositoryException | PersistenceException e) {
				LOG.error("Exception while trying to purge temporary testuser {}: {}", temporaryUserPath, e);
			}
		}
		return true;
	}

	private User createTestUser(UserManager adminUserManager)
			throws AuthorizableExistsException, RepositoryException {
		String userID = "testuser_" + System.currentTimeMillis();
		PrincipalImpl principalForNewUser = new PrincipalImpl(userID);
		User user = adminUserManager.createUser(userID, "password", principalForNewUser, this.path);
		return user;
	}

}
