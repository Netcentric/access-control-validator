/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.aem.tools.acvalidator.serviceuser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;


/**
 * Returns service resource resolvers.
 * 
 * @author Roland Gruber
 */
@Component
@Service
public class ServiceResourceResolverServiceImpl implements ServiceResourceResolverService {

	@Reference
	ResourceResolverFactory resourceResolverFactory;

	@Reference
	SlingRepository repository;

	@Override
	public ResourceResolver getServiceResourceResolver() throws LoginException {
		final Map<String, Object> authenticationInfo = new HashMap<>();
		authenticationInfo.put(ResourceResolverFactory.SUBSERVICE, "acvalidator");
		return resourceResolverFactory.getServiceResourceResolver(authenticationInfo);
	}

	@Override
	public Session getUserSession(SimpleCredentials credentials) throws javax.jcr.LoginException, RepositoryException {
		javax.jcr.Session session = repository.login(credentials);
		return session;
	}

	@Override
	public ResourceResolver getTestUserResourceResolver(String username, String password) throws LoginException {
		final Map<String, Object> authenticationInfo = new HashMap<>();
		authenticationInfo.put(ResourceResolverFactory.USER, username);
		authenticationInfo.put(ResourceResolverFactory.PASSWORD, password.toCharArray());
		return resourceResolverFactory.getResourceResolver(authenticationInfo);
	}

	@Override
	public ResourceResolver getSystemUserResourceResolver(String authorizableID) {
		ResourceResolver adminResolver = null;
		Session systemUserSession = null;
		try {
			try {
				adminResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
				Session adminSession = adminResolver.adaptTo(Session.class);
				systemUserSession = adminSession.impersonate(new SimpleCredentials(authorizableID, new char[0]));
				return resourceResolverFactory.getResourceResolver(Collections.singletonMap("user.jcr.session", (Object) systemUserSession));
			} catch (RepositoryException | LoginException e) {
				throw new IllegalStateException(e);
			}
		} finally {
			if(adminResolver != null) {
				adminResolver.close();
			}
		}

	}

}
