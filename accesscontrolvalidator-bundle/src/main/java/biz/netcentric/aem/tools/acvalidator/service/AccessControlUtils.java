/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.aem.tools.acvalidator.service;
/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */



import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.AccessControlPolicy;
import javax.jcr.security.AccessControlPolicyIterator;

import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This class provides common access control related utilities. Mostly a copy of org.apache.jackrabbit.commons.AccessControlUtils. */
public class AccessControlUtils {

    private static final Logger LOG = LoggerFactory.getLogger(AccessControlUtils.class);

    private AccessControlUtils() {
    }
	
	  /** Utility that combines {@link AccessControlManager#getApplicablePolicies(String)} and
   * {@link AccessControlManager#getPolicies(String)} to retrieve a modifiable {@code JackrabbitAccessControlList} for the given path.<br>
   *
   * Note that the policy must be {@link AccessControlManager#setPolicy(String, javax.jcr.security.AccessControlPolicy) reapplied} and the
   * changes must be saved in order to make the AC modifications take effect.
   *
   * @param session The editing session.
   * @param absPath The absolute path of the target node.
   * @return A modifiable access control list or null if there is none.
   * @throws RepositoryException If an error occurs. */
  public static JackrabbitAccessControlList getAccessControlList(
          Session session, String absPath) throws RepositoryException {
      final AccessControlManager acMgr = session.getAccessControlManager();
      return getAccessControlList(acMgr, absPath);
  }

  /** Utility that combines {@link AccessControlManager#getApplicablePolicies(String)} and
   * {@link AccessControlManager#getPolicies(String)} to retrieve a modifiable {@code JackrabbitAccessControlList} for the given path.<br>
   *
   * Note that the policy must be {@link AccessControlManager#setPolicy(String, javax.jcr.security.AccessControlPolicy) reapplied} and the
   * changes must be saved in order to make the AC modifications take effect.
   *
   * @param accessControlManager The {@code AccessControlManager} .
   * @param absPath The absolute path of the target node.
   * @return A modifiable access control list or null if there is none.
   * @throws RepositoryException If an error occurs. */
  public static JackrabbitAccessControlList getAccessControlList(
          AccessControlManager accessControlManager, String absPath)
                  throws RepositoryException {
      // try applicable (new) ACLs
      final AccessControlPolicyIterator itr = accessControlManager
              .getApplicablePolicies(absPath);
      while (itr.hasNext()) {
          final AccessControlPolicy policy = itr.nextAccessControlPolicy();
          if (policy instanceof JackrabbitAccessControlList) {
              return (JackrabbitAccessControlList) policy;
          }
      }

      // try if there is an acl that has been set before
      final AccessControlPolicy[] pcls = accessControlManager.getPolicies(absPath);
      for (final AccessControlPolicy policy : pcls) {
          if (policy instanceof JackrabbitAccessControlList) {
              return (JackrabbitAccessControlList) policy;
          }
      }

      // no policy found
//      LOG.warn("no policy found for path: {}", absPath);
      return null;
  }
}
