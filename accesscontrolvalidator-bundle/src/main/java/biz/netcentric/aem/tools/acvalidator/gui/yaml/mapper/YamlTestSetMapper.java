/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.aem.tools.acvalidator.gui.yaml.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.AssignUserToGroupNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.ConfigurationNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.CreateGroupNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.CreateUserNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.ModifyGroupNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.ModifyUserNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.PageTestNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.PagesNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.Property;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.RootNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.UserAdminNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.UserAdminTestNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.parser.YamlParserException;
import biz.netcentric.aem.tools.acvalidator.model.AcTestSet;
import biz.netcentric.aem.tools.acvalidator.model.authorizabletestcases.GroupCreateTest;
import biz.netcentric.aem.tools.acvalidator.model.authorizabletestcases.ModifyGroupTest;
import biz.netcentric.aem.tools.acvalidator.model.authorizabletestcases.ModifyUserTest;
import biz.netcentric.aem.tools.acvalidator.model.authorizabletestcases.UserAddToGroupTest;
import biz.netcentric.aem.tools.acvalidator.model.authorizabletestcases.UserCreateTest;
import biz.netcentric.aem.tools.acvalidator.model.pagetestcases.PageCreateTest;
import biz.netcentric.aem.tools.acvalidator.model.pagetestcases.PageDeleteTest;
import biz.netcentric.aem.tools.acvalidator.model.pagetestcases.PageModifyTest;
import biz.netcentric.aem.tools.acvalidator.model.pagetestcases.PageReadTest;
import biz.netcentric.aem.tools.acvalidator.model.pagetestcases.PageReplicateTest;
import biz.netcentric.aem.tools.acvalidator.model.permissiontestcases.AclReadTest;
import biz.netcentric.aem.tools.acvalidator.model.permissiontestcases.AclWriteTest;

/**
 * Maps the Yaml structure to a test file that can be executed later.
 * 
 * @author Roland Gruber
 *
 */
public class YamlTestSetMapper {
	
	private RootNode root;
	private boolean skipSimulation;
    private String pathToTestfile;
	/**
	 * Constructor
	 * 
	 * @param root root node
	 * @param skipSimulation skip simulation even if configured in file
	 */
	public YamlTestSetMapper(RootNode root, boolean skipSimulation, String pathToTestfile) {
		this.root = root;
		this.skipSimulation = skipSimulation;
		this.pathToTestfile = pathToTestfile;
	}
	
	/**
	 * Returns the mapped test sets.
	 * 
	 * @return test sets
	 * @throws YamlParserException error reading tests
	 */
	public List<AcTestSet> getTestSets() throws YamlParserException {
		List<AcTestSet> testSets = new ArrayList<>();
		List<ConfigurationNode> testNodes = root.getSubnodes();
		for (ConfigurationNode testNode : testNodes) {
			for (ConfigurationNode userNode : testNode.getSubnodes()) {
				String authorizable = userNode.getNodeName();
				AcTestSet testSet = new AcTestSet(authorizable, this.pathToTestfile);
				addPageAndUserTests(testSet, userNode.getSubnodes());
				testSets.add(testSet);
			}
		}
		return testSets;
	}

	/**
	 * Add the page and user tests to the provided test set.
	 * 
	 * @param testSet test set
	 * @param subnodes page and user test nodes
	 * @throws YamlParserException error reading tests
	 */
	private void addPageAndUserTests(AcTestSet testSet, List<ConfigurationNode> subnodes) throws YamlParserException {
		for (ConfigurationNode node : subnodes) {
			if (node instanceof PagesNode) {
				addPageTests(testSet, (PagesNode) node, this.pathToTestfile);
			}
			else if (node instanceof UserAdminNode) {
				addUserAdminTests(testSet, (UserAdminNode) node);
			}
		}
	}

