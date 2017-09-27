/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.aem.tools.acvalidator.model.pagetestcases;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.RepositoryException;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.PageManager;

import biz.netcentric.aem.tools.acvalidator.model.CqActionTester;
import biz.netcentric.aem.tools.acvalidator.model.Testable;

public abstract class PageTestCase implements Testable {

	private Set<String> cqActions;
	protected String path;
	protected boolean isAllow;
	protected boolean simulate;
	protected boolean isSimulateSuccess;


	private static final Logger LOG = LoggerFactory.getLogger(PageTestCase.class);

	public PageTestCase(String path, boolean isAllow, String cqAction) {
		this.path = path;
		this.cqActions = new HashSet<String>(Arrays.asList(cqAction));
		this.isAllow = isAllow;
	}
    
	protected boolean isCQActionsOk(ResourceResolver resolver, Authorizable authorizable) throws RepositoryException {
		// Test against cq actions
		CqActionTester cqActionsTester = new CqActionTester(resolver);
		return cqActionsTester.check(authorizable, path, cqActions);
	}

	public Set<String> getActions() {
		return this.cqActions;
	}
	
	protected PageManager getPageManager(ResourceResolver resolver){
		PageManager pm = resolver.adaptTo(PageManager.class);
		if(pm == null){
			throw new IllegalStateException("Could not adapt ResourceResolver to PageManager!");
		}
		return pm;
	}
	
	protected Replicator getReplicator(){
		BundleContext bundleContext = FrameworkUtil.getBundle(PageTestCase.class).getBundleContext();
		ServiceReference factoryRef = bundleContext.getServiceReference(Replicator.class.getName());
		Replicator replicator = (Replicator) bundleContext.getService(factoryRef);
		return replicator;
	}
	
	protected boolean isOk(ResourceResolver testUserResolver, Authorizable authorizable) throws RepositoryException {
		boolean isOk = false;
		if(this.simulate){
			isOk = !(isSimulateSuccess ^ this.isAllow);
		}else{
			isOk = !(isCQActionsOk(testUserResolver, authorizable) ^ this.isAllow);
		}
		return isOk;
	}

}
