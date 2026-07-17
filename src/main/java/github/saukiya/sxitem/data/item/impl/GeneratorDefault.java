package github.saukiya.sxitem.data.item.impl;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.expression.ExpressionHandler;
import github.saukiya.sxitem.data.item.IGenerator;
import github.saukiya.sxitem.data.item.IUpdate;
import github.saukiya.sxitem.data.item.ItemManager;
import github.saukiya.sxitem.data.random.INode;
import github.saukiya.sxitem.util.Util;
import github.saukiya.tools.nms.*;
import github.saukiya.tools.nbt.TagCompound;
import github.saukiya.tools.util.XMaterial;
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
 * 默认物品生成器，负责把物品配置转换为可生成、可更新的 Bukkit 物品。
 *
 * <p>配置字段通常使用既有的规范拼写；材质 ID 字段额外兼容不同大小写，
 * 避免 YAML 键名大小写差异使物品静默回退为默认材质。</p>
 */
@Getter
public class GeneratorDefault extends IGenerator implements IUpdate {

    /**
     * 材质 ID 的规范配置键；精确拼写始终优先于兼容的大小写变体。
     */
    private static final String MATERIAL_ID_KEY = "ID";

    protected String displayName;

    protected List<String> ids;

    protected Map<String, INode> randomMap;

    protected Map<String, Object> nbt;

    protected Map<String, Object> component;

    protected int hashCode;

    protected boolean update;

    /**
     * 创建默认物品生成器，并快照配置中需要跨多次生成复用的数据。
     *
     * @param key 物品配置键
     * @param config 当前物品配置节点
     * @param group 配置来源组
     */
    @SuppressWarnings("DataFlowIssue")
    public GeneratorDefault(String key, ConfigurationSection config, String group) {
        super(key, config, group);
        this.displayName = config.getString("Name");
        String materialIdKey = findKeyIgnoreCase(config, MATERIAL_ID_KEY);
        this.ids = config.isList(materialIdKey) ? config.getStringList(materialIdKey) : Collections.singletonList(config.getString(materialIdKey, "APPLE"));
        if (config.isConfigurationSection("Random")) {
            SXItem.getRandomManager().loadRandom(this.randomMap = new HashMap<>(), config.getConfigurationSection("Random"));
        }
        if (config.isConfigurationSection("NBT")) {
            this.nbt = convertConfig(config.getConfigurationSection("NBT"));
            nbtFix(nbt);
        }
        if (config.isConfigurationSection("Components")) {
            component = convertConfig(config.getConfigurationSection("Components"));
        }
        this.hashCode = configString.hashCode();
        this.update = config.getBoolean("Update");
    }

    /**
     * 在当前配置节点中解析字段名，同时确保规范拼写在多个大小写变体并存时优先。
     * Bukkit 的 YAML 键区分大小写，因此必须显式处理历史配置中的 {@code Id}/{@code id}。
     */
    private static String findKeyIgnoreCase(ConfigurationSection config, String canonicalKey) {
        Set<String> keys = config.getKeys(false);
        if (keys.contains(canonicalKey)) {
            return canonicalKey;
        }
        return keys.stream().filter(key -> key.equalsIgnoreCase(canonicalKey)).findFirst().orElse(canonicalKey);
    }

    @Override
    public String getType() {
        return "Default";
    }

    @Override
    public String getName() {
        if (displayName != null) return ExpressionHandler.getInst().replace(displayName).replace('&', '§');
        return "§7" + String.join("§8|§7", ids);
    }

    @Override
    public BaseComponent getNameComponent() {
        if (displayName != null)
            return new TextComponent(ExpressionHandler.getInst().replace(displayName).replace('&', '§'));
        MessageUtil.Builder cb = MessageUtil.getInst().builder().add("§r");
        Material material = XMaterial.getMaterial(ExpressionHandler.getInst().replace(ids.get(0)));
        if (material != null) cb.add(material);
        else cb.add(ids.get(0));
        if (ids.size() > 1) cb.add("..");
        return cb.getHandle();
    }

    @Override
    protected ItemStack getItem(Player player, Object... args) {
        if (args.length > 0 && args[0] instanceof ExpressionHandler) {
            return getItem(player, (ExpressionHandler) args[0]);
        }
        ExpressionHandler handler = new ExpressionHandler(player, randomMap);
        if (args.length > 0) {
            if (args[0] instanceof Map) {
                handler.getOtherMap().putAll((Map<String, String>) args[0]);
            } else if (args[0] instanceof String) {
                for (int i = 1; i < args.length; i += 2) {
                    handler.getOtherMap().put((String) args[i - 1], (String) args[i]);
                }
            }
        }
        return getItem(player, handler);
    }

