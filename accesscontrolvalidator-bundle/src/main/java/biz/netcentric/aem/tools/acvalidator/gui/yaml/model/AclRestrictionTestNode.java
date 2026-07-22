/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.aem.tools.acvalidator.gui.yaml.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import biz.netcentric.aem.tools.acvalidator.gui.yaml.parser.VariableHelper;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.parser.YamlParserException;

/**
 * Node to store a single ACL restriction test entry.
 *
 * YAML format:
 * <pre>
 * - path: /content/dam/brand
 *   privilege: jcr:read
 *   permission: allow
 *   restrictions:
 *     rep:glob: "&#42;/jcr:content&#42;"
 *     dc:format: image/jpeg
 * </pre>
 */
public class AclRestrictionTestNode extends ConfigurationNode {

    public static final String PATH = "path";
    public static final String PRIVILEGE = "privilege";
    public static final String PERMISSION = "permission";
    public static final String RESTRICTIONS = "restrictions";

    private Map<String, Property> properties = new LinkedHashMap<>();
    private Map<String, String> restrictions = new LinkedHashMap<>();

    public AclRestrictionTestNode() {
        properties.put(PATH, new Property(PATH));
        properties.put(PRIVILEGE, new Property(PRIVILEGE));
        properties.put(PERMISSION, new Property(PERMISSION));
    }

    @SuppressWarnings("unchecked")
    public void setPropertiesFromYaml(LinkedHashMap rawProperties) throws YamlParserException {
        for (Object key : rawProperties.keySet()) {
            String keyStr = (String) key;
            Object value = rawProperties.get(key);
            if (RESTRICTIONS.equals(keyStr)) {
                if (!(value instanceof Map)) {
                    throw new YamlParserException("'restrictions' must be a map of key-value pairs");
                }
                for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                    restrictions.put(entry.getKey().toString(), entry.getValue().toString());
                }
            } else if (properties.containsKey(keyStr)) {
                properties.get(keyStr).setValue(value.toString());
            } else {
                throw new YamlParserException("Unknown property in aclrestrictions entry: " + keyStr);
            }
        }
        if (properties.get(PATH).getValue() == null) {
            throw new YamlParserException("'path' is required in aclrestrictions entry");
        }
        if (properties.get(PRIVILEGE).getValue() == null) {
            throw new YamlParserException("'privilege' is required in aclrestrictions entry");
        }
        if (properties.get(PERMISSION).getValue() == null) {
            throw new YamlParserException("'permission' is required in aclrestrictions entry");
        }
    }

    @Override
    public List<ConfigurationNode> unroll(Map<String, String> variables) throws YamlParserException {
        List<ConfigurationNode> result = super.unroll(variables);
        Map<String, String> replaced = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : restrictions.entrySet()) {
            replaced.put(entry.getKey(), VariableHelper.replace(entry.getValue(), variables));
        }
        restrictions = replaced;
        return result;
    }

    @Override
    public List<Class> getAllowedSubnodeClasses() {
        return new ArrayList<>();
    }

    @Override
    public List<Property> getProperties() {
        return new ArrayList<>(properties.values());
    }

    @Override
    public String getNodeName() {
        return null;
    }

    public String getPath() {
        return properties.get(PATH).getValue();
    }

    public String getPrivilege() {
        return properties.get(PRIVILEGE).getValue();
    }

    public boolean isAllow() throws YamlParserException {
        return PageTestPermission.fromLabel(properties.get(PERMISSION).getValue()).equals(PageTestPermission.ALLOW);
    }

    public Map<String, String> getRestrictions() {
        return restrictions;
    }

}
