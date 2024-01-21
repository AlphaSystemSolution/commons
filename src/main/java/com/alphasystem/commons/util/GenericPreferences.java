package com.alphasystem.commons.util;

import com.alphasystem.commons.BusinessException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.prefs.Preferences;

/**
 * Base class for Preferences.
 */
public abstract class GenericPreferences {

    private static final Map<String, GenericPreferences> instances = Collections.synchronizedMap(new HashMap<>());
    private static GenericPreferences instance;

    // ~ Instance/static variables .............................................

    /**
     * @return the instance
     */
    public static GenericPreferences getInstance() {
        if (instance == null) {
            ServiceLoader<GenericPreferences> serviceLoader = ServiceLoader
                    .load(GenericPreferences.class);
            for (GenericPreferences pref : serviceLoader) {
                instance = pref;
                if (instance != null) {
                    break;
                }
            }
            if (instance == null) {
                throw new RuntimeException("Unable to find any implementation");
            }
        }
        return instance;
    }

    /**
     * Returns Preferences of type P.
     *
     * @param klass Class type
     * @param <P>   GenericPreferences of type P
     * @return Preferences of type P
     */
    public static <P extends GenericPreferences> P getInstance(Class<P> klass) {
        return getInstance(klass, false);
    }

    @SuppressWarnings({"unchecked"})
    private static <P extends GenericPreferences> P getInstance(Class<P> klass, boolean recursive) {
        final String _className = klass.getName();
        P instance = (P) instances.get(_className);
        if (instance == null) {
            ServiceLoader<GenericPreferences> serviceLoader = ServiceLoader
                    .load(GenericPreferences.class);

            for (GenericPreferences pref : serviceLoader) {
                boolean prefType = _className.equals(pref.getClass().getName());
                if (!prefType && recursive) {
                    prefType = AppUtil.isInstanceOf(klass, pref);
                }
                if (prefType) {
                    instance = (P) pref;
                    instances.put(_className, instance);
                    break;
                }
            }
        }

        if (instance == null) {
            if (!recursive) {
                return getInstance(klass, true);
            }
            throw new RuntimeException(String.format("Unable to find instance for Class \"%s\".", _className));
        }
        return instance;
    }

    // ~ Constructors ..........................................................

    private final Preferences root;

    // ~ Methods ...............................................................

    /**
     * Creates a new GenericPreferences object.
     *
     * @param c Initialize Preferences for given class.
     */
    protected GenericPreferences(Class<?> c) {
        root = Preferences.userNodeForPackage(c);
    }

    /**
     * Value of the node for the given key or default value if key doesn't exist.
     *
     * @param nodeName Node name
     * @param key      Key name
     * @param def      Default value
     * @return Value of the node for the given key.
     */
    public String get(String nodeName, String key, String def) {
        Preferences node = root.node(nodeName);
        return node.get(key, def);
    }

    /**
     * Returns Preferences based on given prefix_nodeName.
     *
     * @param prefix   Prefix of the node.
     * @param nodeName Node name
     * @return Preferences based on given prefix_nodeName.
     */
    public Preferences getNode(String prefix, String nodeName) {
        return getNode(String.format("%s_%s", prefix, nodeName));
    }

    /**
     * DOCUMENT ME!
     *
     * @param nodeName DOCUMENT ME!
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
     * @param nodeName DOCUMENT ME!
     * @param key      DOCUMENT ME!
     * @param value    DOCUMENT ME!
     */
    public void put(String nodeName, String key, String value) {
        Preferences node = root.node(nodeName);
        node.put(key, value);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws BusinessException DOCUMENT ME!
     */
    public void save() throws BusinessException {
        try {
            root.flush();
        } catch (Exception ex) {
            throw new BusinessException(ex.getMessage(), ex);
        }
    }
}
