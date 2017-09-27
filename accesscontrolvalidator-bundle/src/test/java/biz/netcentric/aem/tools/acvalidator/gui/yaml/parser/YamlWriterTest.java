package biz.netcentric.aem.tools.acvalidator.gui.yaml.parser;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.RootNode;

/**
 * Tests the Yaml writer.
 * 
 * @author Roland Gruber
 */
public class YamlWriterTest extends YamlParserTest {

	private static final String PREFIX = "src/test/resources/biz/netcentric/aem/tools/acvalidator/gui.yaml/";

	@Test
	public void testParseAndWrite() throws YamlParserException, IOException {
		YamlParser parser = new YamlParser();
		String input = readFile(PREFIX + "test.yaml");
		RootNode root = parser.parse(input);
		
		checkRootNodes(root);
		
		YamlWriter writer = new YamlWriter();
		String output = writer.getYaml(root);
		
		assertEquals(input, output);
	}
	
}
