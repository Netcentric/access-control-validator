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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import biz.netcentric.aem.tools.acvalidator.gui.yaml.parser.YamlParserException;

/**
 * Node to store page tests.
 * 
 * @author Roland Gruber
 */
public class PageTestNode extends ConfigurationNode {

	public static final String SIMULATE = "simulate";
	public static final String DEACTIVATE_AFTER_PUBLISH = "deactivateAfterPublish";
	public static final String PROPERTY_NAMES_MODIFY = "propertyNamesModify";
	public static final String TEMPLATE = "template";
	public static final String PERMISSION = "permission";
	public static final String ACTIONS = "actions";
	public static final String PATH = "path";
	public static final String DEACTIVATE = "isDeactivate";
	
	Map<String, Property> properties = new LinkedHashMap();

	public PageTestNode() {
		super();
		properties.put(PATH, new Property(PATH));
		properties.put(ACTIONS, new Property(ACTIONS));
		properties.put(PERMISSION, new Property(PERMISSION));
		properties.put(TEMPLATE, new Property(TEMPLATE));
		properties.put(PROPERTY_NAMES_MODIFY, new Property(PROPERTY_NAMES_MODIFY));
		properties.put(DEACTIVATE_AFTER_PUBLISH, new Property(DEACTIVATE_AFTER_PUBLISH));
		properties.put(SIMULATE, new Property(SIMULATE));
		properties.put(DEACTIVATE, new Property(DEACTIVATE));
	}

	/**
	 * Copy constructor.
	 * 
	 * @param node original node.
	 */
	public PageTestNode(PageTestNode node) {
		super();
		List<Property> copiedProperties = copyProperties(node);
		properties = new LinkedHashMap<>();
		for (Property property : copiedProperties) {
			properties.put(property.getName(), property);
		}
	}

	@Override
	public List<Class> getAllowedSubnodeClasses() {
		return new ArrayList<>();
	}

	@Override
	public List<Property> getProperties() {
		return new ArrayList<>(properties.values());
	}
	public Map<String, Property> getPropertiesMap() {
		return this.properties;
	}

	@Override
	public String getNodeName() {
		return null;
	}

	/**
	 * Adds the properties from the Yaml.
	 * 
	 * @param properties properties
	 * @throws YamlParserException error while parsing
	 */
	public void setPropertiesFromYaml(LinkedHashMap properties) throws YamlParserException {
		for (Object key : properties.keySet()) {
			// TODO validate input (actions, ...)
			String keyString = (String) key;
			String value = properties.get(keyString).toString();
			setProperty(keyString, value);
		}
	}

	private void setProperty(String name, String value) throws YamlParserException {
		if (properties.containsKey(name)) {
			properties.get(name).setValue(value);
			return;
		}
		throw new YamlParserException("Unknown property: " + name);
	}
	
	/**
	 * Returns if the actions contain a create action.
	 * 
	 * @return is isPageCreateTest
	 * @throws YamlParserException error getting actions
	 */
	public boolean isPageCreateTest() throws YamlParserException {
		return getActions().contains(PageTestAction.CREATE);
	}
	
	/**
	 * Returns if the actions contain a read action.
	 * 
	 * @return is isPageReadTest
	 * @throws YamlParserException error getting actions
	 */
	public boolean isPageReadTest() throws YamlParserException {
		return getActions().contains(PageTestAction.READ);
	}
	
	/**
	 * Returns if the actions contain a delete action.
	 * 
	 * @return is isPageDeleteTest
	 * @throws YamlParserException error getting actions
	 */
	public boolean isPageDeleteTest() throws YamlParserException {
		return getActions().contains(PageTestAction.DELETE);
	}
	
	/**
	 * Returns if the actions contain a modify action.
	 * 
	 * @return is isPageModifyTest
	 * @throws YamlParserException error getting actions
	 */
	public boolean isPageModifyTest() throws YamlParserException {
		return getActions().contains(PageTestAction.MODIFY);
	}
	
	/**
	 * Returns if the actions contain a modify action.
	 * 
	 * @return is isPagePublishTest
	 * @throws YamlParserException error getting actions
	 */
	public boolean isPagePublishTest() throws YamlParserException {
		return getActions().contains(PageTestAction.PUBLISH);
	}
	
	/**
	 * Returns if the actions contain a modify action.
	 * 
	 * @return is isAclReadTest
	 * @throws YamlParserException error getting actions
	 */
	public boolean isAclReadTest() throws YamlParserException {
		return getActions().contains(PageTestAction.READ_ACL);
	}
	
	/**
	 * Returns if the actions contain a acl write action.
	 * 
	 * @return is isAclWriteTest
	 * @throws YamlParserException error getting actions
	 */
	public boolean isAclWriteTest() throws YamlParserException {
		return getActions().contains(PageTestAction.WRITE_ACL);
	}
	
	/**
	 * Returns the list of actions.
	 * 
	 * @return actions
	 * @throws YamlParserException invalid actions
	 */
	private List<PageTestAction> getActions() throws YamlParserException {
		Property actionProperty = properties.get(ACTIONS);
		String[] actionLabels = actionProperty.getValue().split("[ ,]+");
		List<PageTestAction> actions = new ArrayList<>();
		for (String label : actionLabels) {
			actions.add(PageTestAction.fromLabel(label));
		}
		return actions;
	}
	
	/**
	 * Returns the path property.
	 * 
	 * @return path
	 */
	public String getPath() {
		return properties.get(PATH).getValue();
	}
	
	/**
	 * Returns the template property.
	 * 
	 * @return path
	 */
	public String getTemplate() {
		return properties.get(TEMPLATE).getValue();
	}
	
	/**
	 * Returns if this is an allow or deny type.
	 * 
	 * @return is allow type
	 * @throws YamlParserException invalid type
	 */
	public boolean isAllow() throws YamlParserException {
		return PageTestPermission.fromLabel(properties.get(PERMISSION).getValue()).equals(PageTestPermission.ALLOW);
	}

	/**
	 * Returns if this is a test to be simulated.
	 * 
	 * @return run simulation
	 * @throws YamlParserException invalid type
	 */
	public boolean isSimulate() throws YamlParserException {
		String value = properties.get(SIMULATE).getValue();
		if (StringUtils.isEmpty(value)) {
			return false;
		}
		if ("true".equals(value)) {
			return true;
		}
		else if ("false".equals(value)) {
			return false;
		}
		throw new YamlParserException("Invalid value for " + SIMULATE + ": " + value);
	}

	public boolean isDeactivate() throws YamlParserException {
		String value = properties.get(DEACTIVATE).getValue();	
		if (StringUtils.isEmpty(value)) {
			return false;
		}
		if ("true".equals(value)) {
			return true;
		}
		else if ("false".equals(value)) {
			return false;
		}
		throw new YamlParserException("Invalid value for " + DEACTIVATE + ": " + value);
	}

}
