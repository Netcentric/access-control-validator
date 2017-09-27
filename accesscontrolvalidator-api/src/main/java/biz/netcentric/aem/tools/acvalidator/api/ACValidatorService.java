package biz.netcentric.aem.tools.acvalidator.api;

import java.util.List;

/**
 * Public service interface to e.g. run tests.
 * 
 * @author Roland Gruber
 */
public interface ACValidatorService {

	/**
	 * Returns the version of the AC Validator.
	 * 
	 * @return version
	 */
	String getVersion();
	
	/**
	 * Path to folder where test files are located.
	 * 
	 * @param path folder path
	 * @param skipSimulation skip simulation even if configured in file
	 * @return test run result
	 * @throws ACValidatorException error during test run
	 */
	TestRun runTests(String path, boolean skipSimulation) throws ACValidatorException;

	/**
	 * Path to file where tests are located.
	 * 
	 * @param path file path
	 * @return test run result
	 * @throws ACValidatorException error during test run
	 */
	TestRun runSingleFileTest(String path) throws ACValidatorException;

	/**
	 * Returns a list of possible test files in the given path.
	 * If the path is a directory then also subdirectories will be searched.
	 * 
	 * @param path path
	 * @return test file paths
	 * @throws ACValidatorException error during test run
	 */
	List<String> getFiles(String path) throws ACValidatorException;
	
}
