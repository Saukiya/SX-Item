package github.saukiya.sxitem.util;

import lombok.Getter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Saukiya
 */

@Getter
public class NbtUtil {

    /**
     * 获取全部NBT数据
     *
     * @param item ItemStack
     * @return String
     */
    public String getAllNBT(ItemStack item) {
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        if (nmsItem != null) {
            NBTTagCompound itemTag = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
            return "§c[" + item.getType().name() + ":" + item.getDurability() + "-" + item.hashCode() + "]§7 " + itemTag.toString().replace("§", "&");
        }
        return "§c[" + item.getType().name() + ":" + item.getDurability() + "-" + item.hashCode() + "]§7 §cNULL";
    }

    /**
     * 设置物品NBT数据
     *
     * @param item  ItemStack
     * @param key   String
     * @param value String
     * @return ItemStack
     */
    public ItemStack setNBT(ItemStack item, String key, Object value) {
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        if (nmsItem != null) {
            NBTTagCompound itemTag = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
//            NBTTagString tagString = new NBTTagString(value.toString());
//            itemTag.set(key, tagString);
            nmsItem.setTag(itemTag);
            item.setItemMeta((CraftItemStack.asBukkitCopy(nmsItem)).getItemMeta());
        }
        return item;
    }

    /**
     * 设置物品NBT数据 List 会被设置ItemMeta
     *
     * @param item ItemStack
     * @param key  String
     * @param list List
     * @return ItemStack
     */
    public ItemStack setNBTList(ItemStack item, String key, List<String> list) {
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        if (nmsItem != null) {
            NBTTagCompound itemTag = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
            NBTTagList tagList = new NBTTagList();
            for (int i = 0; i < list.size(); i++) {
//                tagList.add(i, new NBTTagString(list.get(i)));
            }
            itemTag.set(key, tagList);
            nmsItem.setTag(itemTag);
            item.setItemMeta((CraftItemStack.asBukkitCopy(nmsItem)).getItemMeta());
        }
        return item;
    }

    /**
     * 获取物品NBT数据
     *
     * @param item ItemStack
     * @param key  String
     * @return String
     */
    public String getNBT(ItemStack item, String key) {
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        if (nmsItem != null) {
            NBTTagCompound itemTag = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
            if (itemTag.hasKey(key)) {
                return itemTag.getString(key);
            }
        }
        return null;
    }


    /**
     * 设置物品NBT数据 List
     *
     * @param item ItemStack
     * @param key  String
     * @return List
     */
    public List<String> getNBTList(ItemStack item, String key) {
        List<String> list = new ArrayList<>();
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        if (nmsItem != null) {
            NBTTagCompound itemTag = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
            NBTTagList tagList = itemTag.hasKey(key) ? itemTag.getList(key, 8) : new NBTTagList();
            for (int i = 0; i < tagList.size(); i++) {
                list.add(tagList.getString(i));
            }
        }
        return list;
    }

    /**
     * 判断是否有物品NBT数据
     *
     * @param item ItemStack
     * @param key  String
     * @return Boolean
     */
    public boolean hasNBT(ItemStack item, String key) {
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        if (nmsItem != null) {
            NBTTagCompound itemTag = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
            return itemTag.hasKey(key);
        }
        return false;
    }


    /**
     * 清除指定nbt
     *
     * @param item ItemStack
     * @param key  String
     * @return boolean
     */
    public boolean removeNBT(ItemStack item, String key) {
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        if (nmsItem != null) {
            NBTTagCompound itemTag = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
            if (itemTag.hasKey(key)) {
                itemTag.remove(key);
                nmsItem.setTag(itemTag);
                item.setItemMeta((CraftItemStack.asBukkitCopy(nmsItem)).getItemMeta());
            }
            return true;
        }
        return false;
    }
}
