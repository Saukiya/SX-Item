package github.saukiya.sxitem.data.item.sub;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.item.IGenerator;
import github.saukiya.sxitem.data.item.IUpdate;
import github.saukiya.sxitem.data.random.RandomDocker;
import github.saukiya.sxitem.util.MessageUtil;
import lombok.NoArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Saukiya
 */
@NoArgsConstructor
public class GeneratorDefault implements IGenerator, IUpdate {

    String pathName;

    String key;

    ConfigurationSection config;

    String displayName;

    List<String> ids;

    int amount;

    List<String> loreList;

    List<String> enchantList;

    List<String> itemFlagList;

    boolean unbreakable;

    Color color;

    String skullName;

    int hashCode;

    boolean update;

    private GeneratorDefault(String pathName, String key, ConfigurationSection config) {
        this.pathName = pathName;
        this.key = key;
        this.config = config;
        this.displayName = config.getString("Name");
        this.ids = config.isList("ID") ? config.getStringList("ID") : Collections.singletonList(config.getString(".ID"));
        this.amount = config.getInt("Amount", 1);
        this.loreList = config.getStringList("Lore");
        this.enchantList = config.getStringList("Enchants");
        this.itemFlagList = config.getStringList("Flags");
        this.unbreakable = config.getBoolean("Unbreakable");
        this.color = config.isString("Color") ? Color.fromRGB(Integer.parseInt(config.getString("Color"), 16)) : null;
        this.skullName = config.getString("SkullName");
        this.hashCode = config.getValues(true).hashCode();
        this.update = config.getBoolean("Update");
        if (config.contains("Random")) {
            SXItem.getRandomStringManager().loadRandomLocal(key, config.getConfigurationSection("Random"));
        }
    }

    @Override
    public String getType() {
        return "Default";
    }

    @Override
    public IGenerator newGenerator(String pathName, String key, ConfigurationSection config) {
        return new GeneratorDefault(pathName, key, config);
    }

    @Override
    public String getPathName() {
        return pathName;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public BaseComponent getNameComponent() {
        if (displayName != null) return new TextComponent(displayName.replace("&", "§"));
        BaseComponent bc = new TextComponent();
        for (String id : ids) {
            if (bc.getExtra() != null) bc.addExtra("§8|");
            bc.addExtra(MessageUtil.getInst().showItem(Material.getMaterial(id)));
        }
        return bc;
    }

    @Override
    public String getName() {
        if (displayName != null) return displayName;
        StringBuilder sb = new StringBuilder("§7");
        for (String id : ids) {
            if (sb.length() != 0) sb.append("§8|§7");
            sb.append(Material.getMaterial(id));
        }
        return sb.toString();
    }

    @Override
    public ConfigurationSection getConfig() {
        return config;
    }

    @Override
    public ItemStack getItem(@Nonnull Player player) {
        RandomDocker docker = new RandomDocker(key);

        String id = docker.setRandom(ids.get(SXItem.getRandom().nextInt(ids.size())));

        String itemName = docker.setRandom(this.displayName);
        if (itemName != null) itemName = PlaceholderAPI.setPlaceholders(player, itemName);

        List<String> loreList = new ArrayList<>();
        for (String lore : this.loreList) {
            lore = docker.setRandom(lore);
            if (!lore.contains("%DeleteLore%")) {
                loreList.addAll(Arrays.asList(lore.split("/n|\n")));
            }
        }
        loreList = PlaceholderAPI.setPlaceholders(player, loreList);


        List<String> enchantList = new ArrayList<>();
        for (String enchant : this.enchantList) {
            enchant = docker.setRandom(enchant);
            if (!enchant.contains("%DeleteLore%")) {
                enchantList.addAll(Arrays.asList(enchant.split("//n|\n")));
            }
        }

        String skullName = this.skullName != null ? PlaceholderAPI.setPlaceholders(player, this.skullName) : this.skullName;
        ItemStack item = getItemStack(itemName, id, amount, loreList, enchantList, itemFlagList, unbreakable, color, skullName);

        //Deprecated
        if (docker.getLockMap().size() > 0) {
            List<String> list = new ArrayList<>();
            for (Map.Entry<String, String> entry : docker.getLockMap().entrySet()) {
                list.add(entry.getKey() + "§e§k|§e§r" + entry.getValue());
            }
            SXItem.getNbtUtil().setNBTList(item, SXItem.getInst().getName() + "-Lock", list);
        }
        return item;
    }

    @Override
    public ConfigurationSection saveItem(ItemStack saveItem, ConfigurationSection config) {
        ItemMeta itemMeta = saveItem.getItemMeta();
        config.set("Name", itemMeta.hasDisplayName() ? itemMeta.getDisplayName().replace("§", "&") : null);
        config.set("ID", saveItem.getType().name());
        if (saveItem.getAmount() > 1)
            config.set("Amount", saveItem.getAmount());
        if (itemMeta.hasLore())
            config.set("Lore", itemMeta.getLore().stream().map(s -> s.replace("§", "&")).collect(Collectors.toList()));
        if (itemMeta.hasEnchants())
            config.set("Enchants", itemMeta.getEnchants().entrySet().stream().map(entry -> entry.getKey().getName() + ":" + entry.getValue()).collect(Collectors.toList()));
        if (itemMeta.getItemFlags().size() > 0)
            config.set("Flags", itemMeta.getItemFlags().stream().map(Enum::name).collect(Collectors.toList()));
        if (itemMeta.isUnbreakable())
            config.set("Unbreakable", true);
        if (itemMeta instanceof LeatherArmorMeta)
            config.set("Color", Integer.toHexString(((LeatherArmorMeta) itemMeta).getColor().asRGB()));
        if (itemMeta instanceof SkullMeta && ((SkullMeta) itemMeta).hasOwner()) {
            config.set("SkullName", ((SkullMeta) itemMeta).getOwningPlayer().getUniqueId().toString());
        }
        return config;
    }


    public ItemStack getItemStack(String itemName, String id, int amount, List<String> loreList, List<String> enchantList, List<String> itemFlagList, boolean unbreakable, Color color, String skullName) {
        Material material = Material.getMaterial(id.replace(' ', '_').toUpperCase());
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();

        if (itemName != null) {
            meta.setDisplayName(itemName.replace("&", "§"));
        }
        for (int i = 0; i < loreList.size(); i++) {
            loreList.set(i, loreList.get(i).replace("&", "§"));
        }
        meta.setLore(loreList);

        for (String enchant : enchantList) {
            String[] enchantSplit = enchant.split(":");
            Enchantment enchantment = Enchantment.getByName(enchantSplit[0]);
            int level = Integer.parseInt(enchantSplit[1]);
            if (enchantment != null && level != 0) {
                meta.addEnchant(enchantment, level, true);
            }
        }

        for (ItemFlag itemFlag : ItemFlag.values()) {
            if (itemFlagList.contains(itemFlag.name())) {
                meta.addItemFlags(itemFlag);
            }
        }

        meta.setUnbreakable(unbreakable);

        if (color != null && meta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) meta).setColor(color);
        }

        if (skullName != null && meta instanceof SkullMeta) {
            try {
                UUID skullUUID = UUID.fromString(skullName);
                OfflinePlayer player = Bukkit.getOfflinePlayer(skullUUID);
                ((SkullMeta) meta).setOwningPlayer(player);
            } catch (Exception e) {
                ((SkullMeta) meta).setOwner(skullName);
            }
        }

        item.setItemMeta(meta);
        return item;
    }

    @Override
    public int updateCode() {
        return hashCode;
    }

    @Override
    public boolean isUpdate() {
        return update;
    }
}
