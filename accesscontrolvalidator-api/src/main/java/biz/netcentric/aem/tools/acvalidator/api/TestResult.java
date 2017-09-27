package biz.netcentric.aem.tools.acvalidator.api;

/**
 * Result of a single test (e.g. a page or user admin test).
 * 
 * @author Roland Gruber
 */
public class TestResult {
	
	private String authorizable;
	private String testName;
	private String parameters;
	private boolean ok;
	private String errorMessage; 
	
	/**
	 * Constructor
	 * 
	 * @param authorizable authorizable name that was used to perform the test
	 * @param testName test id
	 * @param parameters additional parameters for the test run
	 * @param ok test was ok
	 * @param errorMessage error message for end user
	 */
	public TestResult(String authorizable, String testName, String parameters, boolean ok, String errorMessage) {
		this.authorizable = authorizable;
		this.testName = testName;
		this.parameters = parameters;
		this.ok = ok;
		this.errorMessage = errorMessage;
	}

	/**
	 * Returns the user name.
	 * 
	 * @return user name
	 */
	public String getAuthorizable() {
		return authorizable;
	}

	/**
	 * Returns the test id.
	 * 
	 * @return test id
	 */
	public String getTestName() {
		return testName;
	}

	/**
	 * Returns the parameters for the test.
	 * 
	 * @return parameters
	 */
	public String getParameters() {
		return parameters;
	}

	/**
	 * Returns if test was successful.
	 * 
	 * @return ok
	 */
	public boolean isOk() {
		return ok;
	}

	/**
	 * Returns the error message.
	 * 
	 * @return error message
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	@Override
	public String toString() {
		return "TestResult [user=" + authorizable + ", testName=" + testName + ", parameters=" + parameters + ", ok=" + ok
				+ ", errorMessage=" + errorMessage + "]";
	}
	
	

}
