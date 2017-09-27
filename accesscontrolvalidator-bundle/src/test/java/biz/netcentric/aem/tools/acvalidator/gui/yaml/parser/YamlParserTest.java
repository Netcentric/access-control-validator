package biz.netcentric.aem.tools.acvalidator.gui.yaml.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.AssignUserToGroupNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.ConfigurationNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.CreateGroupNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.CreateUserNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.ForLoopNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.ModifyGroupNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.ModifyUserNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.PageTestNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.PagesNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.PrincipalNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.Property;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.RootNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.TestsNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.UserAdminNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.UserAdminTestNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.VariablesNode;

public class YamlParserTest {
	
	private static final String PREFIX = "src/test/resources/biz/netcentric/aem/tools/acvalidator/gui.yaml/";
	
	@Test
	public void readFile() throws IOException, YamlParserException {
		YamlParser parser = new YamlParser();
		RootNode root = parser.parse(readFile(PREFIX + "test.yaml"));
		checkRootNodes(root);		
	}
	
	protected void checkRootNodes(RootNode root) {
		assertEquals(3, root.getSubnodes().size());
		ConfigurationNode varNode = root.getSubnodes().get(0);
		assertTrue(varNode instanceof VariablesNode);
		checkVariables(varNode);
		ConfigurationNode varNode2 = root.getSubnodes().get(1);
		assertTrue(varNode2 instanceof VariablesNode);
		checkVariables2(varNode2);
		ConfigurationNode testNode = root.getSubnodes().get(2);
		assertTrue(testNode instanceof TestsNode);
		checkTests(testNode);
	}

	private void checkVariables(ConfigurationNode varNode) {
		assertEquals(2, varNode.getProperties().size());
		Property damProperty = varNode.getProperties().get(0);
		assertEquals("DAM", damProperty.getName());
		assertEquals("/content/dam", damProperty.getValue());
		Property reportProperty = varNode.getProperties().get(1);
		assertEquals("REPORTS", reportProperty.getName());
		assertEquals("/etc/reports", reportProperty.getValue());
	}

	private void checkVariables2(ConfigurationNode varNode) {
		assertEquals(2, varNode.getProperties().size());
		Property damP1Property = varNode.getProperties().get(0);
		assertEquals("DAM_P1", damP1Property.getName());
		assertEquals("${DAM}/p1", damP1Property.getValue());
		Property damXProperty = varNode.getProperties().get(1);
		assertEquals("DAM_X", damXProperty.getName());
		assertEquals("${DAM_P1}/x", damXProperty.getValue());
	}

	private void checkTests(ConfigurationNode testNode) {
		assertEquals(1, testNode.getSubnodes().size());
		ConfigurationNode principalNode = testNode.getSubnodes().get(0);
		assertTrue(principalNode instanceof PrincipalNode);
		checkPrincipal(principalNode);
	}

	private void checkPrincipal(ConfigurationNode principalNode) {
		assertEquals("myuser", principalNode.getNodeName());
		assertEquals(2, principalNode.getSubnodes().size());
		ConfigurationNode pagesNode = principalNode.getSubnodes().get(0);
		assertTrue(pagesNode instanceof PagesNode);
		checkPagesNode(pagesNode);
		ConfigurationNode useradminNode = principalNode.getSubnodes().get(1);
		assertTrue(useradminNode instanceof UserAdminNode);
		checkUseradminNode(useradminNode);
	}

