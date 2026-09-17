/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.aem.tools.acvalidator.gui.yaml.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.AclRestrictionTestNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.AclRestrictionsNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.ConfigurationNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.PrincipalNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.RootNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.TestsNode;

/**
 * Tests parsing of the aclrestrictions section.
 */
public class AclRestrictionsParserTest {

	private static final String PREFIX = "src/test/resources/biz/netcentric/aem/tools/acvalidator/gui.yaml/";

	@Test
	public void readFile() throws IOException, YamlParserException {
		YamlParser parser = new YamlParser();
		RootNode root = parser.parse(readFile(PREFIX + "testAclRestrictions.yaml"));

		AclRestrictionsNode restrictionsNode = getRestrictionsNode(root, 1);
		assertEquals(2, restrictionsNode.getSubnodes().size());

		AclRestrictionTestNode brand = (AclRestrictionTestNode) restrictionsNode.getSubnodes().get(0);
		checkProperties(brand, "/content/dam/brand", "jcr:read", "allow");
		assertEquals(3, brand.getRestrictions().size());
		assertEquals("*/jcr:content*", brand.getRestrictions().get("rep:glob"));
		assertEquals("nt:unstructured,dam:Asset", brand.getRestrictions().get("rep:ntNames"));
		// variables are only replaced on unroll
		assertEquals("${BRAND}", brand.getRestrictions().get("brand"));

		AclRestrictionTestNode publicNode = (AclRestrictionTestNode) restrictionsNode.getSubnodes().get(1);
		checkProperties(publicNode, "/content/dam/public", "jcr:all", "deny");
		assertTrue(publicNode.getRestrictions().isEmpty());
	}

	@Test
	public void readFileAndUnroll() throws IOException, YamlParserException {
		YamlParser parser = new YamlParser();
		RootNode root = parser.unrollAndReplaceVariables(parser.parse(readFile(PREFIX + "testAclRestrictions.yaml")));

		AclRestrictionsNode restrictionsNode = getRestrictionsNode(root, 0);
		AclRestrictionTestNode brand = (AclRestrictionTestNode) restrictionsNode.getSubnodes().get(0);
		assertEquals("Adobe", brand.getRestrictions().get("brand"));
		assertEquals("*/jcr:content*", brand.getRestrictions().get("rep:glob"));
	}

	@Test
	public void readFileUnknownProperty() throws IOException {
		assertParseFails("testAclRestrictionsUnknownProperty.yaml", "Unknown property in aclrestrictions entry: bogus");
	}

	@Test
	public void readFileMissingPath() throws IOException {
		assertParseFails("testAclRestrictionsMissingPath.yaml", "'path' is required in aclrestrictions entry");
	}

	@Test
	public void readFileInvalidRestrictions() throws IOException {
		assertParseFails("testAclRestrictionsInvalidRestrictions.yaml", "'restrictions' must be a map of key-value pairs");
	}

	private void assertParseFails(String fileName, String expectedMessage) throws IOException {
		YamlParser parser = new YamlParser();
		String content = readFile(PREFIX + fileName);
		try {
			parser.parse(content);
			fail("Expected YamlParserException for " + fileName);
		} catch (YamlParserException e) {
			assertEquals(expectedMessage, e.getMessage());
		}
	}

	private AclRestrictionsNode getRestrictionsNode(RootNode root, int testsNodeIndex) {
		ConfigurationNode testsNode = root.getSubnodes().get(testsNodeIndex);
		assertTrue(testsNode instanceof TestsNode);
		ConfigurationNode principal = testsNode.getSubnodes().get(0);
		assertTrue(principal instanceof PrincipalNode);
		assertEquals("myproject-service-user", principal.getNodeName());
		ConfigurationNode restrictionsNode = principal.getSubnodes().get(0);
		assertTrue(restrictionsNode instanceof AclRestrictionsNode);
		return (AclRestrictionsNode) restrictionsNode;
	}

	private void checkProperties(AclRestrictionTestNode node, String path, String privilege, String permission) {
		assertEquals(path, node.getProperties().get(0).getValue());
		assertEquals(privilege, node.getProperties().get(1).getValue());
		assertEquals(permission, node.getProperties().get(2).getValue());
	}

	private String readFile(String name) throws IOException {
		Path path = FileSystems.getDefault().getPath(name);
		return new String(Files.readAllBytes(path));
	}
}
