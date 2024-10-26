package github.saukiya.sxitem.data.item;

import github.saukiya.tools.nms.NbtUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;

/**
 * 用于表示这个 IGenerator 适合更新
 */
public interface IUpdate {

    String getKey();

    ConfigurationSection getConfig();

    boolean isUpdate();

    int updateCode();

    ItemStack update(ItemStack oldItem, NbtUtil.Wrapper oldWrapper, Player player);


    /**
     * 保护自定义NBT标签
     * <pre>{@code
     * val newWrapper = NbtUtil.getInst().getItemTagWrapper(newItem);
     * val oldWrapper = NbtUtil.getInst().getItemTagWrapper(oldItem);
     * protectNBT(newWrapper, oldWrapper, globalProtectNBT);
     * }</pre>
     *
     * @param newWrapper       更新后的物品NBT封装
     * @param oldWrapper       更新前的物品NBT封装
     * @param globalProtectNBT 全局保护配置
     */
    default void protectNBT(NbtUtil.ItemWrapper newWrapper, NbtUtil.Wrapper oldWrapper, HashSet<String> globalProtectNBT) {
        HashSet<String> protectNBT = new HashSet<>(globalProtectNBT);
        getConfig().getStringList("ProtectNBT").forEach(nbt -> {
            if (nbt.startsWith("!")) {
                protectNBT.remove(nbt.substring(1));
                return;
            }
            protectNBT.add(nbt);
        });
        protectNBT.forEach(nbt -> newWrapper.set(nbt, oldWrapper.getNBTBase(nbt)));
        newWrapper.save();
    }
}
