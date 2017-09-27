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

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;

import biz.netcentric.aem.tools.acvalidator.api.TestResult;
import biz.netcentric.aem.tools.acvalidator.model.SimulatableTest;
import biz.netcentric.aem.tools.acvalidator.model.Testable;

/**
 * Checks if a page can(not) be deleted.
 * 
 * @author Roland Gruber
 */
public class PageDeleteTest extends PageTestCase implements SimulatableTest, Testable {
	
	private final Logger LOG = LoggerFactory.getLogger(PageDeleteTest.class);

	private String path;
	private Authorizable authorizable;
	private boolean isTestPageExisting;
	private String errorMessage;
 
	/**
	 * Constructor
	 * 
	 * @param path page path
	 * @param isAllow test for allow
	 * @param simulate simulate write
	 * @param propertyNames propertyNames
	 */
	public PageDeleteTest(String path, boolean isAllow, boolean simulate) {
		super(path, isAllow, "delete");
		this.path = path;
		this.simulate = simulate;
		this.errorMessage = StringUtils.EMPTY;
	}

	@Override
	public TestResult isOk(ResourceResolver serviceResourcerResolver, ResourceResolver testUserResolver, Authorizable authorizable) throws RepositoryException, LoginException {
		if(this.simulate){
			try {
				isSimulateSuccess = deletePage(serviceResourcerResolver, testUserResolver);
			} catch (WCMException e) {
				LOG.error("Exception in PageDeleteTest: ", e);
				throw new RepositoryException(e);
			}
		}
		if(!this.isTestPageExisting){
			this.errorMessage = "testpage: " + path + " is not existing in repository!";
		}
		boolean isOk = isOk(serviceResourcerResolver, authorizable);
		return new TestResult(authorizable.getID(), "Page Delete Test", " path: " + this.path + ", isSimulate:" + this.simulate + " isSimulateSuccess: " + this.isSimulateSuccess(), isOk, this.errorMessage);
	}

	@Override
	public boolean isSimulateSuccess() {
		return this.isSimulateSuccess;
	}
	
	private boolean deletePage(ResourceResolver serviceResourcerResolver, ResourceResolver testUserResolver) throws WCMException  {
		// check if given path from testcase is an existing page
		PageManager servicePageManager = getPageManager(serviceResourcerResolver);
		Page pageToDelete = servicePageManager.getPage(this.path);
		if(pageToDelete == null){
			this.errorMessage =  "testpage: " + this.path + " is not existing in repository!";
			return false;
		} 
		
		PageManager testPagemanager = getPageManager(testUserResolver);
		try {
			// TODO: clarify if param 'shallow' should be influenceable
			// autosave set to false - we don't want to really delete the testpage
			testPagemanager.delete(pageToDelete, false, false);
		} catch (Exception e) {
			this.errorMessage = e.getLocalizedMessage();
			return false;
		}
		testUserResolver.revert();
		return true;
	}
	
}
