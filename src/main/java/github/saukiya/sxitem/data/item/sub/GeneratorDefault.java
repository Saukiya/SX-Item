package github.saukiya.sxitem.data.item.sub;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.item.IGenerator;
import github.saukiya.sxitem.data.item.IUpdate;
import github.saukiya.sxitem.data.item.ItemManager;
import github.saukiya.sxitem.data.random.INode;
import github.saukiya.sxitem.data.random.RandomDocker;
import github.saukiya.sxitem.helper.PlaceholderHelper;
import github.saukiya.sxitem.nbt.*;
import github.saukiya.sxitem.util.ComponentBuilder;
import github.saukiya.sxitem.util.ItemUtil;
import github.saukiya.sxitem.util.MessageUtil;
import github.saukiya.sxitem.util.NbtUtil;
import lombok.Getter;
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

import java.util.*;
import java.util.function.BiConsumer;
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

    public GeneratorDefault(String key, ConfigurationSection config) {
        super(key, config);
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
        if (displayName != null) return RandomDocker.getInst().replace(displayName).replace("&", "§");
        return "§7" + String.join("§8|§7", ids);
    }

    @Override
    public BaseComponent getNameComponent() {
        if (displayName != null)
            return new TextComponent(RandomDocker.getInst().replace(displayName).replace("&", "§"));
        ComponentBuilder cb = MessageUtil.getInst().componentBuilder().add("§r");
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

        String itemName = docker.replace(PlaceholderHelper.setPlaceholders(player, this.displayName));
        if (itemName != null) {
            meta.setDisplayName(itemName.replace("&", "§"));
        }

        List<String> loreList = docker.replace(PlaceholderHelper.setPlaceholders(player, config.getStringList("Lore")));
        loreList.replaceAll(lore -> lore.replace("&", "§"));
        meta.setLore(loreList);


        for (String enchant : docker.replace(config.getStringList("EnchantList"))) {
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

        ItemUtil.getInst().setSkull(meta, docker.replace(PlaceholderHelper.setPlaceholders(player, config.getString("SkullName"))));

        if (meta instanceof LeatherArmorMeta && config.isString("Color")) {
            ((LeatherArmorMeta) meta).setColor(Color.fromRGB(Integer.parseInt(docker.replace(config.getString("Color")), 16)));
        }

        if (config.getBoolean("ClearAttribute")) {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
            ItemUtil.getInst().addAttribute(item, new ItemUtil.AttributeData().setAttrName("GENERIC_ATTACK_DAMAGE").setAmount(0));
        } else {
            item.setItemMeta(meta);
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

        NBTItemWrapper wrapper = NbtUtil.getInst().getItemTagWrapper(item);
        wrapper.setAll((TagCompoundBase) docker.replace(nbt));

        docker.getLockMap().forEach((key, value) -> wrapper.set(SXItem.getInst().getName() + ".Lock." + key, value));

        wrapper.save();
        return item;
    }

    @Override
    public ItemStack update(ItemStack oldItem, NBTTagWrapper oldWrapper, Player player) {
        RandomDocker randomDocker = new RandomDocker(randomMap, player);
        Map<String, String> map = (Map<String, String>) oldWrapper.getMap(SXItem.getInst().getName() + ".Lock");
        if (map != null) randomDocker.getOtherList().add(map);
        return getItem(player, randomDocker);
    }

    @Override
    public int updateCode() {
        return hashCode;
    }

    @Override
    public boolean isUpdate() {
        return update;
    }

    public static BiConsumer<ItemStack, ConfigurationSection> saveFunc() {
        return (item, config) -> {
            ItemMeta itemMeta = item.getItemMeta();
            config.set("Name", itemMeta.hasDisplayName() ? itemMeta.getDisplayName().replace("§", "&") : null);
            config.set("ID", item.getType().name() + (item.getDurability() != 0 ? ":" + item.getDurability() : ""));
            if (item.getAmount() > 1)
                config.set("Amount", item.getAmount());
            if (itemMeta.hasLore())
                config.set("Lore", itemMeta.getLore().stream().map(s -> s.replace("§", "&")).collect(Collectors.toList()));
            if (itemMeta.hasEnchants())
                config.set("EnchantList", itemMeta.getEnchants().entrySet().stream().map(entry -> entry.getKey().getName() + ":" + entry.getValue()).collect(Collectors.toList()));
            if (itemMeta.getItemFlags().size() > 0)
                config.set("ItemFlagList", itemMeta.getItemFlags().stream().map(Enum::name).collect(Collectors.toList()));
            if (ItemUtil.getInst().isUnbreakable(itemMeta))
                config.set("Unbreakable", true);
            List<ItemUtil.AttributeData> attributeList = ItemUtil.getInst().getAttributes(item);
            if (attributeList != null && attributeList.size() > 0)
                config.set("Attributes", attributeList.stream().map(data -> data.getAttrName() + ":" + data.getAmount() + ":" + data.getOperation() + (data.getSlot() != null ? ":" + data.getSlot() : "")).collect(Collectors.toList()));
            if (itemMeta instanceof LeatherArmorMeta)
                config.set("Color", Integer.toHexString(((LeatherArmorMeta) itemMeta).getColor().asRGB()));
            config.set("SkullName", ItemUtil.getInst().getSkull(itemMeta));
        };
    }
}
