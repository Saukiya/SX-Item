package github.saukiya.sxitem.util;

import lombok.Getter;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Saukiya
 */

@Getter
public class NbtUtilDeprecated {

    /**
     * 获取全部NBT数据
     *
     * @param item ItemStack
     * @return String
     */
    public String getAllNBT(ItemStack item) {
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        String base = "§c[" + item.getType().name() + ":" + item.getDurability() + "-" + item.hashCode() + "]§7 ";
        if (nmsItem != null && nmsItem.hasTag()) {
            return base + nmsItem.getTag().toString().replace("§", "&");
        }
        return base;
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
            NBTTagCompound itemTag = nmsItem.getOrCreateTag();
            itemTag.set(key, NBTTagString.a(value.toString()));
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
            NBTTagCompound itemTag = nmsItem.getOrCreateTag();
            NBTTagList tagList = new NBTTagList();
            for (int i = 0; i < list.size(); i++) {
                tagList.add(i, NBTTagString.a(list.get(i)));
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
            NBTTagCompound itemTag = nmsItem.getTag();
            if (itemTag != null && itemTag.hasKey(key)) {
                return itemTag.getString(key);
            }
        }
        return null;
    }


    /**
     * 获取物品NBT数据 List
     *
     * @param item ItemStack
     * @param key  String
     * @return List
     */
    public List<String> getNBTList(ItemStack item, String key) {
        List<String> list = new ArrayList<>();
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        if (nmsItem != null) {
            NBTTagCompound itemTag = nmsItem.getTag();
            if (itemTag != null && itemTag.hasKey(key)) {
                NBTTagList tagList = itemTag.getList(key, 8);
                for (NBTBase nbtBase : tagList) {
                    list.add(nbtBase.asString());
                }
//                for (int i = 0; i < tagList.size(); i++) {
//                    list.add(tagList.getString(i));
//                }
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
        return nmsItem != null && nmsItem.hasTag() && nmsItem.getTag().hasKey(key);
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
            nmsItem.removeTag(key);
            item.setItemMeta((CraftItemStack.asBukkitCopy(nmsItem)).getItemMeta());
            return true;

//            NBTTagCompound itemTag = nmsItem.getTag();
//            if (itemTag != null && itemTag.hasKey(key)) {
//                itemTag.remove(key);
//                nmsItem.setTag(itemTag);
//                item.setItemMeta((CraftItemStack.asBukkitCopy(nmsItem)).getItemMeta());
//                return true;
//            }
        }
        return false;
    }
}
