/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.aem.tools.acvalidator.model.permissiontestcases;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlManager;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;

import com.day.cq.wcm.api.WCMException;

import biz.netcentric.aem.tools.acvalidator.api.TestResult;
import biz.netcentric.aem.tools.acvalidator.model.SimulatableTest;
import biz.netcentric.aem.tools.acvalidator.model.Testable;
import biz.netcentric.aem.tools.acvalidator.model.pagetestcases.PageTestCase;
import biz.netcentric.aem.tools.acvalidator.service.AccessControlUtils;

/**
 * Checks if ACLs of a path can(not) be read.
 * 
 * @author jochen koschorke
 */
public class AclReadTest extends PageTestCase implements SimulatableTest, Testable {

	private String path;
	private boolean isAllow;
	private String error;

	/**
	 * Constructor
	 * 
	 * @param path page path
	 * @param isAllow test for allow
	 * @param simulate simulate acl read
	 */
	public AclReadTest(String path, boolean isAllow, boolean simulate) {
		super(path, isAllow, "acl_read");
		this.path = path;
		this.isAllow = isAllow;
		this.simulate = simulate;
		this.error = StringUtils.EMPTY;
	}

	@Override
	public TestResult isOk(ResourceResolver serviceResourcerResolver, ResourceResolver testUserResolver, Authorizable authorizable) throws RepositoryException, LoginException {
		if(this.simulate){
			try {
				isSimulateSuccess = canReadAcl(serviceResourcerResolver, testUserResolver);
			} catch (WCMException e) {
				throw new RepositoryException(e);
			}
		}
		
		// TODO: align which parameters do we need to add
		boolean isOk = isOk(testUserResolver, authorizable);
		
		return new TestResult(authorizable.getID(), "ACL read test", " path: " + this.path + ", isSimulate:" + this.simulate + " isSimulateSuccess: " + this.isSimulateSuccess(), isOk , error);
	}

	private boolean canReadAcl(ResourceResolver serviceResourcerResolver, ResourceResolver testUserResolver) throws WCMException  {
		Session testUserSession = testUserResolver.adaptTo(Session.class);
		try{
			if(testUserSession == null){
				throw new IllegalStateException("Could not adapt testUserResolver to Session!");

			}
			JackrabbitAccessControlManager acMgr = (JackrabbitAccessControlManager) testUserSession.getAccessControlManager();
			AccessControlUtils.getAccessControlList(acMgr, this.path);
		}catch(Exception e){
			this.error = "Exception: " + e.getClass() +" :" + e.getLocalizedMessage();
			return false;
		}

		return isAllow;

	}


	@Override
	public boolean isSimulateSuccess() {
		return this.isSimulateSuccess;
	}
}
