/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.aem.tools.acvalidator.model;


/**
 * Tests that can be simulated by running e.g. a page action.
 * 
 * @author Roland Gruber
 */
public interface SimulatableTest {

	/**
	 * This test should be simulated.
	 * 
	 * @return simulate
	 */
	public boolean isSimulateSuccess();
	
}
