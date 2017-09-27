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
 * The root node of the configuration.
 * 
 * @author Roland Gruber
 *
 */
public class RootNode extends ConfigurationNode {

	@Override
	public List<Class> getAllowedSubnodeClasses() {
		return Arrays.asList(new Class[] {VariablesNode.class, TestsNode.class});
	}

	@Override
	public List<Property> getProperties() {
		return new ArrayList<>();
	}
	
	/**
	 * Creates the right nodes based on the Yaml data.
	 * 
	 * @param yamlNodes Yaml data
	 * @throws YamlParserException error in data structure
	 */
	public void addNodesFromYaml(List<LinkedHashMap> yamlNodes) throws YamlParserException {
		for (LinkedHashMap subnodes : yamlNodes) {
			for (Object identifier : subnodes.keySet()) {
				if (VariablesNode.NAME.equals(identifier)) {
					addVariablesNode((List<LinkedHashMap>) subnodes.get(VariablesNode.NAME));
				}
				else if (TestsNode.NAME.equals(identifier)) {
					addTestsNode((List<LinkedHashMap>) subnodes.get(TestsNode.NAME));
				}
				else {
					throw new YamlParserException("Unknown root node: " + identifier.toString());
				}
			}
		}
	}

	/**
	 * Adds the tests node.
	 * 
	 * @param subnodes subnodes
	 * @throws YamlParserException error while parsing
	 */
	private void addTestsNode(List<LinkedHashMap> subnodes) throws YamlParserException {
		TestsNode tests = new TestsNode();
		tests.addNodesFromYaml(subnodes);
		addSubnode(tests);
	}

	/**
	 * Adds a variables node.
	 * 
	 * @param subnodes subnodes
	 */
	private void addVariablesNode(List<LinkedHashMap> subnodes) {
		VariablesNode variables = new VariablesNode();
		variables.addNodesFromYaml(subnodes);
		addSubnode(variables);
	}

	@Override
	public String getNodeName() {
		return null;
	}

}
