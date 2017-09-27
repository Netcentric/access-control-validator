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

import org.apache.commons.lang3.StringUtils;

import biz.netcentric.aem.tools.acvalidator.gui.yaml.parser.YamlParserException;

/**
 * Node to store useradmin test subnodes.
 * 
 * @author Roland Gruber
 */
public class UserAdminNode extends ConfigurationNode {

	public static final String NAME = "useradmin";

	@Override
	public List<Class> getAllowedSubnodeClasses() {
		return Arrays.asList(new Class[] {
				ForLoopNode.class,
				AssignUserToGroupNode.class,
				CreateUserNode.class,
				ModifyUserNode.class,
				ModifyGroupNode.class,
				CreateGroupNode.class
		});
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
	 * Adds a user admin test node.
	 * 
	 * @param properties properties
	 * @throws YamlParserException error while parsing
	 */
	private void addTestNode(LinkedHashMap properties) throws YamlParserException {
		String action = (String) properties.get(UserAdminTestNode.ACTION);
		if (action != null) {
			action = action.trim();
		}
		if (StringUtils.isEmpty(action)) {
			throw new YamlParserException("No action found");
		}
		if (AssignUserToGroupNode.KEY.equals(action)) {
			AssignUserToGroupNode node = new AssignUserToGroupNode();
			node.setPropertiesFromYaml(properties);
			addSubnode(node);
		}
		else if (CreateUserNode.KEY.equals(action)) {
			CreateUserNode node = new CreateUserNode();
			node.setPropertiesFromYaml(properties);
			addSubnode(node);
		}
		else if (ModifyUserNode.KEY.equals(action)) {
			ModifyUserNode node = new ModifyUserNode();
			node.setPropertiesFromYaml(properties);
			addSubnode(node);
		}		
		else if (ModifyGroupNode.KEY.equals(action)) {
			ModifyGroupNode node = new ModifyGroupNode();
			node.setPropertiesFromYaml(properties);
			addSubnode(node);
		}		
		else if (CreateGroupNode.KEY.equals(action)) {
			CreateGroupNode node = new CreateGroupNode();
			node.setPropertiesFromYaml(properties);
			addSubnode(node);
		}
		else {
			throw new YamlParserException("Invalid action found: " + action);
		}
	}

	@Override
	public String getNodeName() {
		return NAME;
	}

}
