/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.aem.tools.acvalidator.model.restrictiontestcases;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;

import biz.netcentric.aem.tools.acvalidator.api.TestResult;
import biz.netcentric.aem.tools.acvalidator.model.Testable;

/**
 * Inspects the rep:restrictions node of an ACE to verify that restriction
 * properties (standard Oak restrictions such as rep:glob, rep:ntNames, or
 * custom AEM metadata restrictions registered via DefaultRestrictionProviderConfiguration)
 * are present with the expected values.
 *
 * Uses the admin (service) session — no impersonation required.
 */
public class AceRestrictionTest implements Testable {

    private static final String TEST_LABEL = "ACE Restriction Test";
    private static final String REP_POLICY = "rep:policy";
    private static final String REP_RESTRICTIONS = "rep:restrictions";
    private static final String REP_PRINCIPAL_NAME = "rep:principalName";
    private static final String REP_PRIVILEGES = "rep:privileges";
    private static final String REP_GRANT_ACE = "rep:GrantACE";
    private static final String REP_DENY_ACE = "rep:DenyACE";

    private final String path;
    private final String privilege;
    private final boolean isAllow;
    private final Map<String, String> restrictions;

    public AceRestrictionTest(String path, String privilege, boolean isAllow, Map<String, String> restrictions) {
        this.path = path;
        this.privilege = privilege;
        this.isAllow = isAllow;
        this.restrictions = restrictions;
    }

    @Override
    public TestResult isOk(ResourceResolver serviceResourceResolver, ResourceResolver testUserResolver, Authorizable authorizable)
            throws RepositoryException, LoginException {

        String principalName = authorizable.getID();
        String description = "path: " + path + ", principal: " + principalName
                + ", privilege: " + privilege + ", permission: " + (isAllow ? "allow" : "deny");

        Session session = serviceResourceResolver.adaptTo(Session.class);
        if (session == null) {
            return fail(principalName, description, "Could not adapt service ResourceResolver to JCR Session");
        }

        String policyPath = path + "/" + REP_POLICY;
        if (!session.nodeExists(policyPath)) {
            return fail(principalName, description, "No rep:policy node found at " + path);
        }

        Node policyNode = session.getNode(policyPath);
        NodeIterator aceIterator = policyNode.getNodes();

        List<String> aceFailures = new ArrayList<>();
        while (aceIterator.hasNext()) {
            Node aceNode = aceIterator.nextNode();
            if (!isMatchingAce(aceNode, principalName)) {
                continue;
            }
            TestResult result = checkRestrictions(aceNode, principalName, description);
            if (result.isOk()) {
                return result;
            }
            aceFailures.add(result.getErrorMessage());
        }

        if (!aceFailures.isEmpty()) {
            return fail(principalName, description,
                    "Found " + aceFailures.size() + " matching ACE(s) but none had the expected restrictions: " + aceFailures);
        }

        return fail(principalName, description,
                "No matching ACE found for principal '" + principalName + "' with privilege '" + privilege + "'");
    }

    private boolean isMatchingAce(Node aceNode, String principalName) throws RepositoryException {
        String nodeType = aceNode.getPrimaryNodeType().getName();
        boolean typeMatches = isAllow ? REP_GRANT_ACE.equals(nodeType) : REP_DENY_ACE.equals(nodeType);
        if (!typeMatches) {
            return false;
        }
        if (!aceNode.hasProperty(REP_PRINCIPAL_NAME)) {
            return false;
        }
        if (!principalName.equals(aceNode.getProperty(REP_PRINCIPAL_NAME).getString())) {
            return false;
        }
        return hasPrivilege(aceNode);
    }

    private boolean hasPrivilege(Node aceNode) throws RepositoryException {
        if (!aceNode.hasProperty(REP_PRIVILEGES)) {
            return false;
        }
        for (Value v : aceNode.getProperty(REP_PRIVILEGES).getValues()) {
            if (privilege.equals(v.getString())) {
                return true;
            }
        }
        return false;
    }

    private TestResult checkRestrictions(Node aceNode, String principalName, String description) throws RepositoryException {
        if (restrictions.isEmpty()) {
            return pass(principalName, description);
        }
        if (!aceNode.hasNode(REP_RESTRICTIONS)) {
            return fail(principalName, description,
                    "ACE has no rep:restrictions node but expected: " + restrictions.keySet());
        }
        Node restrictionsNode = aceNode.getNode(REP_RESTRICTIONS);
        for (Map.Entry<String, String> expected : restrictions.entrySet()) {
            String propName = expected.getKey();
            String expectedValue = expected.getValue();
            if (!restrictionsNode.hasProperty(propName)) {
                return fail(principalName, description, "Missing restriction property: " + propName);
            }
            javax.jcr.Property prop = restrictionsNode.getProperty(propName);
            String actualValue = prop.isMultiple() ? joinValues(prop.getValues()) : prop.getString();
            if (!expectedValue.equals(actualValue)) {
                return fail(principalName, description,
                        "Restriction '" + propName + "': expected '" + expectedValue + "' but was '" + actualValue + "'");
            }
        }
        return pass(principalName, description);
    }

    private String joinValues(Value[] values) throws RepositoryException {
        StringBuilder sb = new StringBuilder();
        for (Value v : values) {
            if (sb.length() > 0) sb.append(",");
            sb.append(v.getString());
        }
        return sb.toString();
    }

    private TestResult pass(String principalName, String description) {
        return new TestResult(principalName, TEST_LABEL, description, true, "");
    }

    private TestResult fail(String principalName, String description, String error) {
        return new TestResult(principalName, TEST_LABEL, description, false, error);
    }

}
