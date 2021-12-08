package github.saukiya.sxitem.nms;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * NBTTagCompound的包装类
 * 用来封装每个版本的数据交互
 * <p>
 * NBTBase get(String path) -> NBTBase / null *1
 * <p>
 * NBTBase set(String path, Object object) -> NBTBase / null *2
 * <p>
 * Set<String> getKeys(String path) -> Set<String> / null *1
 * <p>
 * boolean remove(String path) -> true / false *1
 * <p>
 * NBTCompound getCompound()
 *
 * @return
 */
public interface NBTTagWrapper {

    Object get(String path);

    /**
     * 设置nbt
     *
     * @param path
     * @param def
     * @return
     */
    Object set(String path, Object def);

    boolean remove(String path);

    Set<String> getKeys(String path);

    default Set<String> getKeys() {
        return getKeys(null);
    }

    default boolean has(String path) {
        return get(path) != null;
    }
}
