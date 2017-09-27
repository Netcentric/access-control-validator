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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Node to store variables.
 * 
 * @author Roland Gruber
 */
public class VariablesNode extends ConfigurationNode {

	public static final String NAME = "variables";

	private List<Property> properties = new ArrayList<>();

	@Override
	public List<Class> getAllowedSubnodeClasses() {
		return new ArrayList<>();
	}

	@Override
	public List<Property> getProperties() {
		return properties;
	}

	@Override
	public boolean allowsCustomProperties() {
		return true;
	}

	/**
	 * Adds the nodes from the Yaml.
	 * 
	 * @param subnodes subnodes
	 */
	public void addNodesFromYaml(List<LinkedHashMap> subnodes) {
		for (LinkedHashMap variablesMap : subnodes) {
			for (Object key : variablesMap.keySet()) {
				String value = (String) variablesMap.get(key);
				properties.add(new Property((String) key, value));
			}
		}
	}

	@Override
	public String getNodeName() {
		return NAME;
	}

	/**
	 * Returns a map of all variable names and its value.
	 * 
	 * @return map
	 */
	public Map<String, String> getVariables() {
		Map<String, String> variables = new HashMap<>();
		for (Property property : getProperties()) {
			variables.put(property.getName(), property.getValue());
		}
		return variables;
	}

}
