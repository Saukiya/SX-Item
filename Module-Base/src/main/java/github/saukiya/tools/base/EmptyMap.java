package github.saukiya.tools.base;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Set;

/**
 * 永远的空map,
 * <pre>
 * 与 {@link Collections#emptyMap()} 功能类似
 * 但是调用 put 时不会产生 {@link UnsupportedOperationException}
 * {@code
 *  return EmptyMap.emptyMap();
 * }
 * </pre>
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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
