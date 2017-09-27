package biz.netcentric.aem.tools.acvalidator.api;

import java.util.List;

/**
 * Result of a test run.
 * 
 * @author Roland Gruber
 */
public class TestRun {

	private List<FileResult> fileResults;
	
	/**
	 * Constrcutor.
	 * 
	 * @param fileResults single file results
	 */
	public TestRun(List<FileResult> fileResults) {
		this.fileResults = fileResults;
	}
	
	/**
	 * Returns if the whole test was ok.
	 * 
	 * @return test ok
	 */
	public boolean isOk() {
		for (FileResult result : fileResults) {
			if (!result.isOk()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		int testsCount = 0;
		int testsFailed = 0;
		int testsSucceeded = 0;
		StringBuilder output = new StringBuilder();
		for (FileResult file : fileResults) {
			StringBuilder errorMessages = new StringBuilder("\n\n");
			int testsFileCount = 0;
			int testsFileFailed = 0;
			int testsFileSucceeded = 0;
			for (TestResult result : file.getResults()) {
				testsCount++;
				testsFileCount++;
				if (result.isOk()) {
					testsSucceeded++;
					testsFileSucceeded++;
				}
				else {
					testsFailed++;
					testsFileFailed++;
					errorMessages.append(file.getFileName() + ": " + result.getAuthorizable() + " " + result.getTestName() + " " + result.getParameters() + ": " + result.getErrorMessage() + "\n");
				}
			}
			output.append("File " + file.getFileName() + "\n\n" +
					"Tests run: " + testsFileCount + "\n" +
					"Tests ok: " + testsFileSucceeded + "\n" +
					"Tests failed: " + testsFileFailed + "\n\n\n");
			if (testsFileFailed > 0) {
				output.append(errorMessages.toString() + "\n\n\n");
			}
		}
		output.append("Summary:\n\n" +
						"Tests run: " + testsCount + "\n" +
						"Tests ok: " + testsSucceeded + "\n" +
						"Tests failed: " + testsFailed + "\n");
		return output.toString();
	}

	/**
	 * Returns the file results.
	 * 
	 * @return results
	 */
	public List<FileResult> getFileResults() {
		return fileResults;
	}
	
}
