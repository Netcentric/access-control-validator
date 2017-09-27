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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import biz.netcentric.aem.tools.acvalidator.gui.yaml.parser.YamlParserException;

/**
 * Node to represent a FOR loop.
 * 
 * @author Roland Gruber
 */
public class ForLoopNode extends ConfigurationNode {

	private static final Pattern FOR_PATTERN = Pattern.compile("^FOR[ ]+([a-zA-Z0-9_-]+)[ ]+IN[ ]+\\[[ ]+([a-zA-Z0-9, _-]+)[ ]+\\][ ]*$");
	private static final Pattern VARS_PATTERN = Pattern.compile("\\[[ ]+([a-zA-Z0-9, _-]+)[ ]+\\]");

	private String name;
	private ConfigurationNode parent;

	/**
	 * Constructor
	 * 
	 * @param properties properties
	 * @param parent parent node
	 * @throws YamlParserException error during parsing
	 */
	public ForLoopNode(LinkedHashMap properties, ConfigurationNode parent) throws YamlParserException {
		if (!isValidForLoopName(properties)) {
			throw new YamlParserException("Invalid configuration: " + properties.toString());
		}
		this.parent = parent;
		for (Object key : properties.keySet()) {
			name = (String) key;
			List<LinkedHashMap> subnodes = (List<LinkedHashMap>) properties.get(key);
			for (LinkedHashMap subNode : subnodes) {
				if (parent instanceof PagesNode) {
					addPageTestNode(subNode);
				}
				else if (parent instanceof UserAdminNode) {
					addUserTestNode(subNode);
				}
			}
		}
	}

	/**
	 * Adds a new page test node.
	 * 
	 * @param subnodes subnodes
	 * @param name node name
	 * @throws YamlParserException 
	 */
	private void addPageTestNode(LinkedHashMap properties) throws YamlParserException {
		PageTestNode pageTestNode = new PageTestNode();
		pageTestNode.setPropertiesFromYaml(properties);
		addSubnode(pageTestNode);
	}

	/**
	 * Adds a new user admin test node.
	 * 
	 * @param subnodes subnodes
	 * @param name node name
	 * @throws YamlParserException 
	 */
	private void addUserTestNode(LinkedHashMap properties) throws YamlParserException {
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
		else if (CreateGroupNode.KEY.equals(action)) {
			CreateGroupNode node = new CreateGroupNode();
			node.setPropertiesFromYaml(properties);
			addSubnode(node);
		}
		else if (CreateUserNode.KEY.equals(action)) {
			CreateUserNode node = new CreateUserNode();
			node.setPropertiesFromYaml(properties);
			addSubnode(node);
		}
		else if (ModifyGroupNode.KEY.equals(action)) {
			ModifyGroupNode node = new ModifyGroupNode();
			node.setPropertiesFromYaml(properties);
			addSubnode(node);
		}
		else if (ModifyUserNode.KEY.equals(action)) {
			ModifyUserNode node = new ModifyUserNode();
			node.setPropertiesFromYaml(properties);
			addSubnode(node);
		}
		else {
			throw new YamlParserException("Invalid action found: " + action);
		}
	}

	@Override
	public List<Class> getAllowedSubnodeClasses() {
		if (parent instanceof PagesNode) {
			return Arrays.asList(new Class[] {TestsNode.class});
		}
		return Arrays.asList(new Class[] {
				AssignUserToGroupNode.class,
				CreateGroupNode.class,
				CreateUserNode.class,
				ModifyGroupNode.class,
				ModifyUserNode.class});
	}

	@Override
	public List<Property> getProperties() {
		return new ArrayList<>();
	}

	@Override
	public boolean allowsCustomProperties() {
		return false;
	}

	@Override
	public String getNodeName() {
		return name;
	}

	/**
	 * Checks if the name of the node is valid for this loop node.
	 * 
	 * @param properties properties
	 * @return is valid
	 */
	public static boolean isValidForLoopName(LinkedHashMap properties) {
		if (properties.size() != 1) {
			return false;
		}
		return true;
	}
	
	/**
	 * Returns the name of the loop variable.
	 * 
	 * @return name
	 * @throws YamlParserException error getting name
	 */
	private String getVariableName() throws YamlParserException {
		Matcher matcher = FOR_PATTERN.matcher(name);
		if (matcher.matches()) {
			return matcher.group(1);
		}
		throw new YamlParserException("For loop name does not match format: " + name);
	}
	
	/**
	 * Returns the values of the loop variable.
	 * 
	 * @return values
	 * @throws YamlParserException error getting values
	 */
	private List<String> getVariableValues() throws YamlParserException {
		Matcher matcher = VARS_PATTERN.matcher(name);
		if (matcher.find()) {
			String vars = matcher.group(1);
			return Arrays.asList(StringUtils.split(vars, " ,"));
		}
		throw new YamlParserException("For loop name does not match format: " + name);
	}

	@Override
	public List<ConfigurationNode> unroll(Map<String, String> variables) throws YamlParserException {
		List<ConfigurationNode> result = new ArrayList<>();
		for (String value : getVariableValues()) {
			Map<String, String> variablesLoop = new HashMap<>(variables);
			variablesLoop.put(getVariableName(), value);
			for (ConfigurationNode subnode : getSubnodes()) {
				result.addAll(copy(subnode).unroll(variablesLoop));
			}
		}
		return result;
	}
	
	/**
	 * Copies the subnodes.
	 * 
	 * @param node subnode
	 * @return copy
	 */
	private ConfigurationNode copy(ConfigurationNode node) {
		if (node instanceof PageTestNode) {
			return new PageTestNode((PageTestNode) node);
		}
		if (node instanceof AssignUserToGroupNode) {
			return new AssignUserToGroupNode((AssignUserToGroupNode) node);
		}
		if (node instanceof CreateUserNode) {
			return new CreateUserNode((CreateUserNode) node);
		}
		if (node instanceof ModifyUserNode) {
			return new ModifyUserNode((ModifyUserNode) node);
		}
		if (node instanceof ModifyGroupNode) {
			return new ModifyGroupNode((ModifyGroupNode) node);
		}
		if (node instanceof CreateGroupNode) {
			return new CreateGroupNode((CreateGroupNode) node);
		}
		throw new IllegalStateException("unknow node to copy " + node.getClass().getName());
	}

}
