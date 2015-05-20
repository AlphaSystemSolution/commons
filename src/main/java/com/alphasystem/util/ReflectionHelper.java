/**
 * 
 */
package com.alphasystem.util;

import static java.lang.Class.forName;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author sali
 * 
 */
public final class ReflectionHelper {

	protected static final Log LOG = LogFactory.getLog(ReflectionHelper.class);

	/**
	 * Recursive method used to find all classes in a given directory and
	 * subdirs.
	 * 
	 * @param directory
	 *            The base directory
	 * @param packageName
	 *            The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private static List<Class<?>> findClasses(File directory, String packageName)
			throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				classes.addAll(findClasses(file,
						packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(forName(packageName
						+ '.'
						+ file.getName().substring(0,
								file.getName().length() - 6)));
			}
		}
		return classes;
	}

	public static Iterable<Class<?>> getClasses(String packageName)
			throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}

		return classes;
	}

	public static int getCollectionSize(Class<?> collectionClass, Object value) {
		Integer size = 0;
		if (Collection.class.isAssignableFrom(collectionClass)) {
			try {
				Method sizeMethod = collectionClass.getMethod("size");
				size = (Integer) sizeMethod.invoke(value);
				if (size == null) {
					size = 0;
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return size;
	}

	public static String getPropertyName(String methodName) {
		String propertyName = methodName;
		if (methodName.startsWith("get")) {
			propertyName = methodName.substring(3);
		} else if (methodName.startsWith("is")) {
			propertyName = methodName.substring(2);
		}
		return Character.toString(propertyName.charAt(0)).toLowerCase()
				+ propertyName.substring(1);
	}

	public static Object invoke(Method method, Object obj, Object... args) {
		Object value = null;
		try {
			value = method.invoke(obj, args);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return value;
	}
}
