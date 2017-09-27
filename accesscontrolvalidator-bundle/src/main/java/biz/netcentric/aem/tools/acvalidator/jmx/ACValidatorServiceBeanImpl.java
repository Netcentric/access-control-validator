/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.aem.tools.acvalidator.jmx;

import javax.management.NotCompliantMBeanException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

import com.adobe.granite.jmx.annotation.AnnotatedStandardMBean;

import biz.netcentric.aem.tools.acvalidator.api.ACValidatorException;
import biz.netcentric.aem.tools.acvalidator.api.ACValidatorService;
import biz.netcentric.aem.tools.acvalidator.serviceuser.ServiceResourceResolverService;

/**
 * JMX service.
 * 
 * @author Roland Gruber
 */
@Service
@Component(immediate = true)
@Properties({
	@Property(name = "jmx.objectname", value = "biz.netcentric.aem.tools:type=ACValidator"),
	@Property(name = "pattern", value = "/.*") })
public class ACValidatorServiceBeanImpl extends AnnotatedStandardMBean implements ACValidatorServiceBean {

	@Reference
	ACValidatorService acValidatorService;

	@Reference
	ServiceResourceResolverService serviceResourceResolverService;

	/**
	 * Constructor
	 * 
	 * @throws NotCompliantMBeanException error creating MBean
	 */
	public ACValidatorServiceBeanImpl() throws NotCompliantMBeanException {
		super(ACValidatorServiceBean.class);
	}

	@Override
	public String getVersion() {
		return acValidatorService.getVersion();
	}

	@Override
	public String runTests(String path) {
		try {
			return acValidatorService.runTests(path, false).toString();
		} catch (ACValidatorException e) {
			return e.getMessage();
		}
	}

}
