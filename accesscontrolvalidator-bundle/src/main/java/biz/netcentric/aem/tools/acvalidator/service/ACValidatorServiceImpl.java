/*
 * (C) Copyright 2015 Netcentric AG.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package biz.netcentric.aem.tools.acvalidator.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;

import biz.netcentric.aem.tools.acvalidator.api.ACValidatorException;
import biz.netcentric.aem.tools.acvalidator.api.ACValidatorService;
import biz.netcentric.aem.tools.acvalidator.api.FileResult;
import biz.netcentric.aem.tools.acvalidator.api.TestResult;
import biz.netcentric.aem.tools.acvalidator.api.TestRun;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.mapper.YamlTestSetMapper;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.model.RootNode;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.parser.YamlParser;
import biz.netcentric.aem.tools.acvalidator.gui.yaml.parser.YamlParserException;
import biz.netcentric.aem.tools.acvalidator.model.AcTestSet;
import biz.netcentric.aem.tools.acvalidator.serviceuser.ServiceResourceResolverService;

/**
 * Service interface to e.g. run tests.
 * 
 * @author Roland Gruber
 */
@Component
@Service
public class ACValidatorServiceImpl implements ACValidatorService {

	private final Logger LOG = LoggerFactory.getLogger(ACValidatorServiceImpl.class);


	private static final int RUN_MODE_START_INDEX = 1;

	private static final String YAML = ".yaml";

	@Reference
	private ServiceResourceResolverService resolverService;

	@Reference
	private SlingSettingsService slingSettingsService;

	@Override
	public String getVersion() {
		return FrameworkUtil.getBundle(ACValidatorServiceImpl.class).getVersion().toString();
	}

	@Override
	public synchronized TestRun runTests(String path, boolean skipSimulation) throws ACValidatorException {
		if (StringUtils.isEmpty(path)) {
			throw new ACValidatorException("Starting path is empty");
		}
		ResourceResolver resolver = null;
		try {
			resolver = resolverService.getServiceResourceResolver();
			List<FileResult> results = new ArrayList<FileResult>();
			Resource pathResource = resolver.getResource(path);
			if(pathResource == null){
				throw new ACValidatorException("Could not get resource for path: " + path);
			}
			ValueMap vm = pathResource.getValueMap();
			String primaryType = vm.get(JcrConstants.JCR_PRIMARYTYPE, "");
			if(primaryType.isEmpty()){
				throw new ACValidatorException("Could not get primary type of resource: " + path);
			}
			List<String> files = new ArrayList<String>();
			if(isFolder(primaryType)){
				files = getFilesFromRepo(resolver, path);
			}else{
				files.add(path);
			}
			for(String currentPath:files){
				TestFile testFile = readFile(resolver.getResource(currentPath), skipSimulation);
				results.add(execute(testFile));
			}
			return new TestRun(results);
		}
		catch (LoginException e) {
			LOG.error("Exception: {}", e);
			throw new ACValidatorException("Unable to get service resource resolver", e);
		}
		catch (Exception e) {
			LOG.error("Exception: {}", e);
			throw new ACValidatorException("Error during test run: " + e.getMessage(), e);
		}
		finally {
			if (resolver != null) {
				resolver.close();
			}
		}
	}

	private boolean isFolder(String primaryType) {
		return "nt:folder".equals(primaryType) || "sling:folder".equals(primaryType) || "sling:ordererfolder".equals(primaryType);
	}

	@Override
	public List<String> getFiles(String path) throws ACValidatorException {
		ResourceResolver resolver = null;
		try {
			resolver = resolverService.getServiceResourceResolver();
			return getFilesFromRepo(resolver, path);
		}
		catch (LoginException e) {
			throw new ACValidatorException("Unable to get service resource resolver", e);
		}
	}

	/**
	 * Reads the starting path and returns a list of tests to run.
	 * 
	 * @param resolver resource resolver
	 * @param path starting path
	 * @return test files
	 * @throws ACValidatorException error reading files
	 */
	private List<String> getFilesFromRepo(ResourceResolver resolver, String path) throws ACValidatorException {
		Resource resource = resolver.getResource(path);
		if (resource == null) {
			throw new ACValidatorException("Path " + path + " not found");
		}
		List<String> files = new ArrayList<>();
		if (!matchesRunMode(resource.getName())) {
			return files;
		}
		String primaryType = resource.getValueMap().get(JcrConstants.JCR_PRIMARYTYPE, String.class);
		if (resource.getName().endsWith(YAML) && JcrConstants.NT_FILE.equals(primaryType)) {
			files.add(resource.getPath());
		}
		else {
			for (Resource child : resource.getChildren()) {
				files.addAll(getFilesFromRepo(resolver, child.getPath()));
			}
		}
		return files;
	}

	/**
	 * Reads the test set from the file.
	 * 
	 * @param resource file node
	 * @param skipSimulation skip simulation even if configured in file
	 * @return file
	 * @throws ACValidatorException error reading file
	 */
	private TestFile readFile(Resource resource, boolean skipSimulation) throws ACValidatorException {
		Resource jcrContentResource = resource.getChild(JcrConstants.JCR_CONTENT);
		if(jcrContentResource == null){
			throw new ACValidatorException("Unable to get jcr:content resource of resource " + resource.getPath());
		}
		String content = jcrContentResource.getValueMap().get(JcrConstants.JCR_DATA, String.class);
		YamlParser parser = new YamlParser();
		try {
			RootNode root = parser.parse(content);
			root = parser.unrollAndReplaceVariables(root);
			YamlTestSetMapper mapper = new YamlTestSetMapper(root, skipSimulation, resource.getPath());
			return new TestFile(resource.getPath(), mapper.getTestSets());
		} catch (YamlParserException e) {
			throw new ACValidatorException(e.getMessage(), e);
		}
	}

	/**
	 * Executes the tests in the file.
	 * 
	 * @param file test file
	 * @return test result
	 * @throws ACValidatorException 
	 */
	private FileResult execute(TestFile file) throws ACValidatorException {
		List<TestResult> results = new ArrayList<>();
		for (AcTestSet set : file.getTestSets()) {
			try {
				results.addAll(set.isOk(resolverService));
			} catch (RepositoryException|LoginException e) {
				throw new ACValidatorException(e.getMessage());
			}
		}
		FileResult result = new FileResult(file.getPath(), results);
		return result;
	}

	/**
	 * Checks if the current node name matches the run modes.
	 * 
	 * @param name node name
	 * @return matches run modes
	 */
	protected boolean matchesRunMode(String name) {
		Set<String> runModes = slingSettingsService.getRunModes();
		if (!name.contains(".") || name.endsWith(YAML)) {
			return true;
		}
		String[] parts = name.split("\\.");
		if (parts.length < 2) {
			return true;
		}
		for (int i = RUN_MODE_START_INDEX; i < parts.length; i++) {
			if (!runModes.contains(parts[i])) {
				return false;
			}
		}
		return true;
	}

	@Override
	public TestRun runSingleFileTest(String path) throws ACValidatorException {
		FileResult result = execute(new TestFile(path, null));
		return new TestRun(Arrays.asList(result));
	}

}
