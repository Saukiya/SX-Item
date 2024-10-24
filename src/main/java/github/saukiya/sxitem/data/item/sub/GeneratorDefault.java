package github.saukiya.sxitem.data.item.sub;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.expression.ExpressionSpace;
import github.saukiya.sxitem.data.item.IGenerator;
import github.saukiya.sxitem.data.item.IUpdate;
import github.saukiya.sxitem.data.item.ItemManager;
import github.saukiya.sxitem.data.random.INode;
import github.saukiya.util.nms.*;
import lombok.Getter;
import lombok.val;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Saukiya
 */
@Getter
public class GeneratorDefault extends IGenerator implements IUpdate {

    protected String displayName;

    protected List<String> ids;

    protected Map<String, INode> randomMap;

    protected Map<String, Object> nbt;

    protected Object component;

    protected int hashCode;

    protected boolean update;

    @SuppressWarnings("DataFlowIssue")
    public GeneratorDefault(String key, ConfigurationSection config, String group) {
        super(key, config, group);
        this.displayName = config.getString("Name");
        this.ids = config.isList("ID") ? config.getStringList("ID") : Collections.singletonList(config.getString(".ID", "APPLE"));
        if (config.isConfigurationSection("Random")) {
            SXItem.getRandomManager().loadRandom(this.randomMap = new HashMap<>(), config.getConfigurationSection("Random"));
        }
        if (config.isConfigurationSection("NBT")) {
            this.nbt = convertConfig(config.getConfigurationSection("NBT"));
        }
        if (config.isConfigurationSection("Components")) {
            component = ComponentUtil.getInst().valueToMap(convertConfig(config.getConfigurationSection("Components")));
        }
        this.hashCode = configString.hashCode();
        this.update = config.getBoolean("Update");
    }

    @Override
    public String getType() {
        return "Default";
    }

    @Override
    public String getName() {
        if (displayName != null) return ExpressionSpace.getInst().replace(displayName).replace('&', '§');
        return "§7" + String.join("§8|§7", ids);
    }

    @Override
    public BaseComponent getNameComponent() {
        if (displayName != null)
            return new TextComponent(ExpressionSpace.getInst().replace(displayName).replace('&', '§'));
        MessageUtil.Builder cb = MessageUtil.getInst().builder().add("§r");
        Material material = ItemManager.getMaterial(ExpressionSpace.getInst().replace(ids.get(0)));
        if (material != null) cb.add(material);
        else cb.add(ids.get(0));
        if (ids.size() > 1) cb.add("..");
        return cb.getHandle();
    }

    @Override
    protected ItemStack getItem(Player player, Object... args) {
        if (args.length > 0 && args[0] instanceof ExpressionSpace) {
            return getItem(player, (ExpressionSpace) args[0]);
        }
        ExpressionSpace space = new ExpressionSpace(player, randomMap);
        if (args.length > 0) {
            if (args[0] instanceof Map) {
                space.getOtherMap().putAll((Map<String, String>) args[0]);
            } else if (args[0] instanceof String) {
                for (int i = 1; i < args.length; i += 2) {
                    space.getOtherMap().put((String) args[i - 1], (String) args[i]);
                }
            }
        }
        return getItem(player, space);
    }

