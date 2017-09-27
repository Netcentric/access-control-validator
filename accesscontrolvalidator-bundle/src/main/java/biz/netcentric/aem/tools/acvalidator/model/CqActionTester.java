package biz.netcentric.aem.tools.acvalidator.model;

/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
import java.security.Principal;
import java.util.Map;
import java.util.Set;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.sling.api.resource.ResourceResolver;

import com.day.cq.security.util.CqActions;

public class CqActionTester {
	
	private Session session;
	
	public CqActionTester(ResourceResolver resolver){
		this.session = resolver.adaptTo(Session.class);
		if(session == null){
			throw new IllegalStateException("Could not adapt ResourceResolver to Session!");
		}
	}
	
	public boolean check(Authorizable authorizable, String path, Set<String> expectedActions) throws RepositoryException{
		final CqActions cqActions = new CqActions(session);
		final Set<Principal> principals = PermissionUtils.getPrincipals(authorizable, session);
		final Map<String, Boolean> actionsMapFromRepository = PermissionUtils.getActionsMap(cqActions, authorizable.getID(), principals, path);

		return check(expectedActions, actionsMapFromRepository);
	}

	protected boolean check(final Set<String> expectedActions, final Map<String, Boolean> actionsMapFromRepository) {
		for(String expectedAction : expectedActions){
			if(!actionsMapFromRepository.isEmpty() && !actionsMapFromRepository.get(expectedAction)){
				return false;
			}
		}
		return true;
	}

}