	private void checkPagesNode(ConfigurationNode pagesNode) {
		assertEquals(5, pagesNode.getSubnodes().size());
		ConfigurationNode testNode1 = pagesNode.getSubnodes().get(0);
		assertTrue(testNode1 instanceof PageTestNode);
		checkProperties(testNode1.getProperties(), PageTestNode.PATH, "/content", PageTestNode.ACTIONS, "read, write, modify, publish", PageTestNode.PERMISSION, "allow", PageTestNode.TEMPLATE, null, PageTestNode.PROPERTY_NAMES_MODIFY, null, PageTestNode.DEACTIVATE_AFTER_PUBLISH, null, PageTestNode.SIMULATE, null, PageTestNode.DEACTIVATE, null);
		ConfigurationNode testNode2 = pagesNode.getSubnodes().get(1);
		assertTrue(testNode2 instanceof PageTestNode);
		checkProperties(testNode2.getProperties(), PageTestNode.PATH, "${DAM}", PageTestNode.ACTIONS, "read", PageTestNode.PERMISSION, "allow", PageTestNode.TEMPLATE, null, PageTestNode.PROPERTY_NAMES_MODIFY, null, PageTestNode.DEACTIVATE_AFTER_PUBLISH, null, PageTestNode.SIMULATE, null, PageTestNode.DEACTIVATE, null);
		ConfigurationNode testNode3 = pagesNode.getSubnodes().get(2);
		assertTrue(testNode3 instanceof PageTestNode);
		checkProperties(testNode3.getProperties(), PageTestNode.PATH, "${REPORTS}", PageTestNode.ACTIONS, "read", PageTestNode.PERMISSION, "deny", PageTestNode.TEMPLATE, null, PageTestNode.PROPERTY_NAMES_MODIFY, null, PageTestNode.DEACTIVATE_AFTER_PUBLISH, null, PageTestNode.SIMULATE, null, PageTestNode.DEACTIVATE, null);
		ConfigurationNode testNode4 = pagesNode.getSubnodes().get(3);
		assertTrue(testNode4 instanceof PageTestNode);
		checkProperties(testNode4.getProperties(), PageTestNode.PATH, "${DAM_P1}", PageTestNode.ACTIONS, "readACL, writeACL", PageTestNode.PERMISSION, "allow", PageTestNode.TEMPLATE, null, PageTestNode.PROPERTY_NAMES_MODIFY, null, PageTestNode.DEACTIVATE_AFTER_PUBLISH, null, PageTestNode.SIMULATE, null, PageTestNode.DEACTIVATE, null);
		ConfigurationNode testNode5 = pagesNode.getSubnodes().get(4);
		assertTrue(testNode5 instanceof ForLoopNode);
		checkPageForLoopNode(testNode5, "FOR brand IN [ BRAND1, BRAND2, BRAND3 ]");
	}

	private void checkPageForLoopNode(ConfigurationNode forLoopNode, String nodeName) {
		assertEquals(nodeName, forLoopNode.getNodeName());
		assertEquals(6, forLoopNode.getSubnodes().size());
		ConfigurationNode testNode1 = forLoopNode.getSubnodes().get(0);
		assertTrue(testNode1 instanceof PageTestNode);
		checkProperties(testNode1.getProperties(), PageTestNode.PATH, "/content/${brand}", PageTestNode.ACTIONS, "create, modify", PageTestNode.PERMISSION, "allow", PageTestNode.TEMPLATE, "/apps/myapp/templates/page", PageTestNode.PROPERTY_NAMES_MODIFY, "jcr:title, summary", PageTestNode.DEACTIVATE_AFTER_PUBLISH, null, PageTestNode.SIMULATE, "true", PageTestNode.DEACTIVATE, null);
		ConfigurationNode testNode2 = forLoopNode.getSubnodes().get(1);
		assertTrue(testNode2 instanceof PageTestNode);
		checkProperties(testNode2.getProperties(), PageTestNode.PATH, "/content/${brand}/protected", PageTestNode.ACTIONS, "read", PageTestNode.PERMISSION, "deny", PageTestNode.TEMPLATE, null, PageTestNode.PROPERTY_NAMES_MODIFY, null, PageTestNode.DEACTIVATE_AFTER_PUBLISH, null, PageTestNode.SIMULATE, null, PageTestNode.DEACTIVATE, null);

		ConfigurationNode testNode3 = forLoopNode.getSubnodes().get(2);
		assertTrue(testNode3 instanceof PageTestNode);
		checkProperties(testNode3.getProperties(), PageTestNode.PATH, "/content/${brand}/en", PageTestNode.ACTIONS, "delete", PageTestNode.PERMISSION, "deny", PageTestNode.TEMPLATE, null, PageTestNode.PROPERTY_NAMES_MODIFY, null, PageTestNode.DEACTIVATE_AFTER_PUBLISH, null, PageTestNode.SIMULATE, null, PageTestNode.DEACTIVATE, null);
		ConfigurationNode testNode4 = forLoopNode.getSubnodes().get(3);
		assertTrue(testNode4 instanceof PageTestNode);
		checkProperties(testNode4.getProperties(), PageTestNode.PATH, "/content/${brand}/en/*", PageTestNode.ACTIONS, "delete", PageTestNode.PERMISSION, "allow", PageTestNode.TEMPLATE, null, PageTestNode.PROPERTY_NAMES_MODIFY, null, PageTestNode.DEACTIVATE_AFTER_PUBLISH, null, PageTestNode.SIMULATE, null, PageTestNode.DEACTIVATE, null);
		ConfigurationNode testNode5 = forLoopNode.getSubnodes().get(4);
		assertTrue(testNode5 instanceof PageTestNode);
		checkProperties(testNode5.getProperties(), PageTestNode.PATH, "/content/${brand}/en/*", PageTestNode.ACTIONS, "publish", PageTestNode.PERMISSION, "allow", PageTestNode.TEMPLATE, null, PageTestNode.PROPERTY_NAMES_MODIFY, null, PageTestNode.DEACTIVATE_AFTER_PUBLISH, null, PageTestNode.SIMULATE, "true", PageTestNode.DEACTIVATE, "false");
		ConfigurationNode testNode6 = forLoopNode.getSubnodes().get(5);
		assertTrue(testNode6 instanceof PageTestNode);
		checkProperties(testNode6.getProperties(), PageTestNode.PATH, "/content/dam/${brand}", PageTestNode.ACTIONS, "create, modify, delete, publish, rollOut", PageTestNode.PERMISSION, "allow", PageTestNode.TEMPLATE, null, PageTestNode.PROPERTY_NAMES_MODIFY, null, PageTestNode.DEACTIVATE_AFTER_PUBLISH, null, PageTestNode.SIMULATE, "true", PageTestNode.DEACTIVATE, null);
	}

