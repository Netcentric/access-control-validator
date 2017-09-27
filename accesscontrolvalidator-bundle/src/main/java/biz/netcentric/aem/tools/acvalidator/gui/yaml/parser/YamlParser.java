/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.aem.tools.acvalidator.gui.yaml.parser;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.ConfigurationNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.RootNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.VariablesNode;

/**
 * Parses the Yaml file.
 * 
 * @author Roland Gruber
 */
public class YamlParser {
	
	/**
	 * Parses the Yaml file.
	 * 
	 * @param fileContent file content
	 * @return root node
	 * @throws YamlParserException error parsing file
	 */
	public RootNode parse(String fileContent) throws YamlParserException {
		Yaml yamlParser = new Yaml();
		List<LinkedHashMap> yamlRootList = (List<LinkedHashMap>) yamlParser.load(fileContent);
		RootNode root = new RootNode();
		root.addNodesFromYaml(yamlRootList);
		return root;
	}
	
	/**
	 * Unrolls any loops and replaces all variables.
	 * 
	 * @param original original model
	 * @return unrolled model
	 * @throws YamlParserException error during unroll
	 */
	public RootNode unrollAndReplaceVariables(RootNode original) throws YamlParserException {
		Map<String, String> variables = getVariables(original);
		RootNode root = new RootNode();
		for (ConfigurationNode node : original.getSubnodes()) {
			if (node instanceof VariablesNode) {
				// skip variables
				continue;
			}
			List<ConfigurationNode> unrolledNodes = node.unroll(variables);
			for (ConfigurationNode unrolledNode : unrolledNodes) {
				root.addSubnode(unrolledNode);
			}
		}
		return root;
	}

	/**
	 * Extracts all variables and handles also stacked variables (e.g. "${DAM}/p1").
	 * 
	 * @param root root node
	 * @return variables
	 * @throws YamlParserException error getting variables
	 */
	protected Map<String, String> getVariables(RootNode root) throws YamlParserException {
		Map<String, String> variables = new HashMap<>();
		for (ConfigurationNode node : root.getSubnodes()) {
			if (node instanceof VariablesNode) {
				variables.putAll(((VariablesNode) node).getVariables());
			}
		}
		return replaceStackedVariables(variables);
	}

	/**
	 * Replaces all stacked variables.
	 * 
	 * @param variables source variables
	 * @return replaced variables
	 * @throws YamlParserException error during replacement
	 */
	private Map<String, String> replaceStackedVariables(Map<String, String> original) throws YamlParserException {
		Map<String, String> variables = new HashMap<>(original);
		boolean foundStackedVariable = false;
		for (Map.Entry<String, String> entry : original.entrySet()) {
			String replacement = VariableHelper.replace(entry.getValue(), variables);
			if (!replacement.equals(entry.getValue())) {
				variables.put(entry.getKey(), replacement);
				foundStackedVariable = true;
			}
		}
		if (foundStackedVariable) {
			variables = replaceStackedVariables(variables);
		}
		return variables;
	}

}
