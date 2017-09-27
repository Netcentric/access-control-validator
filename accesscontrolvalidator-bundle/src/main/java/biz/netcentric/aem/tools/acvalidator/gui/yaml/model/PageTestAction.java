/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.aem.tools.acvalidator.gui.yaml.model;

import biz.netcentric.aem.tools.acvalidator.gui.yaml.parser.YamlParserException;

/**
 * List of possible page test actions.
 * 
 * @author Roland Gruber
 */
public enum PageTestAction {

	READ("read"),
	WRITE("write"),
	CREATE("create"),
	MODIFY("modify"),
	DELETE("delete"),
	PUBLISH("publish"),
	ROLLOUT("rollOut"),
	READ_ACL("readACL"), 
	WRITE_ACL("writeACL");
	
	private String label;

	/**
	 * Constructor
	 * 
	 * @param label label
	 */
	private PageTestAction(String label) {
		this.label = label;
	}

	/**
	 * Returns the label.
	 * 
	 * @return label
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * Gets the action by checking the label.
	 * 
	 * @param label label
	 * @return action
	 * @throws YamlParserException invalid label
	 */
	public static PageTestAction fromLabel(String label) throws YamlParserException {
		for (PageTestAction action : values()) {
			if (action.label.equals(label)) {
				return action;
			}
		}
		throw new YamlParserException("Invalid page test action: " + label);
	}
	
}
