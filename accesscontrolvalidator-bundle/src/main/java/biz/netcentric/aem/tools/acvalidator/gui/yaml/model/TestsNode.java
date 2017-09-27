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
 * Node to store test cases.
 * 
 * @author Roland Gruber
 */
public class TestsNode extends ConfigurationNode {

	public static final String NAME = "tests";

	@Override
	public List<Class> getAllowedSubnodeClasses() {
		return Arrays.asList(new Class[] {PrincipalNode.class});
	}

	@Override
	public List<Property> getProperties() {
		return new ArrayList<>();
	}

	/**
	 * Adds the nodes from the Yaml.
	 * 
	 * @param subnodes subnodes
	 * @throws YamlParserException error while parsing
	 */
	public void addNodesFromYaml(List<LinkedHashMap> subnodes) throws YamlParserException {
		for (LinkedHashMap principalsMap : subnodes) {
			if (principalsMap.size() != 1) {
				throw new YamlParserException("The principal node may only contain a name and no additional properties.");
			}
			for (Object key : principalsMap.keySet()) {
				String principal = (String) key;
				PrincipalNode principalNode = new PrincipalNode(principal);
				List<LinkedHashMap> principalSubnodes = (List<LinkedHashMap>) principalsMap.get(key);
				principalNode.addNodesFromYaml(principalSubnodes);
				addSubnode(principalNode);
			}
		}
	}

	@Override
	public String getNodeName() {
		return NAME;
	}

}
