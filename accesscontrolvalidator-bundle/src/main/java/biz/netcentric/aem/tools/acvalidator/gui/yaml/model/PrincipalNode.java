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
 * Node to store principals.
 * 
 * @author Roland Gruber
 */
public class PrincipalNode extends ConfigurationNode {

	private String principal;

	/**
	 * Constructor
	 * 
	 * @param principal principal name
	 */
	public PrincipalNode(String principal) {
		this.principal = principal;
	}

	@Override
	public List<Class> getAllowedSubnodeClasses() {
		return Arrays.asList(new Class[] {PagesNode.class, UserAdminNode.class});
	}

	@Override
	public List<Property> getProperties() {
		return new ArrayList<>();
	}

	@Override
	public boolean allowsCustomProperties() {
		return true;
	}

	@Override
	public String getNodeName() {
		return principal;
	}

	/**
	 * Adds the nodes from the Yaml.
	 * 
	 * @param subnodes subnodes
	 * @throws YamlParserException error while parsing
	 */
	public void addNodesFromYaml(List<LinkedHashMap> subnodes) throws YamlParserException {
		for (LinkedHashMap subnode : subnodes) {
			for (Object identifier : subnode.keySet()) {
				if (PagesNode.NAME.equals(identifier)) {
					addPagesNode((List<LinkedHashMap>) subnode.get(PagesNode.NAME));
				}
				else if (UserAdminNode.NAME.equals(identifier)) {
					addUseradminNode((List<LinkedHashMap>) subnode.get(UserAdminNode.NAME));
				}
				else {
					throw new YamlParserException("Unknown subnode for principals: " + identifier.toString());
				}
			}
		}
	}

	/**
	 * Adds a pages node.
	 * 
	 * @param subnodes subnodes
	 * @throws YamlParserException error while parsing
	 */
	private void addPagesNode(List<LinkedHashMap> subnodes) throws YamlParserException {
		PagesNode pagesNode = new PagesNode();
		pagesNode.addNodesFromYaml(subnodes);
		addSubnode(pagesNode);
	}

	/**
	 * Adds a userAdmin node.
	 * 
	 * @param subnodes subnodes
	 * @throws YamlParserException error while parsing
	 */
	private void addUseradminNode(List<LinkedHashMap> subnodes) throws YamlParserException {
		UserAdminNode useradminNode = new UserAdminNode();
		useradminNode.addNodesFromYaml(subnodes);
		addSubnode(useradminNode);
	}

}
