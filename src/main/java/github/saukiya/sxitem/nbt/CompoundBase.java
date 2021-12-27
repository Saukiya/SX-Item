package github.saukiya.sxitem.nbt;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 归并Compound类的使用方法
 * path特性如同{@link org.bukkit.configuration.file.YamlConfiguration}
 */
public interface CompoundBase {

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
        Object obj = get(path);
        if (obj != null && t.isAssignableFrom(obj.getClass())) {
            return (V) obj;
        }
        return null;
    }

    default String getString(String path) {
        return get(path, String.class);
    }

    default Boolean getBoolean(String path) {
        Byte b = get(path, Byte.class);
        return b != null && b == 1;
    }

    default Byte getByte(String path) {
        Number number = get(path, Number.class);
        return number == null ? null : number.byteValue();
    }

    default Short getShort(String path) {
        Number number = get(path, Number.class);
        return number == null ? null : number.shortValue();
    }

    default Integer getInt(String path) {
        Number number = get(path, Number.class);
        return number == null ? null : number.intValue();
    }

    default Long getLong(String path) {
        Number number = get(path, Number.class);
        return number == null ? null : number.longValue();
    }

    default Float getFloat(String path) {
        Number number = get(path, Number.class);
        return number == null ? null : number.floatValue();
    }

    default Double getDouble(String path) {
        Number number = get(path, Number.class);
        return number == null ? null : number.doubleValue();
    }

    default List<?> getList(String path) {
        return get(path, List.class);
    }

    default List<String> getStringList(String path) {
        return get(path, List.class);
    }

    default int[] getIntArray(String path) {
        return get(path, int[].class);
    }

    default byte[] getByteArray(String path) {
        return get(path, byte[].class);
    }

    default long[] getLongArray(String path) {
        return get(path, long[].class);
    }

    default Map<String, ?> getMap(String path) {
        return get(path, Map.class);
    }
}
