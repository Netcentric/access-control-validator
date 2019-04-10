/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.aem.tools.acvalidator.model;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biz.netcentric.aem.tools.acvalidator.api.TestResult;
import biz.netcentric.aem.tools.acvalidator.model.pagetestcases.PageTestCase;
import biz.netcentric.aem.tools.acvalidator.serviceuser.ServiceResourceResolverService;


/**
 * A {@link AcTestSet} comprises of one or several {@link PageTestCase}, creates needed testusers and resolvers for executing the tescases
 * @author jochenkoschorke
 *
 */
public class AcTestSet {

	private final Logger LOG = LoggerFactory.getLogger(AcTestSet.class);


	private static final String ACVALIDATOR_TESTUSER_ID = "acvalidator-testuser";
	private static final String ACVALIDATOR_TESTUSER_PASSWORD = "test";

	private List<Testable> acTestCase;
	private String authorizableID;
	private String pathToTestfile;


	/**
	 * Constructor
	 *
	 * @param authorizableID user/group id
	 */
	public AcTestSet(String authorizableID, String pathToTestfile) {
		this.authorizableID = authorizableID;
		acTestCase = new ArrayList<>();
		this.pathToTestfile = pathToTestfile;
	}
	public void addAcTestCase(Testable testable){
		this.acTestCase.add(testable);
	}

	public String getAuthorizableID(){
		return this.authorizableID;
	}

	/**
	 * creates the needed testuser and resolver needed for the testcases, executes the tests and cleans up afterwards
	 * @param serviceResourceResolverService
	 * @return
	 * @throws RepositoryException
	 * @throws LoginException
	 */
	public List<TestResult> isOk(ServiceResourceResolverService serviceResourceResolverService) throws RepositoryException, LoginException {
		List<TestResult> resultList = new ArrayList<>();
		User testuser = null;
		Authorizable authorizableToTest = null;
		ResourceResolver serviceResourcerResolver = null;
		ResourceResolver testUserResolver = null;
		try {
			// create authorizables
			serviceResourcerResolver = serviceResourceResolverService.getServiceResourceResolver();

			UserManager userManager = getUserManager(serviceResourcerResolver);

			boolean isSystemUser = !userManager.getAuthorizable(authorizableID).isGroup();

            if (isSystemUser) {
                testUserResolver = serviceResourceResolverService.getServiceResourceResolver(authorizableID);
                authorizableToTest = userManager.getAuthorizable(authorizableID);

            } else {

			testuser =  userManager.createUser(ACVALIDATOR_TESTUSER_ID, ACVALIDATOR_TESTUSER_PASSWORD);
			authorizableToTest = getTestGroup(getUserManager(serviceResourcerResolver), authorizableID, testuser);

			// we need to persist the created testuser in order to be able to get a resolver for him
			serviceResourcerResolver.commit();
			LOG.debug("comitting serviceResourcerResolver to persist testuser");
			// create ResourceResolver for the testuser based on his permissions
			testUserResolver = serviceResourceResolverService.getTestUserResourceResolver(ACVALIDATOR_TESTUSER_ID, ACVALIDATOR_TESTUSER_PASSWORD);
            }

			// execute all testcases for the testuser
			for(Testable testable: acTestCase){
				resultList.add(testable.isOk(serviceResourcerResolver, testUserResolver, authorizableToTest));
			}
		} catch (PersistenceException e) {
			throw new RepositoryException(e);
		} finally {
			// clean up temporary testuser
			if(authorizableToTest != null  && authorizableToTest instanceof Group && testuser != null){
                ((Group)authorizableToTest).removeMember(testuser);
			}
			if(testuser != null){
				testuser.remove();
			}

			// close resolvers

			if(testUserResolver != null){
				testUserResolver.revert();
				testUserResolver.close();
			}

			if(serviceResourcerResolver != null){
				try {
					if(serviceResourcerResolver.hasChanges()){
						serviceResourcerResolver.commit();
					}
					serviceResourcerResolver.close();
				} catch (PersistenceException e) {
					throw new RepositoryException(e);
				}
			}
		}
		return resultList;
	}

	private Group getTestGroup(UserManager userManager, String authorizableID, User testuser) throws RepositoryException{
		Group group = (Group) userManager.getAuthorizable(authorizableID);
		group.addMember(testuser);
		return group;
	}

	private UserManager getUserManager(ResourceResolver resolver){
		UserManager userManager = resolver.adaptTo(UserManager.class);
		if(userManager == null){
			throw new IllegalStateException("Could not adapt ResourceResolver to UserManager!");
		}
		return userManager;
	}

}
