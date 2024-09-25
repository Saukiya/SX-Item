package github.saukiya.util.nms;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * <pre>
 * 组件工具类 (v1_20_R4+)
 *
 * 组件结构:
 * DataComponentMap->Component
 *
 * 参考链接: <a href="https://zh.minecraft.wiki/w/%E7%89%A9%E5%93%81%E5%A0%86%E5%8F%A0%E7%BB%84%E4%BB%B6">WIKI</a>
 * WIKI: <a href="https://minecraft.wiki/w/Data_component_format#food">WIKI</a>
 * </pre>
 */
public abstract class ComponentUtil implements NMS {

    @Getter
    private final static ComponentUtil inst = NMS.getInst(ComponentUtil.class, "v1_21_R1", "v1_8_R3");

    @Getter
    private final static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * 返回一个 ItemComponent 封装器
     **/
    public final ItemWrapper getItemWrapper(ItemStack itemStack) {
        return new ItemWrapper(itemStack);
    }

    /**
     * 返回一个 ItemComponent 封装器 (多一个nmsItem参数, 节约一次Copy资源)
     **/
    public final ItemWrapper getItemWrapper(ItemStack itemStack, Object nmsItem) {
        return new ItemWrapper(itemStack, nmsItem);
    }

    public final Object getNMSItem(ItemStack itemStack) {
        return NbtUtil.getInst().getNMSItem(itemStack);
    }

    public final void setNMSItem(ItemStack itemStack, Object nmsItem) {
        NbtUtil.getInst().setNMSItem(itemStack, nmsItem);
    }

    /**
     * 获取 nmsItem 中的 DataComponentMap
     * 覆盖类型 DataComponentPatch 主要用这个
     *
     * @param nmsItem NMSItem
     * @return 如果不存在 则会新建一个 DataComponentMap
     */
    public abstract Object getDataComponentMap(Object nmsItem);

    public abstract void setDataComponentMap(Object nmsItem, Object dataComponentMap);

    public abstract JsonElement mapToJson(Object dataComponentMap);

    public abstract Object jsonToMap(JsonElement jsonElement);

    public abstract Object mapToValue(Object dataComponentMap);

    public abstract Object valueToMap(Object javaObject);

    public abstract void setComponentMapValue(Object dataComponentMap, String type, Object value);

    /**
     * 获取 nmsItem 中的 DataComponentPatch
     * 替代类型 DataComponentPatch 目前作用不明显
     *
     * @param nmsItem NMSItem
     * @return 返回一个重新封装的 DataComponentPatch
     */
    public abstract Object getDataComponentPatch(Object nmsItem);

    public abstract void setDataComponentPatch(Object nmsItem, Object dataComponentPatch);

    public abstract JsonElement patchToJson(Object dataComponentPatch);

    public abstract Object jsonToPatch(JsonElement jsonElement);

    public abstract Object patchToValue(Object dataComponentPatch);

    public abstract Object valueToPatch(Object javaObject);

    public abstract List<String> getItemKeys();

    @Getter
    @RequiredArgsConstructor
    public class ItemWrapper {

        final ItemStack bukkitItem;
        final Object nmsItem;
        Object handle;

        public ItemWrapper(ItemStack item) {
            this(item, getNMSItem(item));
        }

        /**
         * 向dataMap中添加组件元素
         */
        public ItemWrapper set(String type, Object value) {
            Object dataMap = getDataComponentMap(nmsItem);
            setComponentMapValue(dataMap, type, value);
            setDataComponentMap(nmsItem, dataMap);
            return this;
        }

        /**
         * 设置相关组件
         */
        public ItemWrapper setMap(Object dataComponentMap) {
            setDataComponentMap(nmsItem, dataComponentMap);
            return this;
        }

        /**
         * 从Java对象中设置相关组件
         **/
        public ItemWrapper setFromValue(Object javaObject) {
            return setMap(valueToMap(javaObject));
        }

        /**
         * 从Json对象中设置相关组件
         **/
        public ItemWrapper setFromJson(JsonElement jsonElement) {
            return setMap(jsonToMap(jsonElement));
        }

        /**
         * 从Json字符串中设置相关组件
         **/
        public ItemWrapper setFromJson(String jsonString) {
            return setMap(jsonToMap(JsonParser.parseString(jsonString)));
        }

        /**
         * 保存到BukkitItem
         **/
        public void save() {
            setNMSItem(bukkitItem, nmsItem);
        }

        /**
         * 输出Java值
         **/
        public Object toValue() {
            return mapToValue(getDataComponentMap(nmsItem));
        }

        /**
         * 输出Json值
         **/
        public JsonElement toJson() {
            return mapToJson(getDataComponentMap(nmsItem));
        }

        /**
         * 输出Json字符串
         */
        public String toJsonString() {
            JsonElement json = toJson();
            if (json == null) return null;
            return gson.toJson(json);
        }
    }
}
