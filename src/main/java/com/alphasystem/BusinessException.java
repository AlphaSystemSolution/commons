package com.alphasystem;

/**
 * BusinessException wraps all the exception thrown.
 * 
 * @author Syed Farhan Ali
 */
public class BusinessException extends ApplicationException {

	private static final long serialVersionUID = 3444175258941101563L;

	public BusinessException(String code) {
		super(BusinessErrorCode.class, code);
	}

	public BusinessException(String code, String description) {
		super(BusinessErrorCode.class, code, description);
	}

	public BusinessException(String code, String description, Throwable cause) {
		super(BusinessErrorCode.class, code, description, cause);
	}

	public BusinessException(String message, Throwable cause) {
		super(BusinessErrorCode.class, "GEN_BUSINESS_ERROR", message, cause);
	}

}