	/**
	 * Adds page tests.
	 * 
	 * @param testSet test set
	 * @param node page test node
	 * @throws YamlParserException error reading test
	 */
	private void addPageTests(AcTestSet testSet, PagesNode node, String pathToTestfile) throws YamlParserException {
		for (ConfigurationNode subnode : node.getSubnodes()) {
			PageTestNode pageNode = (PageTestNode) subnode;
			
			if (pageNode.isPageCreateTest()) {
				boolean simulate = !skipSimulation && pageNode.isSimulate();
				if(simulate){
					if(StringUtils.isBlank(pageNode.getTemplate())){
						throw new YamlParserException("testfile: " + this.pathToTestfile + ", property: '" + PageTestNode.TEMPLATE + "' in PageCreate test for authorizable: " + testSet.getAuthorizableID()+ " cannot be blank, if simulate is set to true!");
					}
				}
				PageCreateTest test = new PageCreateTest(pageNode.getPath(), pageNode.isAllow(), simulate, pageNode.getTemplate());
				testSet.addAcTestCase(test);
			}
			if (pageNode.isPageReadTest()) {
				boolean simulate = !skipSimulation && pageNode.isSimulate();
				PageReadTest test = new PageReadTest(pageNode.getPath(), pageNode.isAllow(), simulate);
				testSet.addAcTestCase(test);
			}
			if (pageNode.isPageDeleteTest()) {
				boolean simulate = !skipSimulation && pageNode.isSimulate();
				PageDeleteTest test = new PageDeleteTest(pageNode.getPath(), pageNode.isAllow(), simulate);
				testSet.addAcTestCase(test);
			}
			if(pageNode.isPageModifyTest()){
				boolean simulate = !skipSimulation && pageNode.isSimulate();
				Map<String, Property> propertiesMap = pageNode.getPropertiesMap();
				Property modifyProperties = propertiesMap.get(PageTestNode.PROPERTY_NAMES_MODIFY);
				Set<String> propertiesToModify = new HashSet<>();
				if(simulate){
					if(StringUtils.isBlank(modifyProperties.getValue())){
						throw new YamlParserException("testfile: " + this.pathToTestfile + ", property: '" + PageTestNode.PROPERTY_NAMES_MODIFY + "' in PageModify test for authorizable: " + testSet.getAuthorizableID()+ " cannot be blank, if simulate is set to true!");
				}
					propertiesToModify = new HashSet<String>(Arrays.asList(modifyProperties.getValue().trim().split("\\s*,\\s*")));
				}
				
				PageModifyTest test = new PageModifyTest(pageNode.getPath(), pageNode.isAllow(), simulate, propertiesToModify);
				testSet.addAcTestCase(test);
			}
			if(pageNode.isPagePublishTest()){
				boolean simulate = !skipSimulation && pageNode.isSimulate();
				PageReplicateTest test = new PageReplicateTest(pageNode.getPath(), pageNode.isAllow(), simulate, pageNode.isDeactivate());
				testSet.addAcTestCase(test);
			}
			if(pageNode.isAclReadTest()){
				boolean simulate = !skipSimulation && pageNode.isSimulate();
				AclReadTest test = new AclReadTest(pageNode.getPath(), pageNode.isAllow(), simulate);
				testSet.addAcTestCase(test);
			}
			if(pageNode.isAclWriteTest()){
				boolean simulate = !skipSimulation && pageNode.isSimulate();
				AclWriteTest test = new AclWriteTest(pageNode.getPath(), pageNode.isAllow(), simulate);
				testSet.addAcTestCase(test);
			}
		}
	}

	/**
	 * Adds user admin tests.
	 * 
	 * @param testSet test set
	 * @param node user admin test node
	 * @throws YamlParserException 
	 */
	private void addUserAdminTests(AcTestSet testSet, UserAdminNode node) throws YamlParserException {
		for (ConfigurationNode subnode : node.getSubnodes()) {
			
			UserAdminTestNode userAdminTestNode = (UserAdminTestNode) subnode;
			
			if(userAdminTestNode.isGroupCreateTest()){
				CreateGroupNode createGroupNode = (CreateGroupNode)userAdminTestNode;
				GroupCreateTest groupCreateTest = new GroupCreateTest(createGroupNode.getPropertyByName("path").getValue(), createGroupNode.isAllow());
				testSet.addAcTestCase(groupCreateTest);
			}
			if(userAdminTestNode.isUserCreateTest()){
				CreateUserNode createUserNode =  (CreateUserNode)userAdminTestNode;
				UserCreateTest userCreateTest = new UserCreateTest(createUserNode.getPropertyByName("path").getValue(), createUserNode.isAllow());
				testSet.addAcTestCase(userCreateTest);
			}
			if(userAdminTestNode.isAssignUserToGroupTest()){
				AssignUserToGroupNode assignUserToGroupNode = (AssignUserToGroupNode)userAdminTestNode;
				UserAddToGroupTest userAddToGroupTest = new UserAddToGroupTest(assignUserToGroupNode.getPropertyByName("group").getValue(), assignUserToGroupNode.isAllow());
				testSet.addAcTestCase(userAddToGroupTest);
			}
			if(userAdminTestNode.isModifyGroupTest()){
				ModifyGroupNode modifyGroupNode = (ModifyGroupNode)userAdminTestNode;
				ModifyGroupTest userAddToGroupTest = new ModifyGroupTest(modifyGroupNode.getPropertyByName(ModifyGroupNode.GROUP_ID).getValue(), modifyGroupNode.isAllow());
				testSet.addAcTestCase(userAddToGroupTest);
			}
			if(userAdminTestNode.isModifyUserTest()){
				ModifyUserNode modifyGroupNode = (ModifyUserNode)userAdminTestNode;
				ModifyUserTest userAddToGroupTest = new ModifyUserTest(modifyGroupNode.getPropertyByName(ModifyUserNode.USER_ID).getValue(), modifyGroupNode.isAllow());
				testSet.addAcTestCase(userAddToGroupTest);
			}
		}
	}
}
