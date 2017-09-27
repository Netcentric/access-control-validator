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
 * Node to represent a user to group assignment test.
 * 
 * @author Roland Gruber
 *
 */
public class AssignUserToGroupNode extends UserAdminTestNode {
	
	public static final String GROUP = "group";
	public static final String KEY = "assignUserToGroup";

	/**
	 * Constructor
	 */
	public AssignUserToGroupNode() {
		super();
		properties.add(new Property(GROUP));
	}
	
	/**
	 * Copy constructor.
	 * 
	 * @param node original node.
	 */
	public AssignUserToGroupNode(AssignUserToGroupNode node) {
		super();
		properties = copyProperties(node);
	}

	@Override
	protected String getKey() {
		return KEY;
	}

}
