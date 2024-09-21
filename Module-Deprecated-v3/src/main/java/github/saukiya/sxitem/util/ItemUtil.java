package github.saukiya.sxitem.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Delegate;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Deprecated
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemUtil {

    @Getter
    private static final ItemUtil inst = new ItemUtil(github.saukiya.util.nms.ItemUtil.getInst());

    @Delegate
    private github.saukiya.util.nms.ItemUtil target;

    public List<AttributeData> getAttributes(ItemStack item) {
        return target.getAttributes(item).stream().map(AttributeData::new).collect(Collectors.toList());
    }

    public void setAttributes(ItemStack item, @Nullable List<AttributeData> list) {
        if (list == null) {
            target.setAttributes(item, null);
            return;
        }
        target.setAttributes(item, list.stream().map(x -> x.target).collect(Collectors.toList()));
    }

    public void addAttributes(ItemStack item, @Nonnull List<AttributeData> list) {
        target.addAttributes(item, list.stream().map(x -> x.target).collect(Collectors.toList()));
    }

    public void addAttribute(ItemStack item, @Nonnull AttributeData data) {
        target.addAttribute(item, data.target);
    }

    @Deprecated
    @AllArgsConstructor
    public static class AttributeData {

        @Delegate
        github.saukiya.util.nms.ItemUtil.AttributeData target;

        public AttributeData(String attrName, UUID uniqueId, String name, double amount, int operation, String slot) {
            target = new github.saukiya.util.nms.ItemUtil.AttributeData(attrName, uniqueId, name, amount, operation, slot);
            target.setAttrName(attrName);
            target.setUniqueId(uniqueId);
            target.setName(name);
            target.setAmount(amount);
            target.setOperation(operation);
            target.setSlot(slot);
        }

        public AttributeData setAttrName(String attrName) {
            target.setAttrName(attrName);
            return this;
        }

        public AttributeData setSlot(String slot) {
            target.setSlot(slot);
            return this;
        }
    }
}
