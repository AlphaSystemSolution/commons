package com.alphasystem.util;

import com.alphasystem.BusinessException;

import java.util.ServiceLoader;
import java.util.prefs.Preferences;

public abstract class GenericPreferences {

	private static GenericPreferences instance;

	// ~ Instance/static variables .............................................

	/**
	 * DOCUMENT ME!
	 * 
	 * @param name
	 *            DOCUMENT ME!
	 * @return DOCUMENT ME!
	 * @throws BusinessException
	 *             DOCUMENT ME!
	 */
	@SuppressWarnings("rawtypes")
	protected static Class getClassForName(String name)
			throws BusinessException {
		try {
			return Class.forName(name);
		} catch (Exception ex) {
			throw new BusinessException(ex.getMessage(), ex);
		}
	}

	/**
	 * @return the instance
	 */
	public static synchronized GenericPreferences getInstance() {
		if (instance == null) {
			ServiceLoader<GenericPreferences> serviceLoader = ServiceLoader
					.load(GenericPreferences.class);
			for (GenericPreferences pref : serviceLoader) {
				instance = pref;
				if (instance != null) {
					break;
				}
			}
			if(instance==null){
				System.err.println("Still null");
			}
		}
		return instance;
	}

	// ~ Constructors ..........................................................

	protected Preferences root;

	// ~ Methods ...............................................................

	/**
	 * Creates a new GenericPreferences object.
	 * 
	 * @param c
	 *            DOCUMENT ME!
	 */
	protected GenericPreferences(Class<?> c) {
		root = Preferences.userNodeForPackage(c);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param nodeName
	 *            DOCUMENT ME!
	 * @param key
	 *            DOCUMENT ME!
	 * @param def
	 *            DOCUMENT ME!
	 * @return DOCUMENT ME!
	 */
	public String get(String nodeName, String key, String def) {
		Preferences node = root.node(nodeName);
		return node.get(key, def);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param nodeName
	 *            DOCUMENT ME!
	 * @return DOCUMENT ME!
	 */
	public Preferences getNode(String nodeName) {
		return root.node(nodeName);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public Preferences getRoot() {
		return root;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param nodeName
	 *            DOCUMENT ME!
	 * @param key
	 *            DOCUMENT ME!
	 * @param value
	 *            DOCUMENT ME!
	 */
	public void put(String nodeName, String key, String value) {
		Preferences node = root.node(nodeName);
		node.put(key, value);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws BusinessException
	 *             DOCUMENT ME!
	 */
	public void save() throws BusinessException {
		try {
			root.flush();
		} catch (Exception ex) {
			throw new BusinessException(ex.getMessage(), ex);
		}
	}
}
