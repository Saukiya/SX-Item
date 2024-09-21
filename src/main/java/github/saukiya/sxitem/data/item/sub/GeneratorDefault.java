package github.saukiya.sxitem.data.item.sub;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.item.IGenerator;
import github.saukiya.sxitem.data.item.IUpdate;
import github.saukiya.sxitem.data.item.ItemManager;
import github.saukiya.sxitem.data.random.INode;
import github.saukiya.sxitem.data.random.RandomDocker;
import github.saukiya.sxitem.nbt.TagCompound;
import github.saukiya.sxitem.nbt.TagType;
import github.saukiya.sxitem.util.*;
import lombok.Getter;
import lombok.var;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Saukiya
 */
@Getter
public class GeneratorDefault extends IGenerator implements IUpdate {

    String displayName;

    List<String> ids;

    Map<String, INode> randomMap;

    TagCompound nbt;

    int hashCode;

    boolean update;

    public GeneratorDefault(String key, ConfigurationSection config, String group) {
        super(key, config, group);
        this.displayName = config.getString("Name");
        this.ids = config.isList("ID") ? config.getStringList("ID") : Collections.singletonList(config.getString(".ID", "APPLE"));
        if (config.contains("Random")) {
            SXItem.getRandomManager().loadRandom(this.randomMap = new HashMap<>(), config.getConfigurationSection("Random"));
        }
        if (config.contains("NBT")) {
            this.nbt = (TagCompound) TagType.toTag(config.getConfigurationSection("NBT"));
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
        if (displayName != null) return RandomDocker.getInst().replace(displayName).replace('&', '§');
        return "§7" + String.join("§8|§7", ids);
    }

    @Override
    public BaseComponent getNameComponent() {
        if (displayName != null)
            return new TextComponent(RandomDocker.getInst().replace(displayName).replace('&', '§'));
        MessageUtil.Builder cb = MessageUtil.getInst().builder().add("§r");
        for (String id : ids) {
            if (cb.getHandle().getExtra().size() != 1) cb.add("§8|§r");
            Material material = ItemManager.getMaterial(id);
            if (material != null) cb.add(material);
            else cb.add(id);
        }
        return cb.getHandle();
    }

    @Override
    protected ItemStack getItem(Player player, Object... args) {
        if (args.length > 0 && args[0] instanceof RandomDocker)
            return getItem(player, (RandomDocker) args[0]);
        RandomDocker randomDocker = new RandomDocker(randomMap, player);
        if (args.length > 0) {
            if (args[0] instanceof Map) {
                randomDocker.getOtherList().add((Map<String, String>) args[0]);
            } else if (args[0] instanceof String) {
                Map<String, String> map = new HashMap<>();
                for (int i = 1; i < args.length; i++, i++) map.put((String) args[i - 1], (String) args[i]);
                randomDocker.getOtherList().add(map);
            }
        }
        return getItem(player, randomDocker);
    }

    // TODO 干脆都整到ItemUtil里
    private ItemStack getItem(Player player, RandomDocker docker) {
        String[] materAndDur = docker.replace(ids.get(SXItem.getRandom().nextInt(ids.size()))).split(":");
        Material material = ItemManager.getMaterial(materAndDur[0]);
        if (material == null) {
            SXItem.getInst().getLogger().warning("Item-" + getKey() + " ID ERROR: " + materAndDur[0]);
            return ItemManager.getEmptyItem();
        }
        ItemStack item = new ItemStack(material, Integer.parseInt(docker.replace(config.getString("Amount", "1"))));
        String durability = materAndDur.length > 1 ? materAndDur[1] : docker.replace(config.getString("Durability"));
        if (durability != null) {
            if (durability.endsWith("%")) {
                item.setDurability((short) (material.getMaxDurability() * (1 - Double.parseDouble(durability.substring(0, durability.length() - 1)) / 100)));
            } else if (durability.startsWith("<")) {
                item.setDurability((short) (material.getMaxDurability() - Short.parseShort(durability.substring(1))));
            } else {
                item.setDurability(Short.parseShort(durability));
            }
        }
        ItemMeta meta = item.getItemMeta();

        String itemName = docker.replace(this.displayName);
        if (itemName != null) {
            meta.setDisplayName(itemName.replace('&', '§'));
        }

        List<String> loreList = docker.replace(config.getStringList("Lore"));
        loreList.replaceAll(lore -> lore.replace('&', '§'));
        meta.setLore(loreList);

        for (String enchant : docker.replace(config.getStringList("EnchantList"))) {
            String[] enchantSplit = enchant.split(":");
            Enchantment enchantment = Enchantment.getByName(enchantSplit[0]);
            int level = Integer.parseInt(enchantSplit[1]);
            if (enchantment != null && level != 0) {
                meta.addEnchant(enchantment, level, true);
            }
        }

        if (NMS.compareTo(1,14,1) >= 0) {
            int customData = config.getInt("CustomModelData", -1);
            if (customData != -1) {
                meta.setCustomModelData(customData);
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

        ItemUtil.getInst().setSkull(meta, docker.replace(config.getString("SkullName")));

        // TODO 这玩意最好整合到ItemUtil里做NMS
        if (config.isConfigurationSection("Potion") && NMS.compareTo(1, 11, 1) >= 0 && meta instanceof PotionMeta) {
            ConfigurationSection potionConfig = config.getConfigurationSection("Potion");
            PotionEffectType[] potionEffectTypes = PotionEffectType.values();
            if (potionConfig != null) {
                for (String effectId : potionConfig.getKeys(false)) {
                    ConfigurationSection potionEffectConfig = potionConfig.getConfigurationSection(effectId);
                    effectId = docker.replace(effectId);
                    PotionEffectType effect = null;
                    for (PotionEffectType potionEffectType : potionEffectTypes) {
                        if (!potionEffectType.getName().equals(effectId)) continue;
                        effect = potionEffectType;
                    }
                    if (effect != null) {
                        int duration = Integer.parseInt(docker.replace(potionEffectConfig.getString("duration", "1")));
                        int amplifier = Integer.parseInt(docker.replace(potionEffectConfig.getString("amplifier", "1")));
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

        if (meta instanceof LeatherArmorMeta && config.isString("Color")) {
            ((LeatherArmorMeta) meta).setColor(Color.fromRGB(Integer.parseInt(docker.replace(config.getString("Color")), 16)));
        }

        item.setItemMeta(meta);
        if (config.getBoolean("ClearAttribute")) {
            ItemUtil.getInst().clearAttribute(item, meta);
        } else {
            if (config.isList("Attributes")) {
                ItemUtil.getInst().addAttributes(item, docker.replace(config.getStringList("Attributes")).stream()
                        .map(data -> data.split(":")).filter(split -> split.length >= 3)
                        .map(split -> new ItemUtil.AttributeData()
                                .setAttrName(split[0])
                                .setAmount(Double.parseDouble(split[1]))
                                .setOperation(Integer.parseInt(split[2]))
                                .setSlot(split.length > 3 ? split[3] : null))
                        .collect(Collectors.toList())
                );
            }
        }

        Object nmsItem = NbtUtil.getInst().getNMSItem(item);

        if (config.isConfigurationSection("Components")) {
            Map<String, Object> components = new HashMap<>();
            convertConfig(components, config.getConfigurationSection("Components"));
            ComponentUtil.getInst().getItemWrapper(item, nmsItem).setAllValue(components).save();
        }

        var wrapper = NbtUtil.getInst().getItemTagWrapper(item, nmsItem);
        wrapper.setAll((Base.Compound) docker.replace(nbt));

        docker.getLockMap().forEach((key, value) -> wrapper.set(SXItem.getInst().getName() + ".Lock." + key, value));

        wrapper.save();

        return item;
    }

    public void convertConfig(Map<String, Object> map, ConfigurationSection config) {
        config.getValues(false).forEach((k,v) -> {
            if (v instanceof ConfigurationSection) {
                Map<String,Object> subComponents = new HashMap<>();
                convertConfig(subComponents, (ConfigurationSection) v);
                map.put(k, subComponents);
                return;
            }
            map.put(k, v);
        });
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
        RandomDocker randomDocker = new RandomDocker(randomMap, player);
        Map<String, String> map = (Map<String, String>) oldWrapper.getMap(SXItem.getInst().getName() + ".Lock");
        if (map != null) randomDocker.getOtherList().add(map);
        return getItem(player, randomDocker);
    }

    public static Saver saveFunc() {
        return (item, config) -> {
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
        };
    }
}
