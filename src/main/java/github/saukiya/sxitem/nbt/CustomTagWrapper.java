package github.saukiya.sxitem.nbt;

import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * CustomItemTagContainer封装类
 * 兼容版本: V1_13_R2+
 * 默认该插件不实用，因为实际效果略差
 * <p>
 * 食用方法:
 * CustomTagWrapper customTagWrapper = new CustomTagWrapper(plugin, meta.getCustomTagContainer());
 * <p>
 * 无法实现的方法: get(默认)\contains
 * 可实现但是未编写的方法:
 * 1.getList\getStringList\getMap (需要实现接口ItemTagType[CustomItemTagContainer,List/Map], 可自行往types内添加)
 * 2.isArray(需要手动判断byte[]\int[]\long[])
 * <p>
 * TODO 需要改成伪map结构以减少多余的NamespacedKey生成
 */
@Getter
public class CustomTagWrapper implements CompoundBase {

    public static final ArrayList<ItemTagType> types = new ArrayList<>(Arrays.asList(
            ItemTagType.BYTE,
            ItemTagType.SHORT,
            ItemTagType.INTEGER,
            ItemTagType.LONG,
            ItemTagType.FLOAT,
            ItemTagType.DOUBLE,
            ItemTagType.STRING,
            ItemTagType.BYTE_ARRAY,
            ItemTagType.INTEGER_ARRAY,
            ItemTagType.LONG_ARRAY,
            ItemTagType.TAG_CONTAINER)
    );

    final NamespacedKey tagKeys;
    Plugin plugin;
    CustomItemTagContainer handle;

    public CustomTagWrapper(Plugin plugin, CustomItemTagContainer handle) {
        this.plugin = plugin;
        this.handle = handle;
        this.tagKeys = new NamespacedKey(plugin, "TagKeys");
    }

    public <T, Z> Z get(CustomItemTagContainer current, String path, ItemTagType<T, Z> type) {
        Validate.notEmpty(path, "Cannot get to an empty path");
        int index = path.indexOf('.');
        if (index == -1) return current.getCustomTag(new NamespacedKey(plugin, path), type);
        current = current.getCustomTag(new NamespacedKey(plugin, path.substring(0, index)), ItemTagType.TAG_CONTAINER);
        if (current != null) {
            return get(current, path.substring(index + 1), type);
        }
        return null;
    }

    public Object set(CustomItemTagContainer current, String path, Object value) {
        Validate.notEmpty(path, "Cannot get to an empty path");
        int index = path.indexOf('.');
        if (index == -1) {
            NamespacedKey namespacedKey = new NamespacedKey(plugin, path);
            Object ret = current.getCustomTag(namespacedKey, getType(value.getClass()));
            if (value != null) {
                addKey(current, path);
                current.setCustomTag(namespacedKey, getType(value.getClass()), value);
            } else {
                removeKey(current, path);
                current.removeCustomTag(namespacedKey);
            }
            return ret;
        }
        NamespacedKey namespacedKey = new NamespacedKey(plugin, path.substring(0, index));
        CustomItemTagContainer child = current.getCustomTag(namespacedKey, ItemTagType.TAG_CONTAINER);
        if (child == null) {
            child = current.getAdapterContext().newTagContainer();
            addKey(current, path.substring(0, index));
        }
        Object ret = set(child, path.substring(index + 1), value);
        current.setCustomTag(namespacedKey, ItemTagType.TAG_CONTAINER, child);
        return ret;
    }

    @Override
    public <V> V get(String path, Class<V> t) {
        ItemTagType type = getType(t);
        if (type == null) return null;
        Object obj = get(handle, path, type);
        if (obj != null && t.isAssignableFrom(obj.getClass())) {
            return (V) obj;
        }
        return null;
    }

    @Override
    public Object get(String path) {
        return null;
    }

    @Override
    public Object set(String path, Object value) {
        return set(handle, path, value);
    }

    public void addKey(CustomItemTagContainer current, String key) {
        Set<String> keySet = keySet(current);
        if (keySet != null) {
            keySet.add(key);
            current.setCustomTag(tagKeys, ItemTagType.STRING, String.join("\n", keySet));
        }
    }

    public void removeKey(CustomItemTagContainer current, String key) {
        Set<String> keySet = keySet(current);
        if (keySet != null) {
            keySet.remove(key);
            current.setCustomTag(tagKeys, ItemTagType.STRING, String.join("\n", keySet));
        }
    }

    public Set<String> keySet(CustomItemTagContainer current) {
        if (current == null) return null;
        String keyStr = current.getCustomTag(tagKeys, ItemTagType.STRING);
        if (keyStr != null) {
            return new HashSet<>(Arrays.asList(keyStr.split("\n")));
        }
        return new HashSet<>();
    }

    @Override
    public Set<String> keySet(String path) {
        if (path == null) return keySet(handle);
        CustomItemTagContainer base = get(handle, path, ItemTagType.TAG_CONTAINER);
        if (base != null) {
            return keySet(base);
        }
        return null;
    }

    @Override
    public Byte getByte(String path) {
        return get(path, Byte.class);
    }

    @Override
    public Short getShort(String path) {
        return get(path, Short.class);
    }

    @Override
    public Integer getInt(String path) {
        return get(path, Integer.class);
    }

    @Override
    public Long getLong(String path) {
        return get(path, Long.class);
    }

    @Override
    public Float getFloat(String path) {
        return get(path, Float.class);
    }

    @Override
    public Double getDouble(String path) {
        return get(path, Double.class);
    }

    public static ItemTagType getType(Class typeClass) {
        return types.stream().filter(tagType -> tagType.getComplexType().equals(typeClass)).findFirst().orElse(null);
    }
}
