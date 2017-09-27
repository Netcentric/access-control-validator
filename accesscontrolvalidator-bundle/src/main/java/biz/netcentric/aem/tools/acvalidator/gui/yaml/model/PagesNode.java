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
 * Node to store page test subnodes.
 * 
 * @author Roland Gruber
 */
public class PagesNode extends ConfigurationNode {

	public static final String NAME = "pages";

	@Override
	public List<Class> getAllowedSubnodeClasses() {
		return Arrays.asList(new Class[] {PageTestNode.class});
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
		for (LinkedHashMap subnode : subnodes) {
			if (ForLoopNode.isValidForLoopName(subnode)) {
				addForLoopNode(subnode);
			}
			else {
				addTestNode(subnode);
			}
		}
	}

	/**
	 * Adds a new for-loop node.
	 * 
	 * @param subnodes subnodes
	 * @param name node name
	 * @throws YamlParserException error during parsing
	 */
	private void addForLoopNode(LinkedHashMap properties) throws YamlParserException {
		ForLoopNode loopNode = new ForLoopNode(properties, this);
		addSubnode(loopNode);
	}

	/**
	 * Adds a new page test node.
	 * 
	 * @param subnodes subnodes
	 * @param name node name
	 * @throws YamlParserException 
	 */
	private void addTestNode(LinkedHashMap properties) throws YamlParserException {
		PageTestNode pageTestNode = new PageTestNode();
		pageTestNode.setPropertiesFromYaml(properties);
		addSubnode(pageTestNode);
	}

	@Override
	public String getNodeName() {
		return NAME;
	}

}
