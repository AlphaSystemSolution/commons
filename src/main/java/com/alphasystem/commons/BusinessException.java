package com.alphasystem.commons;

import java.io.Serial;

/**
 * BusinessException wraps all the exception thrown.
 * 
 * @author Syed Farhan Ali
 */
public class BusinessException extends Exception {

	@Serial
	private static final long serialVersionUID = 3444175258941101563L;

	/**
	 *
	 * @param message Error message
	 */
	public BusinessException(String message) {
		super(message);
	}

	/**
	 *
	 * @param message Error message
	 * @param cause Underlying cause.
	 */
	public BusinessException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 *
	 * @param cause Underlying cause.
	 */
	public BusinessException(Throwable cause) {
		this(cause.getMessage(), cause);
	}

}
