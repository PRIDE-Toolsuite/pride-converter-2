package uk.ac.ebi.pride.tools.converter.utils;

/**
 * Thrown whenever an invalid formatted file
 * is encountered.
 * @author jg
 *
 */
public class InvalidFormatException extends Exception {

	public InvalidFormatException() {
		
	}

	public InvalidFormatException(String arg0) {
		super(arg0);
	}

	public InvalidFormatException(Throwable arg0) {
		super(arg0);
	}

	public InvalidFormatException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
