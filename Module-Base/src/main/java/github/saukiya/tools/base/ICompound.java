package github.saukiya.tools.base;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 归并Compound类的使用方法
 * path特性如同{@link org.bukkit.configuration.file.YamlConfiguration}
 */
public interface ICompound {

    /**
     * 按路径获取对象
     *
     * @param path 要获取的对象路径
     * @return 基础类型(非NMS类)，未找到路径则返回null
     */
    @Nullable
    Object get(@Nonnull String path);

    /**
     * 按路径设置对象
     *
     * @param path  要设置的对象路径
     * @param value 合法的任意数据(NMSBase、TagBase、基础类型)
     * @return 被覆盖的基础类型(非NMS类)，可以是null
     */
    @Nullable
    Object set(@Nonnull String path, @Nullable Object value);

    /**
     * 获取路径中的key的集合(浅列表)
     *
     * @param path 要获取TagCompound的路径，null时返回自身的keys
     * @return 如果未找到路径，则返回null
     */
    @Nullable
    Set<String> keySet(@Nullable String path);

    /**
     * 返回此集合中元素的数量
     *
     * @return 数量
     */
    int size();

    /**
     * 判断集合是否为空
     *
     * @return 是否为空
     */
    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * 用一个TagCompoundBase添加/覆盖到此处
     *
     * @param value TagCompound、NBTWrapper、NBTItemWrapper
     */
    default void setAll(ICompound value) {
        if (value != null) value.keySet().forEach(key -> set(key, value.get(key)));
    }

    default void setAll(Map<Object, Object> value) {
        if (value == null) return;
        value.forEach((k, v) -> set(k.toString(), v));
    }

    /**
     * 按路径删除对象
     *
     * @param path 要删除的对象路径
     * @return 被删除的基础类型(非NMS类)，可以是null
     */
    @Nullable
    default Object remove(@Nonnull String path) {
        return set(path, null);
    }

    /**
     * 获取所有key的集合(浅列表)
     *
     * @return 包含的一组key
     */
    @Nonnull
    default Set<String> keySet() {
        return keySet(null);
    }

    /**
     * 检查是否存在指定的路径
     * (不推荐使用，不如自行get后检查)
     *
     * @param path 需要检查的路径
     * @return 如果路径中存在值，则为true
     */
    @Deprecated
    default boolean contains(String path) {
        return get(path) != null;
    }

    default boolean isArray(String path) {
        return get(path).getClass().isArray();
    }

    default <V> V get(String path, Class<V> t) {
        return get(path, t, null);
    }

    default <V> V get(String path, Class<V> t, V def) {
        Object obj = get(path);
        if (obj != null && t.isAssignableFrom(obj.getClass())) {
            return (V) obj;
        }
        return def;
    }

    default String getString(String path) {
        return getString(path, null);
    }

    default String getString(String path, String def) {
        return get(path, String.class, def);
    }

    default Boolean getBoolean(String path) {
        return getBoolean(path, false);
    }

    default Boolean getBoolean(String path, boolean def) {
        Byte b = getByte(path, (byte) (def ? 1 : 0));
        return b != null && b != 0;
    }

    default Byte getByte(String path) {
        return getByte(path, null);
    }

    default Byte getByte(String path, Byte def) {
        Number number = get(path, Number.class);
        return number != null ? (Byte) number.byteValue() : def;
    }

    default Short getShort(String path) {
        return getShort(path, null);
    }

    default Short getShort(String path, Short def) {
        Number number = get(path, Number.class);
        return number != null ? (Short) number.shortValue() : def;
    }

    default Integer getInt(String path) {
        return getInt(path, null);
    }

    default Integer getInt(String path, Integer def) {
        Number number = get(path, Number.class);
        return number != null ? (Integer) number.intValue() : def;
    }

    default Long getLong(String path) {
        return getLong(path, null);
    }

    default Long getLong(String path, Long def) {
        Number number = get(path, Number.class);
        return number != null ? (Long) number.longValue() : def;
    }

    default Float getFloat(String path) {
        return getFloat(path, null);
    }

    default Float getFloat(String path, Float def) {
        Number number = get(path, Number.class);
        return number != null ? (Float) number.floatValue() : def;
    }

    default Double getDouble(String path) {
        return getDouble(path, null);
    }

    default Double getDouble(String path, Double def) {
        Number number = get(path, Number.class);
        return number != null ? (Double) number.doubleValue() : def;
    }

    default List<?> getList(String path) {
        return getList(path, null);
    }

    default List<?> getList(String path, List<?> def) {
        return get(path, List.class, def);
    }

    default List<String> getStringList(String path) {
        return getStringList(path, null);
    }

    default List<String> getStringList(String path, List<String> def) {
        return get(path, List.class, def);
    }

    default int[] getIntArray(String path) {
        return getIntArray(path, null);
    }

    default int[] getIntArray(String path, int[] def) {
        return get(path, int[].class, def);
    }

    default byte[] getByteArray(String path) {
        return getByteArray(path, null);
    }

    default byte[] getByteArray(String path, byte[] def) {
        return get(path, byte[].class, def);
    }

    default long[] getLongArray(String path) {
        return getLongArray(path, null);
    }

    default long[] getLongArray(String path, long[] def) {
        return get(path, long[].class, def);
    }

    default Map<String, ?> getMap(String path) {
        return getMap(path, Collections.EMPTY_MAP);
    }

    default Map<String, ?> getMap(String path, Map<String, ?> def) {
        return get(path, Map.class, def);
    }
}
