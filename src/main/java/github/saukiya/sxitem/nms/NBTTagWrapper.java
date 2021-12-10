package github.saukiya.sxitem.nms;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

/**
 * NBTTagCompound的包装类
 * 用来封装每个版本的数据交互
 * 食用方法、特性如同{@link org.bukkit.configuration.file.YamlConfiguration}
 */
public interface NBTTagWrapper {

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
     * 按路径删除对象
     *
     * @param path 要删除的对象路径
     * @return 被删除的基础类型(非NMS类)，可以是null
     */
    @Nullable
    Object remove(@Nonnull String path);

    /**
     * 获取路径中的key的集合(浅列表)
     *
     * @param path 要获取TagCompound的路径，null时返回自身的keys
     * @return 如果未找到路径，则返回null
     */
    @Nullable
    Set<String> getKeys(@Nullable String path);

    /**
     * 获取包装好的NBTTagCompound
     * 返回的对象与父TagCompound关联
     *
     * @param path 要获取TagCompound的路径
     * @return 如果未找到路径，则返回null
     */
    @Nonnull
    NBTTagWrapper getWrapper(@Nonnull String path);

    /**
     * 获取所处理的NBTTagCompound
     *
     * @return NBTTagCompound
     */
    @Nonnull
    Object getHandle();

    /**
     * 获取所有key的集合(浅列表)
     *
     * @return 包含的一组key
     */
    @Nonnull
    default Set<String> getKeys() {
        return getKeys(null);
    }

    /**
     * 检查是否存在指定的路径
     * (不推荐使用，不如自行get后检查)
     *
     * @param path 需要检查的路径
     * @return 如果路径中存在值，则为true
     */
    default boolean contains(String path) {
        return get(path) != null;
    }
}
