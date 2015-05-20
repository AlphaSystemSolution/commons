/**
 * 
 */
package com.alphasystem;

import static com.alphasystem.util.HashCodeUtil.hash;
import static java.lang.String.format;

import java.io.Serializable;

/**
 * @author sali
 * 
 */
public abstract class ApplicationErrorCode implements Serializable,
		Comparable<ApplicationErrorCode> {

	private static final long serialVersionUID = -716216806919398994L;

	protected String code;

	protected String description;

	public ApplicationErrorCode() {
	}

	protected ApplicationErrorCode(String code, String description) {
		setCode(code);
		this.description = description;
	}

	@Override
	public int compareTo(ApplicationErrorCode o) {
		return (o == null) ? 1 : getCode().compareTo(o.getCode());
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = super.equals(obj);
		if (obj != null && obj instanceof ApplicationErrorCode) {
			ApplicationErrorCode o = (ApplicationErrorCode) obj;
			result = getCode().equals(o.getCode());
		}
		return result;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public int hashCode() {
		return hash(code);
	}

	public void setCode(String code) {
		if (code == null) {
			code = "NONE";
		}
		this.code = code;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return format("%s:%s", code, description);
	}

}
