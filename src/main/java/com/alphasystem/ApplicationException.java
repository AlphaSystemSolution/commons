/**
 * 
 */
package com.alphasystem;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sali
 * 
 */
public abstract class ApplicationException extends Exception {

	private static final long serialVersionUID = 10364397046867529L;

	protected List<ApplicationErrorCode> errorCodes = new ArrayList<ApplicationErrorCode>();

	protected <T extends ApplicationErrorCode> ApplicationException(
			Class<T> errorCodeClass, String code) {
		this(errorCodeClass, code, code);
	}

	protected <T extends ApplicationErrorCode> ApplicationException(
			Class<T> errorCodeClass, String code, String description) {
		this(errorCodeClass, code, description, null);
	}
	
	protected <T extends ApplicationErrorCode> ApplicationException(
			Class<T> errorCodeClass, String code, String description,
			Throwable cause) {
		super(description, cause);
		try {
			Constructor<T> cons = errorCodeClass.getConstructor(String.class,
					String.class);
			T errorCode = cons.newInstance(code, description);
			errorCodes.add(errorCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends ApplicationErrorCode> List<T> getErrorCodes() {
		return (List<T>) errorCodes;
	}

}
