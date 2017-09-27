/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.aem.tools.acvalidator.model.pagetestcases;

import java.util.Set;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;

import biz.netcentric.aem.tools.acvalidator.api.TestResult;
import biz.netcentric.aem.tools.acvalidator.model.SimulatableTest;
import biz.netcentric.aem.tools.acvalidator.model.Testable;

/**
 * Checks if a page can(not) be modified.
 * 
 * @author Roland Gruber
 */
public class PageModifyTest extends PageTestCase implements SimulatableTest, Testable {
	
	private final Logger LOG = LoggerFactory.getLogger(PageModifyTest.class);

	private String path;
	private String errorMessage;
	private Set<String> propertyNames;

	/**
	 * Constructor
	 * 
	 * @param path page path
	 * @param isAllow test for allow
	 * @param simulate simulate write
	 * @param propertyNames propertyNames
	 */
	public PageModifyTest(String path, boolean isAllow, boolean simulate, Set<String> propertyNames) {
		super(path, isAllow, "modify");
		this.path = path;
		this.simulate = simulate;
		this.errorMessage = StringUtils.EMPTY;
		this.propertyNames = propertyNames;
	}

	@Override
	public boolean isSimulateSuccess() {
		return isSimulateSuccess;
	}

	@Override
	public TestResult isOk(ResourceResolver serviceResourcerResolver, ResourceResolver testUserResolver, Authorizable authorizable) throws RepositoryException, LoginException {
		if(simulate){
			try {
				isSimulateSuccess = modifyPage(serviceResourcerResolver, testUserResolver);
			} catch (WCMException e) {
				LOG.error("Exception in PageModifyTest: ", e);
				throw new RepositoryException(e);
			}
		}
		boolean isOk = isOk(serviceResourcerResolver, authorizable);
		return new TestResult(authorizable.getID(), "Page Modify Test", " path: " + this.path + ", isSimulate:" + simulate + " isSimulateSuccess: " + this.isSimulateSuccess(), isOk, errorMessage);
	}

	private boolean modifyPage(ResourceResolver serviceResourcerResolver, ResourceResolver testUserResolver) throws WCMException  {
		// check if given path from testcase is an existing page
		PageManager servicePageManager = getPageManager(serviceResourcerResolver);
		Page pageToModify = servicePageManager.getPage(this.path);
		if(pageToModify == null){
			errorMessage = "testpage: " + path + " is not existing in repository!";
			return false;
		} 

		PageManager testPagemanager = getPageManager(testUserResolver);
		try {
			Page testPage = testPagemanager.getPage(this.path);
			if(testPage == null){
				errorMessage = "no access to page";
				return false;
			}
			// autosave set to false - we don't want to really modify the testpage
			Resource contentResource = testPage.getContentResource();
			if(contentResource == null){
				errorMessage = "contentResource missing!";
				return false;
			}
			// TODO: clarify: should it be configurable which value (namespace etc.). Also should setting/modifying value on parent possible?
			ValueMap vm = contentResource.adaptTo(ModifiableValueMap.class);
			if(vm == null){
				return false;
			}
			for(String propertyName : propertyNames){
				vm.put(propertyName, "testvalue");
			}
			Node node = contentResource.adaptTo(Node.class);
			if(node == null){
				throw new IllegalStateException("node is null");
			}
			for(String propertyName : propertyNames){
				node.setProperty(propertyName, "test");
			}
			
		} catch (Exception e) {
			this.errorMessage = e.getLocalizedMessage();
			return false;
		}
		testUserResolver.revert();
		return true;
	}

}
