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
 * Node to represent a group modification test.
 * 
 * @author Roland Gruber
 *
 */
public class ModifyGroupNode extends UserAdminTestNode {
	
	public static final String GROUP_ID = "groupId";
	public static final String KEY = "modifyGroup";

	/**
	 * Constructor
	 */
	public ModifyGroupNode() {
		super();
		properties.add(new Property(GROUP_ID));
	}
	
	/**
	 * Copy constructor.
	 * 
	 * @param node original node.
	 */
	public ModifyGroupNode(ModifyGroupNode node) {
		super();
		properties = copyProperties(node);
	}

	@Override
	protected String getKey() {
		return KEY;
	}

}