	private void checkUseradminNode(ConfigurationNode useradminNode) {
		assertEquals(7, useradminNode.getSubnodes().size());

		ConfigurationNode testNode1 = useradminNode.getSubnodes().get(0);
		assertTrue(testNode1 instanceof ForLoopNode);
		checkUserForLoopNode(testNode1, "FOR group IN [ readers, writers ]");

		ConfigurationNode testNode2 = useradminNode.getSubnodes().get(1);
		assertTrue(testNode2 instanceof AssignUserToGroupNode);
		checkProperties(testNode2.getProperties(), UserAdminTestNode.ACTION, AssignUserToGroupNode.KEY, AssignUserToGroupNode.GROUP, "admin", UserAdminTestNode.PERMISSION, "deny", UserAdminTestNode.SIMULATE, "true");

		ConfigurationNode testNode3 = useradminNode.getSubnodes().get(2);
		assertTrue(testNode3 instanceof CreateUserNode);
		checkProperties(testNode3.getProperties(), UserAdminTestNode.ACTION, CreateUserNode.KEY, CreateUserNode.PATH, "myusers", UserAdminTestNode.PERMISSION, "allow", UserAdminTestNode.SIMULATE, "true");

		ConfigurationNode testNode4 = useradminNode.getSubnodes().get(3);
		assertTrue(testNode4 instanceof ModifyUserNode);
		checkProperties(testNode4.getProperties(), UserAdminTestNode.ACTION, ModifyUserNode.KEY, ModifyUserNode.USER_ID, "a", UserAdminTestNode.PERMISSION, "deny", UserAdminTestNode.SIMULATE, "true");

		ConfigurationNode testNode5 = useradminNode.getSubnodes().get(4);
		assertTrue(testNode5 instanceof ModifyGroupNode);
		checkProperties(testNode5.getProperties(), UserAdminTestNode.ACTION, ModifyGroupNode.KEY, ModifyGroupNode.GROUP_ID, "a", UserAdminTestNode.PERMISSION, "allow", UserAdminTestNode.SIMULATE, "true");

		ConfigurationNode testNode6 = useradminNode.getSubnodes().get(5);
		assertTrue(testNode6 instanceof CreateGroupNode);
		checkProperties(testNode6.getProperties(), UserAdminTestNode.ACTION, CreateGroupNode.KEY, UserAdminTestNode.PERMISSION, "allow", CreateGroupNode.PATH, "mygroups", UserAdminTestNode.SIMULATE, "true");

		ConfigurationNode testNode7 = useradminNode.getSubnodes().get(6);
		assertTrue(testNode7 instanceof CreateGroupNode);
		checkProperties(testNode7.getProperties(), UserAdminTestNode.ACTION, CreateGroupNode.KEY, CreateGroupNode.PATH, "a", UserAdminTestNode.PERMISSION, "deny", UserAdminTestNode.SIMULATE, null);
	}

	private void checkUserForLoopNode(ConfigurationNode forLoopNode, String nodeName) {
		assertEquals(nodeName, forLoopNode.getNodeName());
		assertEquals(5, forLoopNode.getSubnodes().size());

		ConfigurationNode testNode1 = forLoopNode.getSubnodes().get(0);
		assertTrue(testNode1 instanceof AssignUserToGroupNode);
		checkProperties(testNode1.getProperties(), UserAdminTestNode.ACTION, AssignUserToGroupNode.KEY, AssignUserToGroupNode.GROUP, "${group}", UserAdminTestNode.PERMISSION, "allow", UserAdminTestNode.SIMULATE, "true");

		ConfigurationNode testNode2 = forLoopNode.getSubnodes().get(1);
		assertTrue(testNode2 instanceof CreateUserNode);
		checkProperties(testNode2.getProperties(), UserAdminTestNode.ACTION, CreateUserNode.KEY, UserAdminTestNode.PERMISSION, "allow", CreateUserNode.PATH, "myusers", UserAdminTestNode.SIMULATE, null);

		ConfigurationNode testNode3 = forLoopNode.getSubnodes().get(2);
		assertTrue(testNode3 instanceof ModifyUserNode);
		checkProperties(testNode3.getProperties(), UserAdminTestNode.ACTION, ModifyUserNode.KEY, ModifyUserNode.USER_ID, "a", UserAdminTestNode.PERMISSION, "deny", UserAdminTestNode.SIMULATE, null);

		ConfigurationNode testNode4 = forLoopNode.getSubnodes().get(3);
		assertTrue(testNode4 instanceof ModifyGroupNode);
		checkProperties(testNode4.getProperties(), UserAdminTestNode.ACTION, ModifyGroupNode.KEY, UserAdminTestNode.PERMISSION, "allow", ModifyGroupNode.GROUP_ID, "a", UserAdminTestNode.SIMULATE, null);

		ConfigurationNode testNode5 = forLoopNode.getSubnodes().get(4);
		assertTrue(testNode5 instanceof CreateGroupNode);
		checkProperties(testNode5.getProperties(), UserAdminTestNode.PERMISSION, "allow", UserAdminTestNode.ACTION, CreateGroupNode.KEY, CreateGroupNode.PATH, "mygroups", UserAdminTestNode.SIMULATE, null);
	}

