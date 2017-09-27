/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.aem.tools.acvalidator.gui.yaml.parser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.ConfigurationNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.Property;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.RootNode;

/**
 * Writes a node structure to a Yaml file.
 * 
 * @author Roland Gruber
 *
 */
public class YamlWriter {
	
	/**
	 * Parses the node structure and outputs Yaml.
	 * 
	 * @param root node
	 * @return Yaml
	 */
	public String getYaml(RootNode root) {
		Yaml yamlParser = new Yaml(getDumpOtions());
		List<Map> list = new ArrayList<>();
		for (ConfigurationNode node : root.getSubnodes()) {
			parseNode(node, list);
		}
		return yamlParser.dump(list);
	}
	
	private DumperOptions getDumpOtions() {
		DumperOptions options = new DumperOptions();
		options.setIndent(4);
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		return options;
	}

	/**
	 * Parses a node.
	 * 
	 * @param node node
	 * @param list list where to add node data
	 * @return list of maps
	 */
	private void parseNode(ConfigurationNode node, List<Map> list) {
		if (node.getProperties().isEmpty()) {
			Map nodeMap = new LinkedHashMap<>();
			List<Map> nodeSubMap = new ArrayList<>();
			nodeMap.put(node.getNodeName(), nodeSubMap);
			list.add(nodeMap);
			for (ConfigurationNode subNode : node.getSubnodes()) {
				parseNode(subNode, nodeSubMap);
			}
		}
		else {
			Map nodeMap = new LinkedHashMap<>();
			if (node.getNodeName() != null) {
				List<Map> nodeSubMap = new ArrayList<>();
				nodeMap.put(node.getNodeName(), nodeSubMap);
				Map<String, String> propertyMap = new LinkedHashMap<>();
				for (Property property : node.getProperties()) {
					propertyMap.put(property.getName(), property.getValue());
				}
				nodeSubMap.add(propertyMap);
			}
			else {
				addProperties(node, nodeMap);
			}
			list.add(nodeMap);
		}
	}

	private void addProperties(ConfigurationNode node, Map nodeMap) {
		for (Property property : node.getProperties()) {
			if (property.getValue() != null) {
				nodeMap.put(property.getName(), property.getValue());
			}
		}
	}

}