    @SuppressWarnings("deprecation")
    protected ItemStack getItem(Player player, ExpressionHandler handler) {
        String id = handler.replace(Util.random(ids));

        ItemStack item = XMaterial.getItem(id);
        if (item == null) {
            SXItem.getInst().getLogger().warning("Item-" + getKey() + " ID ERROR: " + id);
            return ItemManager.getEmptyItem();
        }
        String inlineDurability = extractInlineDurability(id);
        String durability = inlineDurability != null ? inlineDurability : handler.replace(config.getString("Durability"));
        if (!StringUtils.isEmpty(durability)) {
            Material material = item.getType();
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
        item.setAmount(Integer.parseInt(handler.replace(config.getString("Amount", "1"))));

        ItemMeta meta = item.getItemMeta();

        String itemName = handler.replace(this.displayName);
        if (itemName != null) {
            meta.setDisplayName(itemName.replace('&', '§'));
        }

        List<String> loreList = handler.replace(config.getStringList("Lore"));
        loreList.replaceAll(lore -> lore.replace('&', '§'));
        meta.setLore(loreList);

        for (String enchant : handler.replace(config.getStringList("EnchantList"))) {
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

        ItemUtil.getInst().setSkull(meta, handler.replace(config.getString("SkullName")));

        if (meta instanceof LeatherArmorMeta && config.isString("Color")) {
            ((LeatherArmorMeta) meta).setColor(Color.fromRGB(Integer.parseInt(handler.replace(config.getString("Color")), 16)));
        }

        // TODO 这玩意最好整合到ItemUtil里做NMS
        if (NMS.compareTo(1, 11, 1) >= 0 && config.isConfigurationSection("Potion") && meta instanceof PotionMeta) {
            ConfigurationSection potionConfig = config.getConfigurationSection("Potion");
            PotionEffectType[] potionEffectTypes = PotionEffectType.values();
            if (potionConfig != null) {
                for (String effectId : potionConfig.getKeys(false)) {
                    ConfigurationSection potionEffectConfig = potionConfig.getConfigurationSection(effectId);
                    effectId = handler.replace(effectId);
                    PotionEffectType effect = null;
                    for (PotionEffectType potionEffectType : potionEffectTypes) {
                        if (potionEffectType == null || potionEffectType.getName().equals(effectId)) continue;
                        effect = potionEffectType;
                    }
                    if (effect != null) {
                        int duration = Integer.parseInt(handler.replace(potionEffectConfig.getString("duration", "1")));
                        int amplifier = Integer.parseInt(handler.replace(potionEffectConfig.getString("amplifier", "1")));
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
            if (config.isString("CustomModelData")) {
                val customDataStr = handler.replace(config.getString("CustomModelData"));
                if (StringUtils.isNumeric(customDataStr)) {
                    meta.setCustomModelData(Integer.parseInt(customDataStr));
                }
            } else {
                int customData = config.getInt("CustomModelData", -1);
                if (customData != -1) {
                    meta.setCustomModelData(customData);
                }
            }
        }

        item.setItemMeta(meta);

        if (config.getBoolean("ClearAttribute")) {
            ItemUtil.getInst().clearAttribute(item, meta);
        } else if (config.isList("Attributes")) {
            List<ItemUtil.AttributeData> attributeList = new ArrayList<>();
            for (String data : handler.replace(config.getStringList("Attributes"))) {
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
        if (component != null || nbt != null || !handler.getLockMap().isEmpty()) {
            Object nmsItem = NbtUtil.getInst().getNMSItem(item);
            if (component != null) {
                val dataComponentMap = ComponentUtil.getInst().valueToMap(handler.replace(component));
                ComponentUtil.getInst().setDataComponentMap(nmsItem, dataComponentMap);
            }

            if (nbt != null || !handler.getLockMap().isEmpty()) {
                val wrapper = NbtUtil.getInst().getItemTagWrapper(item, nmsItem);
                wrapper.setAll((Map<Object, Object>) handler.replace(nbt));
                handler.getLockMap().forEach((key, value) -> wrapper.set(SXItem.getInst().getName() + ".Lock." + key, value));
                wrapper.save();
            } else {
                NbtUtil.getInst().setNMSItem(item, nmsItem);
            }
        }
        return item;
    }

    /**
     * Extracts the historical {@code material:durability} suffix without treating the colon in
     * a mod registry key as a separator. An exact Bukkit material match always wins because a
     * numeric namespaced path such as {@code mod:123} is legal and must remain intact.
     *
     * @param id configured material identifier
     * @return inline durability expression, or {@code null} when the ID is only a material key
     */
    private static String extractInlineDurability(String id) {
        int separator = id.lastIndexOf(':');
        if (separator <= 0 || separator == id.length() - 1 || Material.matchMaterial(id) != null) return null;

        String durability = id.substring(separator + 1);
        if (!isDurabilityExpression(durability)) return null;
        return XMaterial.has(id.substring(0, separator)) ? durability : null;
    }

    /**
     * Accepts only the three durability forms consumed below: absolute shorts, remaining-value
     * expressions prefixed by {@code <}, and percentages. This prevents arbitrary namespace
     * paths from reaching numeric parsers.
     */
    private static boolean isDurabilityExpression(String value) {
        try {
            if (value.endsWith("%")) {
                Double.parseDouble(value.substring(0, value.length() - 1));
            } else {
                String absolute = value.startsWith("<") ? value.substring(1) : value;
                Short.parseShort(absolute);
            }
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
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
        ExpressionHandler handler = new ExpressionHandler(player, randomMap);
        Map<String, String> map = (Map<String, String>) oldWrapper.getMap(SXItem.getInst().getName() + ".Lock");
        if (map != null) handler.getOtherMap().putAll(map);
        return getItem(player, handler);
    }

    public static void save(ItemStack item, ConfigurationSection config) {
        ItemMeta itemMeta = item.getItemMeta();
        config.set("Name", itemMeta.hasDisplayName() ? itemMeta.getDisplayName().replace('§', '&') : null);
        // XMaterial preserves vanilla compatibility names, while its runtime fallback retains
        // the namespace required to resolve mod materials after the configuration is reloaded.
        config.set("ID", XMaterial.getKey(item.getType()) + (item.getDurability() != 0 ? ":" + item.getDurability() : ""));
        if (item.getAmount() > 1)
            config.set("Amount", item.getAmount());
        if (itemMeta.hasLore())
            config.set("Lore", serializeLore(itemMeta.getLore()));
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
        saveNativeData(item, config);
    }

    /**
     * Persists native item state as editable YAML sections instead of relying on Bukkit's opaque
     * ItemStack serialization. Mod items commonly share one Bukkit material and keep their real
     * identity in NBT or data components, so omitting either section changes the item on reload.
     */
    private static void saveNativeData(ItemStack item, ConfigurationSection config) {
        NbtUtil nbtUtil = NbtUtil.getInst();
        Object nmsItem = nbtUtil.getNMSItem(item);

        TagCompound itemNbt = nbtUtil.getItemTag(item);
        if (itemNbt != null && !itemNbt.isEmpty()) {
            // Lore has an editable top-level representation. Keeping the raw JSON copy would
            // overwrite expanded lines when NBT is applied after ItemMeta during item creation.
            itemNbt.remove("display.Lore");
            if (itemNbt.getMap("display").isEmpty()) itemNbt.remove("display");
            if (!itemNbt.isEmpty()) createSection(config, "NBT", itemNbt.getValue());
        }

        ComponentUtil componentUtil = ComponentUtil.getInst();
        if (componentUtil == null) return;
        Object componentMap = componentUtil.getDataComponentMap(nmsItem);
        if (componentMap == null) return;

        Object componentValue = componentUtil.mapToValue(componentMap);
        if (componentValue instanceof Map && !((Map<?, ?>) componentValue).isEmpty()) {
            createSection(config, "Components", (Map<?, ?>) componentValue);
        }
    }

    /**
     * Creates a real ConfigurationSection so the newly saved item can be loaded immediately from
     * the in-memory configuration; assigning a raw Map would only become a section after a YAML
     * save-and-reload cycle.
     */
    private static void createSection(ConfigurationSection config, String path, Map<?, ?> values) {
        if (config.isConfigurationSection(path)) config.set(path, null);
        config.createSection(path, values);
    }

    /**
     * Expands embedded newlines into independent YAML entries because Bukkit permits a single
     * lore value to contain multiple rendered lines, while SX-Item's editable format requires
     * one list element per visible line.
     */
    private static List<String> serializeLore(List<String> lore) {
        return lore.stream()
                .flatMap(line -> Arrays.stream(line.split("\\r?\\n", -1)))
                .map(line -> line.replace('§', '&'))
                .collect(Collectors.toList());
    }

    public static Map<String, Object> convertConfig(ConfigurationSection config) {
        Map<String, Object> result = config.getValues(false);
        result.entrySet().stream().filter(entry -> entry.getValue() instanceof ConfigurationSection).forEachOrdered(entry -> result.put(entry.getKey(), convertConfig((ConfigurationSection) entry.getValue())));
        return result;
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void nbtFix(Map<String, Object> nbt) {
        val attributeModifiers = nbt.get("AttributeModifiers");
        if (attributeModifiers instanceof List) {
            for (Object attributeModifier : (List<?>) attributeModifiers) {
                if (attributeModifier instanceof Map) {
                    val attributeModifierMap = (Map) attributeModifier; 
                    val uuids = attributeModifierMap.get("UUID");
                    if (uuids instanceof List) {
                        val uuidsArray = ((List<?>) uuids).stream()
                                .mapToInt(obj -> {
                                    if (obj instanceof Number) {
                                        return ((Number) obj).intValue();
                                    } else if (obj instanceof String) {
                                        try {
                                            return Integer.parseInt((String) obj);
                                        } catch (NumberFormatException ignored) {
                                            SXItem.getInst().getLogger().warning("[" + key + "] UUID Convert Error: " + obj);
                                        }
                                    }
                                    return 0;
                                })
                                .toArray();
                        attributeModifierMap.put("UUID", uuidsArray);
                    }
                }
            }
        }
    }
}