	private void checkProperties(List<Property> properties, String... expected) {
		assertEquals(expected.length / 2, properties.size());
		for (int i = 0; i < expected.length / 2; i++) {
			String key = expected[2 * i];
			String value = expected[2* i + 1];
			assertEquals(key, properties.get(i).getName());
			assertEquals(value, properties.get(i).getValue());
		}
	}

	@Test(expected=YamlParserException.class)
	public void readFileInvalidRootNode() throws IOException, YamlParserException {
		YamlParser parser = new YamlParser();
		parser.parse(readFile(PREFIX + "testInvalidRoot.yaml"));
	}
	
	@Test
	public void getVariables() throws YamlParserException, IOException {
		YamlParser parser = new YamlParser();
		RootNode node = parser.parse(readFile(PREFIX + "test.yaml"));
		Map<String, String> variables = parser.getVariables(node);
		assertEquals(4, variables.size());
		assertEquals("/content/dam", variables.get("DAM"));
		assertEquals("/etc/reports", variables.get("REPORTS"));
		assertEquals("/content/dam/p1", variables.get("DAM_P1"));
		assertEquals("/content/dam/p1/x", variables.get("DAM_X"));
	}
	
	@Test
	public void readFileAndUnroll() throws IOException, YamlParserException {
		YamlParser parser = new YamlParser();

		RootNode root = parser.unrollAndReplaceVariables(parser.parse(readFile(PREFIX + "test.yaml")));
		
		assertEquals(1, root.getSubnodes().size());
		ConfigurationNode testNode = root.getSubnodes().get(0);
		assertTrue(testNode instanceof TestsNode);
		ConfigurationNode userNode = testNode.getSubnodes().get(0);
		ConfigurationNode pagesNode = userNode.getSubnodes().get(0);
		assertEquals(22, pagesNode.getSubnodes().size());
		ConfigurationNode damNode = pagesNode.getSubnodes().get(1);
		checkProperties(damNode.getProperties(), PageTestNode.PATH, "/content/dam", PageTestNode.ACTIONS, "read", PageTestNode.PERMISSION, "allow", PageTestNode.TEMPLATE, null, PageTestNode.PROPERTY_NAMES_MODIFY, null, PageTestNode.DEACTIVATE_AFTER_PUBLISH, null, PageTestNode.SIMULATE, null, PageTestNode.DEACTIVATE, null);
		ConfigurationNode pathNode1 = pagesNode.getSubnodes().get(4);
		checkProperties(pathNode1.getProperties(), PageTestNode.PATH, "/content/BRAND1", PageTestNode.ACTIONS, "create, modify", PageTestNode.PERMISSION, "allow", PageTestNode.TEMPLATE, "/apps/myapp/templates/page", PageTestNode.PROPERTY_NAMES_MODIFY, "jcr:title, summary", PageTestNode.DEACTIVATE_AFTER_PUBLISH, null, PageTestNode.SIMULATE, "true", PageTestNode.DEACTIVATE, null);
		ConfigurationNode pathNode2 = pagesNode.getSubnodes().get(10);
		checkProperties(pathNode2.getProperties(), PageTestNode.PATH, "/content/BRAND2", PageTestNode.ACTIONS, "create, modify", PageTestNode.PERMISSION, "allow", PageTestNode.TEMPLATE, "/apps/myapp/templates/page", PageTestNode.PROPERTY_NAMES_MODIFY, "jcr:title, summary", PageTestNode.DEACTIVATE_AFTER_PUBLISH, null, PageTestNode.SIMULATE, "true", PageTestNode.DEACTIVATE, null);
	}

	protected String readFile(String fileName) throws IOException {
		Path path = FileSystems.getDefault().getPath(fileName);
		return new String(Files.readAllBytes(path));
	}

}
