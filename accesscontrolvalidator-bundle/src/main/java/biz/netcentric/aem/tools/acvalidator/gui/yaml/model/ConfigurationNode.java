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
import java.util.List;
import java.util.Map;

import biz.netcentric.aem.tools.acvalidator.gui.yaml.parser.VariableHelper;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.parser.YamlParserException;

/**
 * Represents a node in the configuration file.
 * 
 * @author Roland Gruber
 */
public abstract class ConfigurationNode {
	
	private List<ConfigurationNode> subnodes = new ArrayList<>();
	
	/**
	 * Returns the list of allowed subnode classes.
	 * 
	 * @return classes
	 */
	public abstract List<Class> getAllowedSubnodeClasses();
	
	/**
	 * Returns the subnodes.
	 * 
	 * @return subnodes
	 */
	public List<ConfigurationNode> getSubnodes() {
		return subnodes;
	}
	
	/**
	 * Returns the list of possible properties.
	 * 
	 * @return properties
	 */
	public abstract List<Property> getProperties();
	
	/**
	 * Allows to set properties with custom names.
	 * 
	 * @return custom properties allowed
	 */
	public boolean allowsCustomProperties() {
		return false;
	}

	/**
	 * Allows to set a custom name.
	 * 
	 * @return custom name allowed
	 */
	public boolean allowsCustomName() {
		return false;
	}

	/**
	 * Adds a subnode.
	 * 
	 * @param node subnode
	 */
	public void addSubnode(ConfigurationNode node) {
		subnodes.add(node);
	}

	/**
	 * Removes a subnode.
	 * 
	 * @param node subnode
	 */
	public void removeSubnode(ConfigurationNode node) {
		subnodes.remove(node);
	}

	/**
	 * Returns the node name.
	 * 
	 * @return node name
	 */
	public abstract String getNodeName();

	/**
	 * Unrolls the node by replacing variables and extracting loops.
	 * 
	 * @param variables variables
	 * @return list of unrolled nodes
	 * @throws YamlParserException error during unroll
	 */
	public List<ConfigurationNode> unroll(Map<String, String> variables) throws YamlParserException {
		for (Property property : getProperties()) {
			property.setValue(VariableHelper.replace(property.getValue(), variables));
		}
		List<ConfigurationNode> subnodesNew = new ArrayList<>();
		for (ConfigurationNode subnode : getSubnodes()) {
			subnodesNew.addAll(subnode.unroll(variables));
		}
		subnodes = subnodesNew;
		return Arrays.asList(this);
	}
	
	protected List<Property> copyProperties(ConfigurationNode node) {
		List<Property> copy = new ArrayList<>();
		for (Property property : node.getProperties()) {
			copy.add(new Property(property.getName(), property.getValue()));
		}
		return copy;
	}
		
}
