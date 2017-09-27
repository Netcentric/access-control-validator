/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.aem.tools.acvalidator.service;

import java.util.List;

import biz.netcentric.aem.tools.acvalidator.model.AcTestSet;

/**
 * Represents a test file to run.
 * 
 * @author Roland Gruber
 */
public class TestFile {
	
	private String path;
	private List<AcTestSet> testSets;

	/**
	 * Constructor.
	 * 
	 * @param path file path
	 * @param testSets test set
	 */
	public TestFile(String path, List<AcTestSet> testSets) {
		this.path = path;
		this.testSets = testSets;
	}
	
	/**
	 * Returns the path in CRX to the test file.
	 * 
	 * @return path
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * Returns the test sets to run.
	 * 
	 * @return test sets
	 */
	public List<AcTestSet> getTestSets() {
		return testSets;
	}

	@Override
	public String toString() {
		StringBuilder out = new StringBuilder(getPath());
		if (getTestSets() != null) {
			out.append(getTestSets().toString());
		}
		return out.toString();
	}

}
