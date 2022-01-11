package github.saukiya.sxitem.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 实现1.13+的Attribute方法
 */
public class ItemUtil_v1_13_R2 extends ItemUtil {

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
        if (meta instanceof SkullMeta && ((SkullMeta) meta).hasOwner()) {
            return ((SkullMeta) meta).getOwningPlayer().getUniqueId().toString();
        }
        return null;
    }

    @Override
    public void setSkull(ItemMeta meta, @Nullable String value) {
        if (meta instanceof SkullMeta) {
            if (value == null) {
                ((SkullMeta) meta).setOwningPlayer(null);
            } else if (value.length() <= 16) {
                ((SkullMeta) meta).setOwner(value);
            } else if (value.length() <= 36) {
                ((SkullMeta) meta).setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(value)));
            }
        }
    }

    @Override
    public List<AttributeData> getAttributes(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasAttributeModifiers()) return null;
        List<AttributeData> list = new ArrayList<>();
        meta.getAttributeModifiers().forEach(
                (attribute, data) -> list.add(new AttributeData(
                        attribute.name(),
                        data.getUniqueId(),
                        data.getName(),
                        data.getAmount(),
                        data.getOperation().ordinal(),
                        data.getSlot() != null ? data.getSlot().name() : null
                ))
        );
        return list;
    }

    @Override
    public void setAttributes(ItemStack item, List<AttributeData> list) {
        ItemMeta meta = item.getItemMeta();
        Multimap<Attribute, AttributeModifier> map = null;
        if (list != null && !list.isEmpty()) {
            map = HashMultimap.create();
            Attribute attribute;
            for (AttributeData data : list) {
                if ((attribute = getAttribute(data)) == null) continue;
                map.put(attribute, getAttributeModifier(data));
            }
        }
        meta.setAttributeModifiers(map);
    }

    @Override
    public void addAttributes(ItemStack item, List<AttributeData> list) {
        ItemMeta meta = item.getItemMeta();
        Attribute attribute;
        for (AttributeData data : list) {
            if ((attribute = getAttribute(data)) == null) continue;
            meta.addAttributeModifier(attribute, getAttributeModifier(data));
        }
        item.setItemMeta(meta);
    }

    @Override
    public void addAttribute(ItemStack item, AttributeData data) {
        ItemMeta meta = item.getItemMeta();
        Attribute attribute = getAttribute(data);
        if (attribute == null) return;
        meta.addAttributeModifier(attribute, getAttributeModifier(data));
        item.setItemMeta(meta);
    }


    public Attribute getAttribute(AttributeData data) {
        return Arrays.stream(Attribute.values()).filter(attr -> attr.name().equals(data.getAttrName())).findFirst().orElse(null);
    }

    public AttributeModifier getAttributeModifier(AttributeData data) {
        return new AttributeModifier(
                data.getUniqueId(),
                data.getName(),
                data.getAmount(),
                AttributeModifier.Operation.values()[data.getOperation()],
                Arrays.stream(EquipmentSlot.values()).filter(s -> s.name().equals(data.getSlot())).findFirst().orElse(null)
        );
    }
}
