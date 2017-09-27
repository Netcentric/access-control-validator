package biz.netcentric.aem.tools.acvalidator.api;

/**
 * Thrown when an error occured during test execution.
 * 
 * @author Roland Gruber
 */
public class ACValidatorException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * 
	 * @param message error message
	 * @param e original exception
	 */
	public ACValidatorException(String message, Throwable e) {
		super(message, e);
	}
	
	/**
	 * Constructor
	 * 
	 * @param message error message
	 */
	public ACValidatorException(String message) {
		super(message);
	}
	
}
