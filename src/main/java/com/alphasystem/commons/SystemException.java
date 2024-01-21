/**
 * 
 */
package com.alphasystem.commons;

/**
 * @author sali
 * 
 */
public class SystemException extends Exception {

	/**
	 *
	 * @param message Error message
	 */
	public SystemException(String message) {
		super(message);
	}

	/**
	 * Creates SystemException from message and cause.
	 *
	 * @param message Error message
	 * @param cause Underlying cause
	 */
	public SystemException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Creates SystemException from cause.
	 *
	 * @param cause Underlying cause
	 */
	public SystemException(Throwable cause) {
		this(cause.getMessage(), cause);
	}

}
