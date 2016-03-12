package com.alphasystem.util;

import java.io.Serializable;

import static com.alphasystem.util.AppUtil.isInstanceOf;
import static java.util.Objects.hash;

/**
 * @author sali
 */
public class KeyValuePair<K, V> implements Serializable {

    private final K key;
    private final V value;

    public KeyValuePair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = super.equals(obj);
        if (isInstanceOf(KeyValuePair.class, obj)) {
            KeyValuePair o = (KeyValuePair) obj;
            result = (o == null) ? false : key.equals(o.getKey());
        }
        return result;
    }

    @Override
    public int hashCode() {
        return hash(key);
    }

    @Override
    public String toString() {
        return (key == null) ? super.toString() : key.toString();
    }
}
