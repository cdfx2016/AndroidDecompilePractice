package org.jivesoftware.smack.util.collections;

public abstract class AbstractKeyValue<K, V> implements KeyValue<K, V> {
    protected K key;
    protected V value;

    protected AbstractKeyValue(K k, V v) {
        this.key = k;
        this.value = v;
    }

    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.value;
    }

    public String toString() {
        return getKey() + '=' + getValue();
    }
}
