package github.saukiya.util.nms;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Item扩展类，保证API全版本覆盖
 */
public abstract class ItemUtil implements NMS {

    @Getter
    private static final ItemUtil inst = NMS.getInst(ItemUtil.class, "v1_21_R1", "v1_13_R2", "v1_12_R1", "v1_11_R1", "v1_8_R3");

    /**
     * 获取物品是否为无限耐久
     *
     * @param meta
     * @return unbreakable
     */
    public abstract boolean isUnbreakable(@Nonnull ItemMeta meta);

    /**
     * 设置无限耐久
     *
     * @param meta        ItemMeta
     * @param unbreakable boolean
     */
    public abstract void setUnbreakable(@Nonnull ItemMeta meta, boolean unbreakable);

    /**
     * 获取头颅信息(优先获得UUID)
     *
     * @param meta SkullMeta
     * @return NameOrUUID
     */
    @Nullable
    public abstract String getSkull(ItemMeta meta);

    /**
     * 设置头颅信息
     * Name: 长度小于16
     * UUID: 长度小于36
     *
     * @param meta  SkullMeta
     * @param value NameOrUUID
     */
    public abstract void setSkull(ItemMeta meta, @Nullable String value);

    /**
     * 清除Attribute属性
     *
     * @param item ItemStack
     */
    public void clearAttribute(ItemStack item) {
        clearAttribute(item, item.getItemMeta());
    }

    /**
     * 清除Attribute属性 (当前实现为完整清除，1.12.1以下通过NBT清除) TODO 需验证
     *
     * @param item ItemStack
     * @param meta ItemMeta
     */
    public void clearAttribute(ItemStack item, ItemMeta meta) {
        if (meta == null) return;
        meta.setAttributeModifiers(ArrayListMultimap.create());
        item.setItemMeta(meta);
    }

    /**
     * 获取Attribute属性
     *
     * @param item ItemStack
     * @return AttributeMap[AttributeName, AttributeData]
     */
    public abstract List<AttributeData> getAttributes(ItemStack item);

    /**
     * 设置Attribute属性, 会覆盖原有的数据
     *
     * @param item ItemStack
     * @param list List[AttributeData]
     */
    public abstract void setAttributes(ItemStack item, @Nullable List<AttributeData> list);

    /**
     * 添加Attribute属性 TODO 优化1.21.1
     *
     * @param item ItemStack
     * @param list List[AttributeData]
     */
    public abstract void addAttributes(ItemStack item, @Nonnull List<AttributeData> list);

    /**
     * 添加Attribute属性
     * 成员参数不使用原有的Attribute、AttributeModifier，
     *
     * @param item ItemStack
     * @param data AttributeData
     */
    public abstract void addAttribute(ItemStack item, @Nonnull AttributeData data);

    @NoArgsConstructor
    @Accessors(chain = true)
    @Data
    public static class AttributeData {

        static BiMap<String, String> attrNameMap = HashBiMap.create();
        static BiMap<String, String> slotMap = HashBiMap.create();

        static {
            attrNameMap.put("GENERIC_MAX_HEALTH", "generic.maxHealth");
            attrNameMap.put("GENERIC_FOLLOW_RANGE", "generic.followRange");
            attrNameMap.put("GENERIC_KNOCKBACK_RESISTANCE", "generic.knockbackResistance");
            attrNameMap.put("GENERIC_MOVEMENT_SPEED", "generic.movementSpeed");
            attrNameMap.put("GENERIC_FLYING_SPEED", "generic.flyingSpeed");
            attrNameMap.put("GENERIC_ATTACK_DAMAGE", "generic.attackDamage");
            attrNameMap.put("GENERIC_ATTACK_KNOCKBACK", "generic.attackKnockback");
            attrNameMap.put("GENERIC_ATTACK_SPEED", "generic.attackSpeed");
            attrNameMap.put("GENERIC_ARMOR", "generic.armor");
            attrNameMap.put("GENERIC_ARMOR_TOUGHNESS", "generic.armorToughness");
            attrNameMap.put("GENERIC_LUCK", "generic.luck");
            attrNameMap.put("HORSE_JUMP_STRENGTH", "horse.jumpStrength");
            attrNameMap.put("ZOMBIE_SPAWN_REINFORCEMENTS", "zombie.spawnReinforcements");

            slotMap.put("HAND", "mainhand");
            slotMap.put("OFF_HAND", "offhand");
            slotMap.put("HEAD", "head");
            slotMap.put("CHEST", "chest");
            slotMap.put("LEGS", "legs");
            slotMap.put("FEET", "feet");
        }

        @Nonnull
        private String attrName;

        @Nullable
        private UUID uniqueId;

        @Nullable
        private String name;

        @Nonnull
        private double amount;

        @Nonnull
        private int operation;

        @Nullable
        private String slot;

        public AttributeData(String attrName, UUID uniqueId, String name, double amount, int operation, String slot) {
            setAttrName(attrName);
            setUniqueId(uniqueId);
            setName(name);
            setAmount(amount);
            setOperation(operation);
            setSlot(slot);
        }

        public UUID getUniqueId() {
            return uniqueId != null ? uniqueId : (uniqueId = UUID.randomUUID());
        }

        public String getName() {
            return name != null ? name : attrName;
        }

        public String getAttrNameNBT() {
            return attrNameMap.get(attrName);
        }

        public String getSlotNBT() {
            return slotMap.get(slot);
        }

        public AttributeData setAttrName(String attrName) {
            String temp = attrNameMap.inverse().get(attrName);
            this.attrName = temp != null ? temp : attrName;
            return this;
        }

        public AttributeData setSlot(String slot) {
            String temp = slotMap.inverse().get(slot);
            this.slot = temp != null ? temp : slot;
            return this;
        }
    }
}