    @SuppressWarnings("deprecation")
    protected ItemStack getItem(Player player, ExpressionSpace expression) {
        String materialAndDurability = expression.replace(ids.get(SXItem.getRandom().nextInt(ids.size())));
        int indexOf = materialAndDurability.indexOf(':');
        val materialName = indexOf != -1 ? materialAndDurability.substring(0, indexOf) : materialAndDurability;
        Material material = ItemManager.getMaterial(materialName);
        if (material == null) {
            SXItem.getInst().getLogger().warning("Item-" + getKey() + " ID ERROR: " + materialName);
            return ItemManager.getEmptyItem();
        }
        ItemStack item = new ItemStack(material, Integer.parseInt(expression.replace(config.getString("Amount", "1"))));
        String durability = indexOf != -1 ? materialAndDurability.substring(indexOf + 1) : expression.replace(config.getString("Durability"));
        if (!StringUtils.isEmpty(durability)) {
            if (durability.charAt(durability.length() - 1) == '%') {
                durability = durability.substring(0, durability.length() - 1);
                item.setDurability((short) (material.getMaxDurability() * (1 - Double.parseDouble(durability) / 100)));
            } else if (durability.charAt(0) == '<') {
                durability = durability.substring(1);
                item.setDurability((short) (material.getMaxDurability() - Short.parseShort(durability)));
            } else {
                item.setDurability(Short.parseShort(durability));
            }
        }

        ItemMeta meta = item.getItemMeta();

        String itemName = expression.replace(this.displayName);
        if (itemName != null) {
            meta.setDisplayName(itemName.replace('&', '§'));
        }

        List<String> loreList = expression.replace(config.getStringList("Lore"));
        loreList.replaceAll(lore -> lore.replace('&', '§'));
        meta.setLore(loreList);

        for (String enchant : expression.replace(config.getStringList("EnchantList"))) {
            String[] enchantSplit = enchant.split(":");
            Enchantment enchantment = Enchantment.getByName(enchantSplit[0]);
            int level = Integer.parseInt(enchantSplit[1]);
            if (enchantment != null && level != 0) {
                meta.addEnchant(enchantment, level, true);
            }
        }

        for (String flagName : config.getStringList("ItemFlagList")) {
            for (ItemFlag itemFlag : ItemFlag.values()) {
                if (itemFlag.name().equals(flagName)) {
                    meta.addItemFlags(itemFlag);
                    break;
                }
            }
        }

        ItemUtil.getInst().setUnbreakable(meta, config.getBoolean("Unbreakable"));

        ItemUtil.getInst().setSkull(meta, expression.replace(config.getString("SkullName")));

        if (meta instanceof LeatherArmorMeta && config.isString("Color")) {
            ((LeatherArmorMeta) meta).setColor(Color.fromRGB(Integer.parseInt(expression.replace(config.getString("Color")), 16)));
        }

        // TODO 这玩意最好整合到ItemUtil里做NMS
        if (NMS.compareTo(1, 11, 1) >= 0 && config.isConfigurationSection("Potion") && meta instanceof PotionMeta) {
            ConfigurationSection potionConfig = config.getConfigurationSection("Potion");
            PotionEffectType[] potionEffectTypes = PotionEffectType.values();
            if (potionConfig != null) {
                for (String effectId : potionConfig.getKeys(false)) {
                    ConfigurationSection potionEffectConfig = potionConfig.getConfigurationSection(effectId);
                    effectId = expression.replace(effectId);
                    PotionEffectType effect = null;
                    for (PotionEffectType potionEffectType : potionEffectTypes) {
                        if (potionEffectType == null || potionEffectType.getName().equals(effectId)) continue;
                        effect = potionEffectType;
                    }
                    if (effect != null) {
                        int duration = Integer.parseInt(expression.replace(potionEffectConfig.getString("duration", "1")));
                        int amplifier = Integer.parseInt(expression.replace(potionEffectConfig.getString("amplifier", "1")));
                        boolean ambient = potionEffectConfig.getBoolean("ambient", true);
                        boolean particles = potionEffectConfig.getBoolean("particles", true);
                        PotionEffect potionEffect;
                        if (NMS.compareTo(1, 13, 2) >= 0) {
                            potionEffect = new PotionEffect(effect, duration, amplifier, ambient, particles, potionEffectConfig.getBoolean("icon", true));
                        } else {
                            potionEffect = new PotionEffect(effect, duration, amplifier, ambient, particles);
                        }
                        ((PotionMeta) meta).addCustomEffect(potionEffect, true);
                    }
                }
            }
        }

        if (NMS.compareTo(1, 14, 1) >= 0) {
            int customData = config.getInt("CustomModelData", -1);
            if (customData != -1) {
                meta.setCustomModelData(customData);
            }
        }

        item.setItemMeta(meta);

        if (config.getBoolean("ClearAttribute")) {
            ItemUtil.getInst().clearAttribute(item, meta);
        } else if (config.isList("Attributes")) {
            List<ItemUtil.AttributeData> attributeList = new ArrayList<>();
            for (String data : expression.replace(config.getStringList("Attributes"))) {
                String[] split = data.split(":");
                if (split.length >= 3) {
                    val attributeData = new ItemUtil.AttributeData()
                            .setAttrName(split[0])
                            .setAmount(Double.parseDouble(split[1]))
                            .setOperation(Integer.parseInt(split[2]))
                            .setSlot(split.length > 3 ? split[3] : null);
                    attributeList.add(attributeData);
                }
            }
            ItemUtil.getInst().addAttributes(item, attributeList);
        }

        Object nmsItem = NbtUtil.getInst().getNMSItem(item);

        if (component != null) {
            ComponentUtil.getInst().setDataComponentMap(nmsItem, component);
        }

        if (nbt != null || !expression.getLockMap().isEmpty()) {
            val wrapper = NbtUtil.getInst().getItemTagWrapper(item, nmsItem);
            wrapper.setAll((Map<Object, Object>) expression.replace(nbt));

            expression.getLockMap().forEach((key, value) -> wrapper.set(SXItem.getInst().getName() + ".Lock." + key, value));
            wrapper.save();
        }
        return item;
    }

