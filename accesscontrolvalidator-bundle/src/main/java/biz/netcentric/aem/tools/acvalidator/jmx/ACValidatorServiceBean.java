/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.aem.tools.acvalidator.jmx;

import com.adobe.granite.jmx.annotation.Description;
import com.adobe.granite.jmx.annotation.Name;

/**
 * JMX service for AC Validator.
 * 
 * @author Roland Gruber
 */
@Description("AC Validator")
public interface ACValidatorServiceBean {

    @Description("Version")
    String getVersion();
	
    @Description("Runs the test file(s)")
    String runTests(
            @Name("path") @Description("Path where test file(s) are located. Can be a file or folder.") String folder);

}
