package github.saukiya.util.base;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Set;

public class EmptyMap<K, V> extends AbstractMap<K, V> {

    private static final EmptyMap<?, ?> EMPTY = new EmptyMap<>();

    @Override
    public Set<Entry<K, V>> entrySet() {
        return Collections.emptySet();
    }

    @Override
    public V put(K key, V value) {
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> EmptyMap<K, V> emptyMap() {
        return (EmptyMap<K, V>) EMPTY;
    }
}
