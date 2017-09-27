/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.aem.tools.acvalidator.model.pagetestcases;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;

import biz.netcentric.aem.tools.acvalidator.api.TestResult;
import biz.netcentric.aem.tools.acvalidator.model.SimulatableTest;
import biz.netcentric.aem.tools.acvalidator.model.Testable;

/**
 * Checks if a page can(not) be replicated.
 * 
 * @author Roland Gruber
 */
public class PageReplicateTest extends PageTestCase implements SimulatableTest, Testable {
	
	private final Logger LOG = LoggerFactory.getLogger(PageReplicateTest.class);

	private String path;
	private boolean deactivate;
	private String errorMessage;

	/**
	 * Constructor
	 * 
	 * @param path page path
	 * @param isAllow test for allow
	 * @param simulate simulate write
	 * @param propertyNames propertyNames
	 * @param deactivate deactivate after replication
	 */
	public PageReplicateTest(String path, boolean isAllow, boolean simulate, boolean deactivate) {
		super(path, isAllow, "replicate");
		this.path = path;
		this.simulate = simulate;
		this.deactivate = deactivate;
		this.errorMessage = StringUtils.EMPTY;
	}

	@Override
	public boolean isSimulateSuccess() {
		return isSimulateSuccess;
	}

	@Override
	public TestResult isOk(ResourceResolver serviceResourcerResolver, ResourceResolver testUserResolver, Authorizable authorizable) throws RepositoryException, LoginException {
		if(this.simulate){
			try {
				isSimulateSuccess = replicatePagePage(serviceResourcerResolver, testUserResolver);
			} catch (WCMException e) {
				LOG.error("Exception in PageReplicateTest: ", e);
				throw new RepositoryException(e);
			}
		}
		
		boolean isOk = isOk(serviceResourcerResolver, authorizable);
		return new TestResult(authorizable.getID(), "Page Replicate Test", " path: " + this.path + ", isSimulate:" + this.simulate + " isSimulateSuccess: " + this.isSimulateSuccess(), isOk, this.errorMessage);
	}

	private boolean replicatePagePage(ResourceResolver serviceResourcerResolver, ResourceResolver testUserResolver) throws WCMException  {
		// check if given path from testcase is an existing page
		PageManager servicePageManager = getPageManager(serviceResourcerResolver);
		if(servicePageManager.getPage(this.path) == null){
			this.errorMessage = "testpage: " + path + " is not existing in repository!";
			return false;
		}
		Replicator replicator = getReplicator();
		Session testUserSession = testUserResolver.adaptTo(Session.class);
		if(testUserSession == null){
			throw new IllegalStateException("Could not adapt ResourceResolver to Session!");
		}
		ReplicationActionType replicationActionType = getReplicationActionType(this.deactivate);
		try {
			replicator.replicate(testUserSession, replicationActionType, this.path);
//			replicator.checkPermission(testUserSession, replicationActionType, this.path);
		} catch (ReplicationException e) {
			this.errorMessage = e.getLocalizedMessage();
			return false;
		}
		testUserResolver.revert();
		return true;
	}

	private ReplicationActionType getReplicationActionType(boolean deactivate) {
		if(deactivate){
			return ReplicationActionType.DEACTIVATE;
		}
		return ReplicationActionType.ACTIVATE;
	}

}
