package github.saukiya.tools.nms;

import github.saukiya.tools.nbt.TagCompound;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Bukkit PDC NBT 降级实现。
 *
 * <p>Luminol 等使用非标准 NMS 映射的服务端无法加载版本化 NMS 类；这里仅保存 SX-Item
 * 自己的标识数据，保证物品识别和更新检查可用，不伪造完整的原版 NBT 映射。</p>
 */
final class NbtUtilFallback extends NbtUtil {

    private static final String KEY = "sx_item_nbt";
    private final Method getContainer;
    private final Method getValue;
    private final Method setValue;
    private final Object byteArrayType;
    private final Object namespacedKey;
    private static final Map<ItemStack, TagCompound> MEMORY = new WeakHashMap<>();

    NbtUtilFallback() {
        try {
            Class<?> meta = Class.forName("org.bukkit.inventory.meta.ItemMeta");
            Class<?> container = Class.forName("org.bukkit.persistence.PersistentDataContainer");
            Class<?> dataType = Class.forName("org.bukkit.persistence.PersistentDataType");
            Class<?> key = Class.forName("org.bukkit.NamespacedKey");
            getContainer = meta.getMethod("getPersistentDataContainer");
            getValue = container.getMethod("get", key, dataType);
            setValue = container.getMethod("set", key, dataType, Object.class);
            Field bytes = dataType.getField("BYTE_ARRAY");
            byteArrayType = bytes.get(null);
            Object plugin = Bukkit.getPluginManager().getPlugin("SX-Item");
            namespacedKey = key.getConstructor(org.bukkit.plugin.Plugin.class, String.class).newInstance(plugin, KEY);
        } catch (Exception exception) {
            getContainer = null;
            getValue = null;
            setValue = null;
            byteArrayType = null;
            namespacedKey = null;
        }
    }

    @Override
    public ItemWrapper getItemTagWrapper(ItemStack itemStack) {
        return new Wrapper(itemStack);
    }

    @Override
    public ItemWrapper getItemTagWrapper(ItemStack itemStack, Object nmsItem) {
        return new Wrapper(itemStack);
    }

    @Override
    public Wrapper createTagWrapper(Object nbtTagCompound) {
        return new Wrapper(null, nbtTagCompound instanceof TagCompound ? (TagCompound) nbtTagCompound : new TagCompound());
    }

    @Override
    public <V> V getNMSItem(ItemStack itemStack) {
        return (V) itemStack;
    }

    @Override
    public void setNMSItem(ItemStack itemStack, Object nmsItem) {
    }

    @Override
    public <V> V getNMSItemNBT(Object nmsItem) {
        return (V) read((ItemStack) nmsItem);
    }

    @Override
    public <V> V getItemNBT(ItemStack itemStack) {
        return (V) read(itemStack);
    }

    @Override
    public TagCompound asTagCompoundCopy(Object nbtTagCompound) {
        return nbtTagCompound instanceof TagCompound ? new TagCompound((TagCompound) nbtTagCompound) : new TagCompound();
    }

    @Override
    public <V> V asNMSCompoundCopy(TagCompound tagCompound) {
        return (V) tagCompound;
    }

    @Override
    public <V> V parseNMSCompound(String json) {
        return (V) new TagCompound();
    }

    @Override
    public <V> V getNMSValue(Object nbtBase) {
        return (V) (nbtBase instanceof TagCompound ? ((TagCompound) nbtBase).getValue() : nbtBase);
    }

    @Override
    public <V> V toNMS(Object obj) {
        return (V) (obj instanceof TagCompound ? obj : new TagCompound(obj instanceof java.util.Map ? (java.util.Map<?, ?>) obj : new HashMap<>()));
    }

    private TagCompound read(ItemStack item) {
        if (getContainer == null) return MEMORY.getOrDefault(item, new TagCompound());
        if (item == null || item.getItemMeta() == null) return new TagCompound();
        try {
            Object value = getValue.invoke(getContainer.invoke(item.getItemMeta()), namespacedKey, byteArrayType);
            if (!(value instanceof byte[])) return new TagCompound();
            ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream((byte[]) value));
            return new TagCompound((java.util.Map<?, ?>) input.readObject());
        } catch (Exception ignored) {
            return new TagCompound();
        }
    }

    private void write(ItemStack item, TagCompound value) {
        if (getContainer == null) {
            MEMORY.put(item, value);
            return;
        }
        if (item == null || item.getItemMeta() == null) return;
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            new ObjectOutputStream(bytes).writeObject(value.getValue());
            Object meta = item.getItemMeta();
            setValue.invoke(getContainer.invoke(meta), namespacedKey, byteArrayType, bytes.toByteArray());
            item.setItemMeta((org.bukkit.inventory.meta.ItemMeta) meta);
        } catch (IOException | ReflectiveOperationException ignored) {
        }
    }

    private final class Wrapper implements ItemWrapper {
        private final ItemStack item;
        private final TagCompound value;

        Wrapper(ItemStack item) {
            this(item, read(item));
        }

        Wrapper(ItemStack item, TagCompound value) {
            this.item = item;
            this.value = value;
        }

        @Override public void save() { write(item, value); }
        @Override public Object get(String path) { return value.get(path); }
        @Override public Object set(String path, Object object) { return value.set(path, object); }
        @Override public java.util.Set<String> keySet(String path) { return value.keySet(path); }
        @Override public Wrapper getWrapper(String path) {
            Object child = value.get(path);
            return child instanceof java.util.Map ? new Wrapper(item, new TagCompound((java.util.Map<?, ?>) child)) : null;
        }
        @Override public Object getNBTBase(String path) { return value.get(path); }
        @Override public Object getHandle() { return value; }
        @Override public int size() { return value.size(); }
        @Override public void save(ItemStack itemStack) { write(itemStack, value); }
    }
}
