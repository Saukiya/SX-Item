package github.saukiya.tools.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * 已废弃的材质兼容入口，仅用于保持旧版 SX-Item 扩展的静态调用兼容。
 *
 * <p>新代码必须直接使用 {@link XMaterial}；该门面不再维护独立材质枚举，
 * 避免 XMaterial 与旧映射表产生两套不一致的数据源。</p>
 */
@Deprecated
public final class ReMaterial {

    private ReMaterial() {
    }

    /**
     * @deprecated 使用 {@link XMaterial#getMaterial(String)}。
     */
    @Deprecated
    @Nullable
    public static Material getMaterial(String key) {
        return XMaterial.getMaterial(key);
    }

    /**
     * @deprecated 使用 {@link XMaterial#getItem(String)}。
     */
    @Deprecated
    @Nullable
    public static ItemStack getItem(String key) {
        return XMaterial.getItem(key);
    }

    /**
     * @deprecated 使用 {@link XMaterial#has(String)}。
     */
    @Deprecated
    public static boolean has(String key) {
        return XMaterial.has(key);
    }

    /**
     * @deprecated 使用 {@link XMaterial#getKey(Material)}。
     */
    @Deprecated
    public static String getKey(Material material) {
        return XMaterial.getKey(material);
    }

    /**
     * @deprecated 使用 {@link XMaterial#getKeys()}。
     */
    @Deprecated
    public static Set<String> getKeys() {
        return XMaterial.getKeys();
    }

    /**
     * 保留旧初始化调用的源码兼容，但返回值类型改为实际维护的 XMaterial 枚举。
     *
     * @deprecated 使用 {@link XMaterial#values()}。
     */
    @Deprecated
    public static XMaterial[] values() {
        return XMaterial.values();
    }

    /**
     * @deprecated 使用 {@link XMaterial#valueOf(String)}。
     */
    @Deprecated
    public static XMaterial valueOf(String name) {
        return XMaterial.valueOf(name);
    }
}
