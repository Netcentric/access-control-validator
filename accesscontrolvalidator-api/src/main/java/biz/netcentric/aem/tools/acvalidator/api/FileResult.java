package biz.netcentric.aem.tools.acvalidator.api;

import java.util.List;

/**
 * Test results for a specific file.
 * 
 * @author Roland Gruber
 */
public class FileResult {
	
	private String fileName;
	
	private List<TestResult> results;
	
	/**
	 * Constructor.
	 * 
	 * @param fileName file name
	 * @param results results
	 */
	public FileResult(String fileName, List<TestResult> results) {
		this.fileName = fileName;
		this.results = results;
	}
	
	/**
	 * Returns if all tests in this file were ok.
	 * 
	 * @return tests ok
	 */
	public boolean isOk() {
		for (TestResult result : results) {
			if (!result.isOk()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the file name.
	 * 
	 * @return file name
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Returns the result list.
	 * 
	 * @return results
	 */
	public List<TestResult> getResults() {
		return results;
	}
	
	/**
	 * Returns the percentage of tests that are ok.
	 * 
	 * @return percentage
	 */
	public double getPercentageOk() {
		int ok = 0;
		double total = results.size();
		if (total == 0) {
			return 0;
		}
		for (TestResult result : results) {
			if (result.isOk()) {
				ok++;
			}
		}
		return (ok * 100) / total;
	}
	
}
