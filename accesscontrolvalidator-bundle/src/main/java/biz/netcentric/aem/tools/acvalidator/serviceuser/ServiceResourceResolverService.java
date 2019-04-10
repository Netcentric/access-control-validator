/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.aem.tools.acvalidator.serviceuser;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * Returns the service resource resolver.
 * 
 * @author Roland Gruber
 */
public interface ServiceResourceResolverService {

	/**
	 * Returns the resource resolver.
	 * 
	 * @return resource resolver
	 * @throws LoginException error getting resource resolver
	 */
	ResourceResolver getServiceResourceResolver() throws LoginException;

	ResourceResolver getServiceResourceResolver(String authorizableID) throws LoginException;


	Session getUserSession(SimpleCredentials credentials) throws javax.jcr.LoginException, RepositoryException;


	ResourceResolver getTestUserResourceResolver(String username, String password) throws LoginException;

}
