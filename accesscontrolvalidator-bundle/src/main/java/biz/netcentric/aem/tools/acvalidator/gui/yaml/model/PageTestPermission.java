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
 * Permission value for page tests.
 * 
 * @author Roland Gruber
 */
public enum PageTestPermission {

	ALLOW,
	DENY;

	/**
	 * Returns the permission type from label.
	 * 
	 * @param label label
	 * @return type
	 * @throws YamlParserException error getting type
	 */
	public static PageTestPermission fromLabel(String label) throws YamlParserException {
		try {
			return PageTestPermission.valueOf(label.toUpperCase());
		}
		catch (IllegalArgumentException|NullPointerException e) {
			throw new YamlParserException("Invalid permission type: " + label);
		}
	}
	
}
