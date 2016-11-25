package com.alphasystem;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author sali
 */
public final class PropertyInfo {

    private final String propertyName;
    private final Field field;
    private final Method readMethod;
    private final Method writeMethod;

    public PropertyInfo(String propertyName, Field field, Method readMethod, Method writeMethod) {
        this.propertyName = propertyName;
        this.field = field;
        this.readMethod = readMethod;
        this.writeMethod = writeMethod;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Field getField() {
        return field;
    }

    public Method getReadMethod() {
        return readMethod;
    }

    public Method getWriteMethod() {
        return writeMethod;
    }
}
