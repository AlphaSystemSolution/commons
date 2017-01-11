package com.alphasystem.util;

import com.alphasystem.BusinessException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.prefs.Preferences;

public abstract class GenericPreferences {

    private static final Map<String, GenericPreferences> instances = Collections.synchronizedMap(new HashMap<>());
    private static GenericPreferences instance;

    // ~ Instance/static variables .............................................

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     * @return DOCUMENT ME!
     * @throws BusinessException DOCUMENT ME!
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
            if (instance == null) {
                System.err.println("Still null");
            }
        }
        return instance;
    }

    public static <P extends GenericPreferences> P getInstance(Class<P> _class) {
        return getInstance(_class, false);
    }

    @SuppressWarnings({"unchecked"})
    private static <P extends GenericPreferences> P getInstance(Class<P> _class, boolean recursive) {
        final String _className = _class.getName();
        P instance = (P) instances.get(_className);
        if (instance == null) {
            ServiceLoader<GenericPreferences> serviceLoader = ServiceLoader
                    .load(GenericPreferences.class);

            for (GenericPreferences pref : serviceLoader) {
                boolean prefType = _className.equals(pref.getClass().getName());
                if (!prefType && recursive) {
                    prefType = AppUtil.isInstanceOf(_class, pref);
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
                return getInstance(_class, true);
            }
            throw new RuntimeException(String.format("Unable to find instance for Class \"%s\".", _className));
        }
        return instance;
    }

    // ~ Constructors ..........................................................

    private Preferences root;

    // ~ Methods ...............................................................

    /**
     * Creates a new GenericPreferences object.
     *
     * @param c DOCUMENT ME!
     */
    protected GenericPreferences(Class<?> c) {
        root = Preferences.userNodeForPackage(c);
    }

    /**
     * DOCUMENT ME!
     *
     * @param nodeName DOCUMENT ME!
     * @param key      DOCUMENT ME!
     * @param def      DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    public String get(String nodeName, String key, String def) {
        Preferences node = root.node(nodeName);
        return node.get(key, def);
    }

    public Preferences getNode(String prefix, String nodeName){
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
