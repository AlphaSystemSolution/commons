/**
 * 
 */
package com.alphasystem.util;

/**
 * @author sali
 * 
 */
public class MethodNotSupportedException extends RuntimeException {

	private static final long serialVersionUID = 6306335250316683570L;

	/**
	 * 
	 */
	public MethodNotSupportedException() {
	}

	/**
	 * @param message
	 */
	public MethodNotSupportedException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public MethodNotSupportedException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MethodNotSupportedException(String message, Throwable cause) {
		super(message, cause);
	}

}
