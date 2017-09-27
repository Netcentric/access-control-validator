/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.aem.tools.acvalidator.gui.yaml.model;

/**
 * Node to represent a user creation test.
 * 
 * @author Roland Gruber
 *
 */
public class CreateUserNode extends UserAdminTestNode {
	
	public static final String PATH = "path";
	public static final String KEY = "createUser";

	/**
	 * Constructor
	 */
	public CreateUserNode() {
		super();
		properties.add(new Property(PATH));
	}
	
	/**
	 * Copy constructor.
	 * 
	 * @param node original node.
	 */
	public CreateUserNode(CreateUserNode node) {
		super();
		properties = copyProperties(node);
	}

	@Override
	protected String getKey() {
		return KEY;
	}

}
