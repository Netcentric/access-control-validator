/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.aem.tools.acvalidator.gui.yaml.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import biz.netcentric.aem.tools.acvalidator.gui.yaml.parser.YamlParserException;

/**
 * Node to store ACL restriction test subnodes.
 */
public class AclRestrictionsNode extends ConfigurationNode {

    public static final String NAME = "aclrestrictions";

    @Override
    public List<Class> getAllowedSubnodeClasses() {
        return Arrays.asList(new Class[] {AclRestrictionTestNode.class});
    }

    @Override
    public List<Property> getProperties() {
        return new ArrayList<>();
    }

    @Override
    public String getNodeName() {
        return NAME;
    }

    public void addNodesFromYaml(List<LinkedHashMap> subnodes) throws YamlParserException {
        for (LinkedHashMap subnode : subnodes) {
            AclRestrictionTestNode node = new AclRestrictionTestNode();
            node.setPropertiesFromYaml(subnode);
            addSubnode(node);
        }
    }

}
