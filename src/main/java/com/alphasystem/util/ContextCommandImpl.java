/**
 * 
 */
package com.alphasystem.util;

import static com.alphasystem.util.HashCodeUtil.hash;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.Serializable;

import com.alphasystem.util.context.ContextCommand;

/**
 * @author sali
 * 
 */
public abstract class ContextCommandImpl implements ContextCommand,
		Comparable<ContextCommandImpl>, Serializable {

	private static final long serialVersionUID = -1944357417326038013L;
	
	private final String command;

	/**
	 * @param command
	 * @throws IllegalArgumentException
	 */
	protected ContextCommandImpl(final String command)
			throws IllegalArgumentException {
		if (isBlank(command)) {
			throw new IllegalArgumentException("Null or empty command");
		}
		this.command = command;
	}

	@Override
	public int compareTo(ContextCommandImpl o) {
		return o == null ? 1 : command.compareTo(o.getCommmand());
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = super.equals(obj);
		if (obj != null
				&& ContextCommandImpl.class.isAssignableFrom(obj.getClass())) {
			ContextCommandImpl o = (ContextCommandImpl) obj;
			result = command.equals(o.getCommmand());
		}
		return result;
	}

	@Override
	public String getCommmand() {
		return command;
	}

	@Override
	public int hashCode() {
		return hash(super.hashCode(), command);
	}

	@Override
	public String toString() {
		return command;
	}

}
