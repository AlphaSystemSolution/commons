/**
 * 
 */
package com.alphasystem.commons;

import java.io.Serial;

/**
 * @author sali
 * 
 */
public class SystemException extends ApplicationException {

	@Serial
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

	public SystemException(Throwable cause) {
		this(cause.getMessage(), cause);
	}

}
