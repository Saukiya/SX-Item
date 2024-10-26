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

/**
 * @see github.saukiya.tools.nms.ItemUtil
 * @deprecated
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemUtil {

    @Getter
    private static final ItemUtil inst = new ItemUtil(github.saukiya.tools.nms.ItemUtil.getInst());

    @Delegate
    private github.saukiya.tools.nms.ItemUtil target;

    @Deprecated
    public List<AttributeData> getAttributes(ItemStack item) {
        return target.getAttributes(item).stream().map(AttributeData::new).collect(Collectors.toList());
    }

    @Deprecated
    public void setAttributes(ItemStack item, @Nullable List<AttributeData> list) {
        if (list == null) {
            target.setAttributes(item, null);
            return;
        }
        target.setAttributes(item, list.stream().map(x -> x.target).collect(Collectors.toList()));
    }

    @Deprecated
    public void addAttributes(ItemStack item, @Nonnull List<AttributeData> list) {
        target.addAttributes(item, list.stream().map(x -> x.target).collect(Collectors.toList()));
    }

    @Deprecated
    public void addAttribute(ItemStack item, @Nonnull AttributeData data) {
        target.addAttribute(item, data.target);
    }

    @Deprecated
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AttributeData {

        @Delegate
        github.saukiya.tools.nms.ItemUtil.AttributeData target;

        @Deprecated
        public AttributeData() {
            target = new github.saukiya.tools.nms.ItemUtil.AttributeData();
        }

        @Deprecated
        public AttributeData(String attrName, UUID uniqueId, String name, double amount, int operation, String slot) {
            target = new github.saukiya.tools.nms.ItemUtil.AttributeData(attrName, uniqueId, name, amount, operation, slot);
            target.setAttrName(attrName);
            target.setUniqueId(uniqueId);
            target.setName(name);
            target.setAmount(amount);
            target.setOperation(operation);
            target.setSlot(slot);
        }

        @Deprecated
        public AttributeData setAttrName(String attrName) {
            target.setAttrName(attrName);
            return this;
        }

        @Deprecated
        public AttributeData setSlot(String slot) {
            target.setSlot(slot);
            return this;
        }
    }
}
