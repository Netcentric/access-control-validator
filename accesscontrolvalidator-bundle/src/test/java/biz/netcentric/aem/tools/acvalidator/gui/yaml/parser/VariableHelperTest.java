package biz.netcentric.aem.tools.acvalidator.gui.yaml.parser;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class VariableHelperTest {
	
	@Test
	public void replaceTest() throws YamlParserException{
		String original = "${PAGEROOT}/${page}";
		Map<String, String> variables = new HashMap<>();
		variables.put("PAGEROOT", "/content/we-retail/us/en");
		variables.put("page", "men");

		String replace = VariableHelper.replace(original, variables);
		System.out.println(replace);
	}

}
