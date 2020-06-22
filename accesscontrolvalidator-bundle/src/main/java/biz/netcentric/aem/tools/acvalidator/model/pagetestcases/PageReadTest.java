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
 * Checks if a page path can(not) be read.
 * 
 * @author Roland Gruber
 */
public class PageReadTest extends PageTestCase implements SimulatableTest, Testable{
	
	private final Logger LOG = LoggerFactory.getLogger(PageReadTest.class);

	private String errorMessage;

	/**
	 * Constructor
	 * 
	 * @param path page path
	 * @param isAllow test for allow
	 * @param simulate read
	 */
	public PageReadTest(String path, boolean isAllow, boolean simulate) {
		super(path, isAllow, "read");
		this.simulate = simulate;
		this.errorMessage = StringUtils.EMPTY;
	}

	@Override
	public TestResult isOk(ResourceResolver serviceResourcerResolver, ResourceResolver testUserResolver, Authorizable authorizable) throws RepositoryException, LoginException {
		if(this.simulate){
			try {
				isSimulateSuccess = readPage(serviceResourcerResolver, testUserResolver);
			} catch (WCMException e) {
				LOG.error("Exception in PageReadTest: ", e);
				throw new RepositoryException(e);
			}
		}
		boolean isOk = isOk(serviceResourcerResolver, authorizable);
		return new TestResult(authorizable.getID(), "Page Read Test", " path: " + path + ", isSimulate:" + this.simulate + " isSimulateSuccess: " + this.isSimulateSuccess(), isOk , this.errorMessage);
	}

	@Override
	public boolean isSimulateSuccess() {
		return isSimulateSuccess;
	}

	private boolean readPage(ResourceResolver serviceResourcerResolver, ResourceResolver testUserResolver) throws WCMException  {
		// check if given path from testcase is an existing page
		PageManager servicePageManager = getPageManager(serviceResourcerResolver);
		if(servicePageManager.getPage(path) == null){
			this.errorMessage = "testpage: " + path + " is not existing in repository or is not a page!";
			return false;
		}

		PageManager testPagemanager = getPageManager(testUserResolver);
		Page page = testPagemanager.getPage(path);
		if(page == null){
			return false;
		}
		return true;
	}

}
