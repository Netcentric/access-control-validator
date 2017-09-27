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
 * Node to represent a user modification test.
 * 
 * @author Roland Gruber
 *
 */
public class ModifyUserNode extends UserAdminTestNode {
	
	public static final String USER_ID = "userId";	
	public static final String KEY = "modifyUser";

	/**
	 * Constructor
	 */
	public ModifyUserNode() {
		super();
		properties.add(new Property(USER_ID));
	}
	
	/**
	 * Copy constructor.
	 * 
	 * @param node original node.
	 */
	public ModifyUserNode(ModifyUserNode node) {
		super();
		properties = copyProperties(node);
	}

	@Override
	protected String getKey() {
		return KEY;
	}

}
