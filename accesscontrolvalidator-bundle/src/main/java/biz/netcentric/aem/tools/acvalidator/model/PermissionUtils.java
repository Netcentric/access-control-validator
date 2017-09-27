/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.aem.tools.acvalidator.model;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.principal.PrincipalIterator;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.security.util.CqActions;



public class PermissionUtils {

	private static final Set<String> CQ_ACTION_SET = new HashSet<String>(Arrays.asList(CqActions.ACTIONS));
	private static final Logger LOG = LoggerFactory.getLogger(PermissionUtils.class);

	public static Map<String, Boolean> getActionsMap(final CqActions cqActions, final String authorizableId, final Set<Principal> principals, final String path) {
		Map<String, Boolean> actionMap;
		try {
			actionMap = getActions(cqActions, path, principals);
		} catch (final RepositoryException e) {
			LOG.debug("Failed to retrieve CQ Actions for " + authorizableId + " at " + path);
			actionMap = Collections.emptyMap();
		}
		return actionMap;
	}

	public static Map<String, Boolean> getActions(final CqActions cqActions, final String path, final Set<Principal> principals)
			throws RepositoryException {
		final Map<String, Boolean> actions = new LinkedHashMap<String, Boolean>();
		final Collection<String> allows = cqActions.getAllowedActions(path, principals);

		for (final String action : CQ_ACTION_SET) {
			final boolean isAllowed = allows.contains(action);
			actions.put(action, Boolean.valueOf(isAllowed));
		}
		return actions;
	}

	public static Authorizable getAuthorizable(final String authorizableId, final Session session)
			throws RepositoryException {
		final UserManager uMgr = ((JackrabbitSession) session).getUserManager();
		final Authorizable authorizable = uMgr.getAuthorizable(authorizableId);

		return authorizable;
	}

	public static Set<Principal> getPrincipals(final Authorizable authorizable, final Session session)
			throws RepositoryException {
		final Set<Principal> principals = new LinkedHashSet<Principal>();
		final Principal principal = authorizable.getPrincipal();
		principals.add(principal);

		for (final PrincipalIterator it = ((JackrabbitSession) session).getPrincipalManager().getGroupMembership(principal); it.hasNext();) {
			principals.add(it.nextPrincipal());
		}

		return principals;
	}

}
