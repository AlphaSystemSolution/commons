/**
 * 
 */
package com.alphasystem;

/**
 * @author sali
 * 
 */
public class SystemException extends ApplicationException {

	private static final long serialVersionUID = 4153167341990093330L;

	public SystemException(String code) {
		super(SystemErrorCode.class, code);
	}

	public SystemException(String code, String description) {
		super(SystemErrorCode.class, code, description);
	}

	public SystemException(String code, String description, Throwable cause) {
		super(SystemErrorCode.class, code, description, cause);
	}

	public SystemException(String message, Throwable cause) {
		super(SystemErrorCode.class, "GEN_SYSTEM_ERROR", message, cause);
	}

}