    @Override
    public boolean isUpdate() {
        return update;
    }

    @Override
    public int updateCode() {
        return hashCode;
    }

    @Override
    public ItemStack update(ItemStack oldItem, NbtUtil.Wrapper oldWrapper, Player player) {
        ExpressionSpace space = new ExpressionSpace(player, randomMap);
        Map<String, String> map = (Map<String, String>) oldWrapper.getMap(SXItem.getInst().getName() + ".Lock");
        if (map != null) space.getOtherMap().putAll(map);
        return getItem(player, space);
    }

    public static void save(ItemStack item, ConfigurationSection config) {
        ItemMeta itemMeta = item.getItemMeta();
        config.set("Name", itemMeta.hasDisplayName() ? itemMeta.getDisplayName().replace('§', '&') : null);
        config.set("ID", item.getType().name() + (item.getDurability() != 0 ? ":" + item.getDurability() : ""));
        if (item.getAmount() > 1)
            config.set("Amount", item.getAmount());
        if (itemMeta.hasLore())
            config.set("Lore", itemMeta.getLore().stream().map(s -> s.replace('§', '&')).collect(Collectors.toList()));
        if (itemMeta.hasEnchants())
            config.set("EnchantList", itemMeta.getEnchants().entrySet().stream().map(entry -> entry.getKey().getName() + ":" + entry.getValue()).collect(Collectors.toList()));
        if (!itemMeta.getItemFlags().isEmpty())
            config.set("ItemFlagList", itemMeta.getItemFlags().stream().map(Enum::name).collect(Collectors.toList()));
        if (ItemUtil.getInst().isUnbreakable(itemMeta))
            config.set("Unbreakable", true);
        List<ItemUtil.AttributeData> attributeList = ItemUtil.getInst().getAttributes(item);
        if (attributeList != null && !attributeList.isEmpty())
            config.set("Attributes", attributeList.stream().map(data -> data.getAttrName() + ":" + data.getAmount() + ":" + data.getOperation() + (data.getSlot() != null ? ":" + data.getSlot() : "")).collect(Collectors.toList()));
        if (itemMeta instanceof LeatherArmorMeta)
            config.set("Color", Integer.toHexString(((LeatherArmorMeta) itemMeta).getColor().asRGB()));
        config.set("SkullName", ItemUtil.getInst().getSkull(itemMeta));
    }

    public static Map<String, Object> convertConfig(ConfigurationSection config) {
        Map<String, Object> result = config.getValues(false);
        result.entrySet().stream().filter(entry -> entry.getValue() instanceof ConfigurationSection).forEachOrdered(entry -> result.put(entry.getKey(), convertConfig((ConfigurationSection) entry.getValue())));
        return result;
    }
}
