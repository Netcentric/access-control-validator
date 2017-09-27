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

import biz.netcentric.aem.tools.acvalidator.gui.yaml.parser.YamlParserException;

/**
 * Node to store user admin tests.
 * 
 * @author Roland Gruber
 */
public abstract class UserAdminTestNode extends ConfigurationNode {

	public static final String PERMISSION = "permission";
	public static final String SIMULATE = "simulate";
	public static final String ACTION = "action";
	
	protected List<Property> properties = new ArrayList<>();
	
	/**
	 * Constructor
	 */
	public UserAdminTestNode() {
		properties.add(new Property(ACTION));
		properties.add(new Property(PERMISSION));
		properties.add(new Property(SIMULATE));
	}
	public Property getPropertyByName(String name){
		for(Property property:this.properties){
			if (property.getName().equals(name)){
				return property;
			}
		}
		return null;
	}
	
	@Override
	public List<Class> getAllowedSubnodeClasses() {
		return new ArrayList<>();
	}

	/**
	 * Returns the key property.
	 * 
	 * @return key property name
	 */
	protected abstract String getKey();

	@Override
	public List<Property> getProperties() {
		return properties;
	}
	
	@Override
	public String getNodeName() {
		return null;
	}

	/**
	 * Adds the properties from the Yaml.
	 * 
	 * @param properties properties
	 * @throws YamlParserException 
	 */
	public void setPropertiesFromYaml(LinkedHashMap properties) throws YamlParserException {
		int index = 0;
		for (Object key : properties.keySet()) {
			String keyString = (String) key;
			String value = (properties.get(keyString) != null) ? properties.get(keyString).toString() : null;
			setProperty(keyString, value, index);
			index++;
		}
	}

	/**
	 * Sets a property and reorders it to the given position.
	 * 
	 * @param name property name
	 * @param value value
	 * @param index new position
	 * @throws YamlParserException error while parsing
	 */
	private void setProperty(String name, String value, int index) throws YamlParserException {
		for (Property property : getProperties()) {
			if (property.getName().equals(name)) {
				property.setValue(value);
				if (properties.indexOf(property) != index) {
					Property temp = properties.get(index);
					int oldIndex = properties.indexOf(property);
					properties.set(index, property);
					properties.set(oldIndex, temp);
				}
				return;
			}
		}
		throw new YamlParserException("Unknown property: " + name);
	}
	
	/**
	 * Returns if the actions contain a croup create action.
	 * 
	 * @return is create
	 * @throws YamlParserException error getting actions
	 */
	public boolean isGroupCreateTest() throws YamlParserException {
		return this.properties.get(0).getValue().equals(UserTestAction.CREATE_GROUP.getLabel());
	}
	
	/**
	 * Returns if the actions contain a user create action.
	 * 
	 * @return is create
	 * @throws YamlParserException error getting actions
	 */
	public boolean isUserCreateTest() throws YamlParserException {
		return this.properties.get(0).getValue().equals(UserTestAction.CREATE_USER.getLabel());
	}
	/**
	 * Returns if the actions contain a user create action.
	 * 
	 * @return is create
	 * @throws YamlParserException error getting actions
	 */
	public boolean isAssignUserToGroupTest() throws YamlParserException {
		return this.properties.get(0).getValue().equals(UserTestAction.ASSIGN_USER_TO_GROUP.getLabel());
	}
	/**
	 * Returns if the actions contain a modify group action.
	 * 
	 * @return is create
	 * @throws YamlParserException error getting actions
	 */
	public boolean isModifyGroupTest() throws YamlParserException {
		return this.properties.get(0).getValue().equals(UserTestAction.MODIFY_GROUP.getLabel());
	}
	
	/**
	 * Returns if the actions contain a modify group action.
	 * 
	 * @return is create
	 * @throws YamlParserException error getting actions
	 */
	public boolean isModifyUserTest() throws YamlParserException {
		return this.properties.get(0).getValue().equals(UserTestAction.MODIFY_USER.getLabel());
	}
	
	/**
	 * Returns if this is an allow or deny type.
	 * 
	 * @return is allow type
	 * @throws YamlParserException invalid type
	 */
	public boolean isAllow() throws YamlParserException {
		return PageTestPermission.fromLabel(this.getPropertyByName("permission").getValue()).equals(PageTestPermission.ALLOW);
	}

}
