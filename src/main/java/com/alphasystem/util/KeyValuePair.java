package com.alphasystem.util;

import static java.util.Objects.hash;

/**
 * @author sali
 */
public class KeyValuePair<K, V> {

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
    public int hashCode() {
        return hash(key);
    }

    @Override
    public String toString() {
        return (key == null) ? super.toString() : key.toString();
    }
}
