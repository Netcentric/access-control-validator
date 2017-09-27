/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.aem.tools.acvalidator.gui.yaml.model;

import java.io.Serializable;

/**
 * Configuration property.
 * 
 * @author Roland Gruber
 */
public class Property implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String value;

	/**
	 * Constructor
	 * 
	 * @param name property name
	 * @param value property value
	 */
	public Property(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	/**
	 * Constructor
	 * 
	 * @param name property name
	 */
	public Property(String name) {
		this.name = name;
	}

	/**
	 * Returns the value.
	 * 
	 * @return value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 * 
	 * @param value value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Returns the name.
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name + ": " + value;
	}
	
}
