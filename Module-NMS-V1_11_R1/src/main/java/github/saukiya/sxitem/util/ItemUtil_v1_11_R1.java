package github.saukiya.sxitem.util;

import lombok.var;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ItemUtil_v1_11_R1 extends ItemUtil {

    public void editAttribute(ItemStack item, Consumer<List<Object>> computeListFunction) {
        editAttribute(item, wrapper -> (List<Object>) wrapper.getList("AttributeModifiers", new ArrayList<>()), computeListFunction);
    }

    public void editAttribute(ItemStack item, Function<NbtUtil.Wrapper, List<Object>> toListFunction, Consumer<List<Object>> computeListFunction) {
        var wrapper = NbtUtil.getInst().getItemTagWrapper(item);
        List<Object> modifiers = toListFunction.apply(wrapper);
        computeListFunction.accept(modifiers);
        wrapper.set("AttributeModifiers", modifiers == null || modifiers.isEmpty() ? null : modifiers);
        wrapper.save();
    }

    public void addAttribute(List<Object> list, AttributeData data) {
        if (data.getAttrNameNBT() == null) return;
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

    @Override
    public boolean isUnbreakable(@Nonnull ItemMeta meta) {
        return meta.isUnbreakable();
    }

    @Override
    public void setUnbreakable(@Nonnull ItemMeta meta, boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
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
        return ((List<Map<String, Object>>) NbtUtil.getInst().getItemTagWrapper(item).getList("AttributeModifiers", new ArrayList<>())).stream().map(
                map -> new AttributeData(
                        (String) map.get("AttributeName"),
                        new UUID((long) map.get("UUIDMost"), (long) map.get("UUIDLeast")),
                        (String) map.get("Name"),
                        (double) map.get("Amount"),
                        (int) map.get("Operation"),
                        (String) map.get("Slot")
                )
        ).collect(Collectors.toList());
    }

    @Override
    public void setAttributes(ItemStack item, List<AttributeData> list) {
        editAttribute(item, wrapper -> new ArrayList<>(), modifiers -> {
            if (list != null) {
                list.forEach(data -> addAttribute(modifiers, data));
            }
        });
    }

    @Override
    public void addAttributes(ItemStack item, List<AttributeData> list) {
        editAttribute(item, modifiers -> list.forEach(data -> addAttribute(modifiers, data)));
    }

    @Override
    public void addAttribute(ItemStack item, AttributeData data) {
        editAttribute(item, modifiers -> addAttribute(modifiers, data));
    }
}
