/**
 * 
 */
package com.alphasystem.util.context;

import static java.lang.String.format;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Observable;
import java.util.ServiceLoader;

/**
 * @author sali
 * 
 */
public abstract class ObservableObject<CC extends ContextCommand, OC extends ObservableContext<CC>>
		extends Observable {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T extends ObservableObject> T getInstance(Class<T> klass) {
		ServiceLoader<ObservableObject> serviceLoader = ServiceLoader
				.load(ObservableObject.class);
		for (ObservableObject observableObject : serviceLoader) {
			if (observableObject.getClass().getName().equals(klass.getName())) {
				return (T) observableObject;
			}
		}
		return null;
	}

	protected Class<CC> commandClass;

	protected Class<OC> contextClass;

	protected ObservableObject() {
		super();
		initGenericClass();
	}

	public void actionPerformed(CC actionCommnand,
			Map<String, Object> acionParams) {
		OC context = newInstance(actionCommnand);
		context.putAll(acionParams);
		doNotify(context);
	}

	public void done(CC actionCommand) {
		OC context = newInstance(actionCommand);
		doNotify(context);
	}

	protected void doNotify(OC context) {
		ContextCommand command = (context == null) ? null : context
				.getCommand();
		if (command == null) {
			throw new IllegalArgumentException(format(
					"Class %s must have command property populated.", context
							.getClass().getName()));
		}
		setChanged();
		notifyObservers(context);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void initGenericClass() {
		Class klass = getClass();
		while (true) {
			Type type = klass.getGenericSuperclass();
			if (type == null) {
				throw new RuntimeException("Could not find generic type: "
						+ getClass().getName());
			}
			if (type instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType) type;
				Type[] types = parameterizedType.getActualTypeArguments();
				commandClass = (Class<CC>) types[0];
				contextClass = (Class<OC>) types[1];
				break;
			}
			klass = klass.getSuperclass();
			if (klass == null) {
				throw new RuntimeException("Could not find generic type: "
						+ getClass().getName());
			}
		}
	}

	/**
	 * @return
	 * @throws IllegalArgumentException
	 */
	protected OC newInstance() throws IllegalArgumentException {
		return newInstance(null);
	}

	/**
	 * @param command
	 * @return
	 * @throws IllegalArgumentException
	 */
	protected OC newInstance(CC command) throws IllegalArgumentException {
		OC context = null;
		try {
			Constructor<OC> cons = null;
			if (command == null) {
				cons = contextClass.getConstructor();
				context = cons.newInstance();
			} else {
				cons = contextClass.getConstructor(commandClass);
				context = cons.newInstance(command);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
		return context;
	}
}
