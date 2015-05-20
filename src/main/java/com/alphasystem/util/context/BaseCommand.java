/**
 * 
 */
package com.alphasystem.util.context;

/**
 * @author sali
 * 
 */
public enum BaseCommand implements ContextCommand {

	EXIT_COMMAND;

	@Override
	public String getCommmand() {
		return name();
	}

}
