package github.saukiya.sxitem.nms;

import java.util.Set;

public interface NBTTagWrapper {

    default Set<String> getKeys() {
        return getKeys(null);
    }

    Set<String> getKeys(String path);

    default <V> V getNBT(String path) {
        return getNBT(path, null);
    }

    <V> V getNBT(String path, V def);

    /**
     * 设置nbt
     *
     * @param path
     * @param def
     */
    void setNBT(String path, Object def);

    void removeNBT(String path);

    default boolean hasNBT(String path) {
        return getNBT(path, null) != null;
    }
}
