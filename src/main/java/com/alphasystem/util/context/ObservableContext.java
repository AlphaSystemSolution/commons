/**
 * 
 */
package com.alphasystem.util.context;

/**
 * @author sali
 * 
 */
public class ObservableContext<C extends ContextCommand> extends ContextBase {

	private static final long serialVersionUID = 2502322208143959744L;

	protected C command;

	public ObservableContext() {
	}

	public ObservableContext(C command) {
		super();
		setCommand(command);
	}

	public C getCommand() {
		return command;
	}

	/**
	 * @param command
	 * @throws IllegalArgumentException
	 */
	public void setCommand(C command) throws IllegalArgumentException {
		if (command == null) {
			throw new IllegalArgumentException(
					"Command cannot be null or empty");
		}
		this.command = command;
	}

}
