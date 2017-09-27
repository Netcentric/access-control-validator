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
import org.apache.jackrabbit.api.security.user.Group;
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
 * Checks if a group can(not) be deleted.
 * 
 * @author jochen koschorke
 */
public class GroupDeleteTest extends AuthorizableTestCase implements SimulatableTest, Testable {

	private static final Logger LOG = LoggerFactory.getLogger(GroupDeleteTest.class);
	private boolean simulate;

	/**
	 * Constructor
	 * 
	 * @param path intermediate path for creating temporary testgroup used for this test
	 */
	public GroupDeleteTest(String path, boolean isAllow) {
		super(path, isAllow);
		this.simulate = true;
	}

	@Override
	public TestResult isOk(ResourceResolver serviceResourceResolver, ResourceResolver testUserResolver,
			Authorizable authorizable) throws RepositoryException, LoginException {
		if(this.simulate){
			isSimulateSuccess = deleteGroup(serviceResourceResolver, testUserResolver);
		}
		String errorString = StringUtils.EMPTY;
		errorString = additionalErrorMessage;

		// TODO: align which parameters do we need to add
		return new TestResult(authorizable.getID(), "Group Delete Test", " path: " + this.path + ", isSimulate:" + this.simulate, isOk(testUserResolver, authorizable) , errorString);
	}

	@Override
	public boolean isSimulateSuccess() {
		return isSimulateSuccess;
	}

	private boolean deleteGroup(ResourceResolver serviceResourcerResolver, ResourceResolver testUserResolver)  {
		UserManager adminUserManager = this.getUserManager(serviceResourcerResolver);
		Group temporaryGroup = null;
		try{
			temporaryGroup = createTestGroup(adminUserManager);
			// Since we have to make the group visible to the underlying session of testUserResolver we have to persist the 
			// created testgroup at this point (will be purged again in finally block)
			serviceResourcerResolver.commit();

			testUserResolver.refresh();
			Resource testGroupResource = testUserResolver.getResource(temporaryGroup.getPath());
			if(testGroupResource == null){
				this.additionalErrorMessage = "Could not get testgroup using testUserResolver!";
				return false;
			}
			testUserResolver.delete(testGroupResource);
		}catch(Exception e){
			this.additionalErrorMessage = "Exception: " + e.getClass() +" :" + e.getLocalizedMessage();
			LOG.error("Exception: {}", e);
			return false;
		}finally{
			String temporaryGroupPath = StringUtils.EMPTY;
			try {
				if(temporaryGroup != null){
					temporaryGroupPath = temporaryGroup.getPath();
					Resource temporaryGroupResource = serviceResourcerResolver.getResource(temporaryGroupPath);
					if(temporaryGroupResource != null){
						serviceResourcerResolver.delete(temporaryGroupResource);
						serviceResourcerResolver.commit();
					}else{
						LOG.warn("Did not find temporaryGroupResource while trying to delete temporary group: {}", temporaryGroupPath);
					}
				}
			} catch (RepositoryException | PersistenceException e) {
				LOG.error("Exception while trying to purge temporary testgroup {}: {}", temporaryGroupPath, e);
			}
		}
		return true;
	}

	private Group createTestGroup(UserManager adminUserManager)
			throws AuthorizableExistsException, RepositoryException {
		String userID = "testuser_" + System.currentTimeMillis();
		PrincipalImpl principalForNewUser = new PrincipalImpl(userID);
		Group group = adminUserManager.createGroup(principalForNewUser, this.path);
		return group;
	}


}
