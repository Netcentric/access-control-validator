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
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.WCMException;

import biz.netcentric.aem.tools.acvalidator.api.TestResult;
import biz.netcentric.aem.tools.acvalidator.model.SimulatableTest;
import biz.netcentric.aem.tools.acvalidator.model.Testable;

/**
 * Checks if a page can(not) be created.
 * 
 * @author jochen.koschorke
 */
public class PageCreateTest extends PageTestCase implements SimulatableTest, Testable {

	private final Logger LOG = LoggerFactory.getLogger(PageCreateTest.class);

	private String path;
	private String template;
	private String errorMessage;

	/**
	 * Constructor
	 * 
	 * @param path page path
	 * @param isAllow test for allow
	 * @param simulate simulate write
	 * @param template template path
	 * @throws LoginException 
	 */
	public PageCreateTest(String path, boolean isAllow, boolean simulate, String template) {
		super(path, isAllow, "create");
		this.path = path;
		this.simulate = simulate;
		this.template = template;
		this.errorMessage = StringUtils.EMPTY;
	}

	@Override
	public boolean isSimulateSuccess() {
		return this.isSimulateSuccess;
	}
	
	@Override
	public TestResult isOk(ResourceResolver serviceResourcerResolver, ResourceResolver testUserResolver, Authorizable authorizable) throws RepositoryException, LoginException {
		if(this.simulate){
			try {
				isSimulateSuccess = createPage(testUserResolver);
			} catch (WCMException e) {
				LOG.error("Exception in PageCreateTest: ", e);
				throw new RepositoryException(e);
			}
		}
		boolean isOk = isOk(serviceResourcerResolver, authorizable);
		return new TestResult(authorizable.getID(), "Page Create Test", " path: " + this.path + ", isSimulate:" + this.simulate + " isSimulateSuccess: " + this.isSimulateSuccess(), isOk, errorMessage);
	}

	private boolean createPage(ResourceResolver resolver) throws WCMException  {
		PageManager pm = null;
		Page testpage = null;
		try {
			pm = getPageManager(resolver);
			testpage = pm.create(this.path, "testpage", this.template, "testpage");
			Template template = testpage.getTemplate();
			if(template == null){
				throw new IllegalArgumentException("Could not get Template of temporary testpage: " + testpage.getPath());
			}
			if(!template.isAllowed(this.path)){
				errorMessage = "Page of this template is not allowed under this path!";
				return false;
			}
		} catch (WCMException e) {
			errorMessage = e.getLocalizedMessage();
			return false;
		}finally{
			if(pm != null && testpage != null){
				pm.delete(testpage, false);
			}
		}
		return true;
	}
}
