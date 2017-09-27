/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.aem.tools.acvalidator.gui.yaml.parser;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Replaces variables in strings.
 * 
 * @author Roland Gruber
 */
public class VariableHelper {
	
	private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{([a-zA-Z0-9_-]+)\\}");

	/**
	 * Replaces variables in the given input string.
	 * 
	 * @param original original string
	 * @param variables variables
	 * @return replacement
	 * @throws YamlParserException error during replacement
	 */
	public static String replace(String original, Map<String, String> variables) throws YamlParserException {
		if (original == null) {
			return null;
		}
		Matcher matcher = VARIABLE_PATTERN.matcher(original);
		boolean match = false;
		String replacedString = original;
		for(String key : variables.keySet()){
			match = true;
//			String key = matcher.group(1);
			if (!variables.containsKey(key)) {
				throw new YamlParserException("Unable to replace variable " + key + " in " + original);
			}
//			replacedString = replacedString.replaceAll("\\$\\{"+key+"\\}", variables.get(key));
			replacedString = replacedString.replaceAll(Pattern.quote("$")+Pattern.quote("{") + key +Pattern.quote("}"), variables.get(key));
		}
		if(match){
			return replacedString;
		}
		return original;
	}
	
}
