package github.saukiya.sxitem.data.item.sub;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.item.IGenerator;
import github.saukiya.sxitem.data.item.IUpdate;
import github.saukiya.sxitem.data.random.INode;
import github.saukiya.sxitem.data.random.RandomDocker;
import github.saukiya.sxitem.nbt.*;
import github.saukiya.sxitem.util.*;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

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

    Map<String, INode> randomMap;

    TagCompound nbt;

    String configString;

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
        this.enchantList = config.getStringList("EnchantList");
        this.itemFlagList = config.getStringList("ItemFlagList");
        this.unbreakable = config.getBoolean("Unbreakable");
        this.color = config.isString("Color") ? Color.fromRGB(Integer.parseInt(config.getString("Color"), 16)) : null;
        this.skullName = config.getString("SkullName");
        if (config.contains("Random")) {
            SXItem.getRandomManager().loadRandom(this.randomMap = new HashMap<>(), config.getConfigurationSection("Random"));
        }
        if (config.contains("NBT")) {
            this.nbt = (TagCompound) TagType.toTag(config.getConfigurationSection("NBT"));
        }
        YamlConfiguration yaml = new YamlConfiguration();
        config.getValues(false).forEach(yaml::set);
        this.configString = yaml.saveToString();
        this.hashCode = configString.hashCode();
        this.update = config.getBoolean("Update");
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
        if (displayName != null)
            return new TextComponent(RandomDocker.getINST().replace(displayName).replace("&", "§"));
        ComponentBuilder cb = MessageUtil.getInst().componentBuilder();
        for (String id : ids) {
            if (cb.getHandle().getExtra() != null) cb.add("§8|");
            Material material = SXItem.getItemManager().getMaterial(id);
            if (material != null) cb.add(material);
            else cb.add(id);
        }
        return cb.getHandle();
    }

    @Override
    public String getName() {
        if (displayName != null) return RandomDocker.getINST().replace(displayName).replace("&", "§");
        return "§7" + String.join("§8|§7", ids);
    }

    @Override
    public ConfigurationSection getConfig() {
        return config;
    }

    @Override
    public String getConfigString() {
        return configString;
    }

    @Override
    public ItemStack getItem(Player player) {
        return getItem(player, new RandomDocker(randomMap));
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
            config.set("EnchantList", itemMeta.getEnchants().entrySet().stream().map(entry -> entry.getKey().getName() + ":" + entry.getValue()).collect(Collectors.toList()));
        if (itemMeta.getItemFlags().size() > 0)
            config.set("ItemFlagList", itemMeta.getItemFlags().stream().map(Enum::name).collect(Collectors.toList()));
        if (ItemUtil.getInst().isUnbreakable(itemMeta))
            config.set("Unbreakable", true);
        List<ItemUtil.AttributeData> attributeList = ItemUtil.getInst().getAttributes(saveItem);
        if (attributeList != null) {
            List<String> list = new ArrayList<>();
            attributeList.forEach(data -> list.add(data.getAttrName() + ":" + data.getAmount() + ":" + data.getOperation() + (data.getSlot() != null ? ":" + data.getSlot() : "")));
            config.set("Attributes", list);
        }
        if (itemMeta instanceof LeatherArmorMeta)
            config.set("Color", Integer.toHexString(((LeatherArmorMeta) itemMeta).getColor().asRGB()));
        config.set("SkullName", ItemUtil.getInst().getSkull(itemMeta));
        return config;
    }

    @Override
    public ItemStack update(ItemStack oldItem, NBTTagWrapper oldWrapper, Player player) {
        RandomDocker randomDocker = new RandomDocker();
        Map<String, String> map = (Map<String, String>) oldWrapper.getMap(SXItem.getInst().getName() + ".Lock");
        if (map != null) map.forEach((k, v) -> randomDocker.getLockMap().put(k, v));
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

    public ItemStack getItem(@Nonnull Player player, RandomDocker docker) {
        String id = docker.replace(ids.get(SXItem.getRandom().nextInt(ids.size())));

        String itemName = docker.replace(PlaceholderUtil.setPlaceholders(player, this.displayName));

        List<String> loreList = docker.replace(PlaceholderUtil.setPlaceholders(player, this.loreList));

        List<String> enchantList = docker.replace(this.enchantList);

        String skullName = docker.replace(PlaceholderUtil.setPlaceholders(player, this.skullName));
        ItemStack item = getItemStack(itemName, id, amount, loreList, enchantList, itemFlagList, unbreakable, color, skullName);

        NBTItemWrapper wrapper = NbtUtil.getInst().getItemTagWrapper(item);
        if (this.nbt != null) {
            TagCompound nbt = (TagCompound) docker.replace(this.nbt);
            for (Map.Entry<String, TagBase> entry : nbt.entrySet()) {
                wrapper.set(entry.getKey(), entry.getValue());
            }
        }

        docker.getLockLog().forEach(key -> wrapper.set(SXItem.getInst().getName() + ".Lock." + key, docker.getLockMap().get(key)));
        wrapper.save();
        return item;
    }

    public ItemStack getItemStack(String itemName, String id, int amount, List<String> loreList, List<String> enchantList, List<String> itemFlagList, boolean unbreakable, Color color, String skullName) {
        Material material = SXItem.getItemManager().getMaterial(id);
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();

        if (itemName != null) {
            meta.setDisplayName(itemName.replace("&", "§"));
        }
        loreList.replaceAll(lore -> lore.replace("&", "§"));
        meta.setLore(loreList);

        for (String enchant : enchantList) {
            String[] enchantSplit = enchant.split(":");
            Enchantment enchantment = Enchantment.getByName(enchantSplit[0]);
            int level = Integer.parseInt(enchantSplit[1]);
            if (enchantment != null && level != 0) {
                meta.addEnchant(enchantment, level, true);
            }
        }

        Arrays.stream(ItemFlag.values()).filter(itemFlag -> itemFlagList.contains(itemFlag.name())).forEach(meta::addItemFlags);

        ItemUtil.getInst().setUnbreakable(meta, unbreakable);

        if (color != null && meta instanceof LeatherArmorMeta) ((LeatherArmorMeta) meta).setColor(color);

        ItemUtil.getInst().setSkull(meta, skullName);

        item.setItemMeta(meta);

        //TODO 改到getItem那边去
        if (config.isList("Attributes")) {
            List<ItemUtil.AttributeData> attributeList = new ArrayList<>();
            for (String attributeData : config.getStringList("Attributes")) {
                String[] split = attributeData.split(":");
                if (split.length < 3) continue;
                int operation = Integer.parseInt(split[2]);
                attributeList.add(new ItemUtil.AttributeData()
                        .setAttrName(split[0])
                        .setAmount(Double.parseDouble(split[1]))
                        .setOperation(operation)
                        .setSlot(split.length > 3 ? split[3] : null));
            }
            ItemUtil.getInst().addAttributes(item, attributeList);
        }
        return item;
    }
}
