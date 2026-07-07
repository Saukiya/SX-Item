package github.saukiya.sxitem.data.item;

import github.saukiya.tools.nms.ComponentUtil;
import github.saukiya.tools.nms.NMS;
import github.saukiya.tools.nms.NbtUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用于表示这个 IGenerator 适合更新
 */
public interface IUpdate {

    /**
     * 组件保护路径前缀. 以此开头的 ProtectNBT 条目将保护原版顶级组件(DataComponent),
     * 而非 custom_data 内部的 NBT 路径. 例: components.minecraft:damage
     */
    String COMPONENT_PREFIX = "components.";

    /**
     * 组件保护所需的最低版本 (1.20.5, 组件格式引入版本)
     */
    String COMPONENT_MIN_VERSION = "v1_20_R4";

    String getKey();

    ConfigurationSection getConfig();

    boolean isUpdate();

    int updateCode();

    ItemStack update(ItemStack oldItem, NbtUtil.Wrapper oldWrapper, Player player);

    /**
     * 合并全局与单物品的保护列表
     * <pre>
     * 规则: 全局列表 ∪ 单物品 ProtectNBT; 单物品条目以 "!" 开头则从结果中移除对应路径
     * </pre>
     *
     * @param globalProtectNBT 全局保护配置
     * @return 合并后的有效保护路径集合
     */
    default HashSet<String> mergeProtectList(HashSet<String> globalProtectNBT) {
        HashSet<String> protectNBT = new HashSet<>(globalProtectNBT);
        getConfig().getStringList("ProtectNBT").forEach(nbt -> {
            if (nbt.startsWith("!")) {
                protectNBT.remove(nbt.substring(1));
                return;
            }
            protectNBT.add(nbt);
        });
        return protectNBT;
    }

    /**
     * 保护自定义NBT标签 (custom_data 组件内部)
     * <pre>{@code
     * val newWrapper = NbtUtil.getInst().getItemTagWrapper(newItem);
     * val oldWrapper = NbtUtil.getInst().getItemTagWrapper(oldItem);
     * protectNBT(newWrapper, oldWrapper, globalProtectNBT);
     * }</pre>
     * 以 {@link #COMPONENT_PREFIX} 开头的条目由 {@link #protectComponents} 处理, 此处跳过.
     *
     * @param newWrapper       更新后的物品NBT封装
     * @param oldWrapper       更新前的物品NBT封装
     * @param globalProtectNBT 全局保护配置
     */
    default void protectNBT(NbtUtil.ItemWrapper newWrapper, NbtUtil.Wrapper oldWrapper, HashSet<String> globalProtectNBT) {
        mergeProtectList(globalProtectNBT).forEach(nbt -> {
            if (nbt.startsWith(COMPONENT_PREFIX)) return;
            newWrapper.set(nbt, oldWrapper.getNBTBase(nbt));
        });
        newWrapper.save();
    }

    /**
     * 保护原版顶级组件 (DataComponent). 处理 ProtectNBT 中以 {@link #COMPONENT_PREFIX} 开头的条目,
     * 将旧物品对应组件整块拷贝到新物品. 例: components.minecraft:damage、components.minecraft:enchantments.
     * <pre>
     * 说明:
     * - 仅 1.20.5+ 生效, 低版本无组件格式直接跳过.
     * - 组件粒度为整块拷贝, 前缀后取第一段作为组件ID(组件ID含 ":" 不含 ".").
     * </pre>
     *
     * @param newItem          更新后的物品
     * @param oldItem          更新前的物品
     * @param globalProtectNBT 全局保护配置
     */
    default void protectComponents(ItemStack newItem, ItemStack oldItem, HashSet<String> globalProtectNBT) {
        if (NMS.compareTo(COMPONENT_MIN_VERSION) < 0) return;
        Set<String> ids = mergeProtectList(globalProtectNBT).stream()
                .filter(path -> path.startsWith(COMPONENT_PREFIX))
                .map(path -> {
                    String id = path.substring(COMPONENT_PREFIX.length());
                    int dot = id.indexOf('.');
                    return dot == -1 ? id : id.substring(0, dot);
                })
                .filter(id -> !id.isEmpty())
                .collect(Collectors.toSet());
        if (ids.isEmpty()) return;
        ComponentUtil cu = ComponentUtil.getInst();
        Object oldMap = cu.getDataComponentMap(cu.getNMSItem(oldItem));
        ComponentUtil.ItemWrapper wrapper = cu.getItemWrapper(newItem);
        for (String id : ids) {
            Object value = cu.getComponentMapValue(oldMap, id);
            if (value != null) wrapper.set(id, value);
        }
        wrapper.save();
    }
}
