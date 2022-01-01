package github.saukiya.sxitem.util;

import github.saukiya.sxitem.nbt.NBTItemWrapper;
import github.saukiya.sxitem.nbt.NBTTagWrapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class ItemUtil_v1_8_R3 extends ItemUtil {

    @Override
    public boolean isUnbreakable(@Nonnull ItemMeta meta) {
        return meta.spigot().isUnbreakable();
    }

    @Override
    public void setUnbreakable(@Nonnull ItemMeta meta, boolean unbreakable) {
        meta.spigot().setUnbreakable(unbreakable);
    }

    @Override
    public String getSkull(ItemMeta meta) {
        if (meta instanceof SkullMeta && ((SkullMeta) meta).hasOwner()) return ((SkullMeta) meta).getOwner();
        return null;
    }

    @Override
    public void setSkull(ItemMeta meta, String value) {
        if (meta instanceof SkullMeta) ((SkullMeta) meta).setOwner(value);
    }

    @Override
    public List<AttributeData> getAttributes(ItemStack item) {
        NBTTagWrapper nbtTagWrapper = NbtUtil.getInst().getItemTagWrapper(item);
        List<Map<String, Object>> modifiers = (List<Map<String, Object>>) nbtTagWrapper.getList("AttributeModifiers");
        if (modifiers == null) return null;
        return modifiers.stream().map(map -> new AttributeData(
                (String) map.get("AttributeName"),
                new UUID((long) map.get("UUIDMost"), (long) map.get("UUIDLeast")),
                (String) map.get("Name"),
                (double) map.get("Amount"),
                (int) map.get("Operation"),
                (String) map.get("Slot")
        )).collect(Collectors.toList());
    }

    @Override
    public void addAttributes(ItemStack item, List<AttributeData> list) {
        NBTItemWrapper wrapper = NbtUtil.getInst().getItemTagWrapper(item);
        List<Object> modifiers = (List<Object>) wrapper.getList("AttributeModifiers", new ArrayList<>());
        list.forEach(data -> addAttribute(modifiers, data));
        wrapper.set("AttributeModifiers", modifiers);
        wrapper.save();
    }

    @Override
    public void addAttribute(ItemStack item, AttributeData data) {
        NBTItemWrapper wrapper = NbtUtil.getInst().getItemTagWrapper(item);
        List<Object> list = (List<Object>) wrapper.getList("AttributeModifiers", new ArrayList<>());
        addAttribute(list, data);
        wrapper.set("AttributeModifiers", list);
        wrapper.save();
    }

    public void addAttribute(List<Object> list, AttributeData data) {
        Map<String, Object> map = new HashMap<>();
        map.put("AttributeName", data.getAttrNameNBT());
        map.put("UUIDMost", data.getUniqueId().getMostSignificantBits());
        map.put("UUIDLeast", data.getUniqueId().getLeastSignificantBits());
        map.put("Name", data.getName());
        map.put("Amount", data.getAmount());
        map.put("Operation", data.getOperation());
        map.put("Slot", data.getSlotNBT());
        list.add(map);
    }
}
