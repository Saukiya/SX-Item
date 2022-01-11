package github.saukiya.sxitem.data.item;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.event.SXItemSpawnEvent;
import github.saukiya.sxitem.event.SXItemUpdateEvent;
import github.saukiya.sxitem.nbt.NBTItemWrapper;
import github.saukiya.sxitem.nbt.NBTTagWrapper;
import github.saukiya.sxitem.util.ComponentBuilder;
import github.saukiya.sxitem.util.MessageUtil;
import github.saukiya.sxitem.util.NMS;
import github.saukiya.sxitem.util.NbtUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Saukiya
 */
public class ItemManager implements Listener {
    @Getter
    private static final List<IGenerator> generators = new ArrayList<>();
    @Getter
    private static final ItemStack emptyItem = new ItemStack(Material.AIR, 0);
    @Getter
    private static final Map<String, Material> materialMap = new HashMap<>();

    private final JavaPlugin plugin;

    private final File itemFiles;

    private final String[] defaultFile;
    //? 抱有疑问的线程安全
    private final List<Player> checkPlayers = new ArrayList<>();

    private final Map<String, String> linkMap = new HashMap<>();

    private final Map<String, IGenerator> itemMap = new HashMap<>();

    public ItemManager() {
        this(SXItem.getInst(), "Item/Default/Default.yml", "Item/NoLoad/Default.yml");
    }

    public ItemManager(JavaPlugin plugin, String... defaultFile) {
        this.plugin = plugin;
        this.defaultFile = defaultFile;
        this.itemFiles = new File(plugin.getDataFolder(), "Item");
        plugin.getLogger().info("Loaded " + generators.size() + " ItemGenerators");
        loadItemData();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (checkPlayers.size() > 0) {
                List<Player> checkPlayers = new ArrayList<>(this.checkPlayers);
                this.checkPlayers.clear();
                for (Player player : checkPlayers) {
                    if (player != null) {
                        updateItem(player, player.getInventory().getContents());
                    }
                }
            }
        }, 20, 20);
    }

    /**
     * 读取物品数据
     */
    public void loadItemData() {
        itemMap.clear();
        linkMap.clear();
        if (!itemFiles.exists() || itemFiles.listFiles().length == 0) {
            Arrays.stream(defaultFile).forEach(fileName -> plugin.saveResource(fileName, true));
        }
        loadItem(itemFiles);
        plugin.getLogger().info("Loaded " + itemMap.size() + " Items");
        linkMap.forEach((k, v) -> {
            IGenerator ig = itemMap.get(v);
            if (ig != null) {
                itemMap.put(k, ig);
            } else {
                linkMap.remove(k);
                plugin.getLogger().info("Linked Error: " + k + "->" + v);
            }
        });
        plugin.getLogger().info("Linked " + linkMap.size() + " Items");
    }

    /**
     * 获取物品编号列表
     *
     * @return Set
     */
    public Set<String> getItemList() {
        return itemMap.keySet();
    }

    /**
     * 遍历读取物品数据
     *
     * @param files File
     */
    private void loadItem(File files) {
        for (File file : files.listFiles()) {
            if (file.getName().startsWith("NoLoad")) continue;
            if (file.isDirectory()) {
                loadItem(file);
            } else {
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
                for (String key : yaml.getKeys(false)) {
                    if (key.startsWith("NoLoad")) continue;
                    if (itemMap.containsKey(key)) {
                        plugin.getLogger().warning("Don't Repeat Item Name: " + file.getName() + File.separator + key + " !");
                        continue;
                    }
                    if (yaml.isString(key)) {
                        linkMap.put(key, yaml.getString(key));
                        continue;
                    }
                    String pathName = getPathName(files);
                    String type = yaml.getString(key + ".Type", "Default");
                    IGenerator generator = getGenerator(type);
                    if (generator != null) {
                        itemMap.put(key, generator.newGenerator(pathName, key, yaml.getConfigurationSection(key)));
                    } else {
                        plugin.getLogger().warning("Don't Item Type: " + file.getName() + File.separator + key + " - " + type + " !");
                    }
                }
            }
        }
    }

    /**
     * 获取路径简称
     *
     * @param file File
     * @return PathName
     */
    private String getPathName(File file) {
        return file.toString().replace("plugins" + File.separator + "SX-Item" + File.separator, "").replace(File.separator, ">");
    }

    /**
     * 获取物品
     *
     * @param itemName String
     * @param player   Player
     * @return ItemStack / null
     */
    public ItemStack getItem(String itemName, @Nonnull Player player) {
        IGenerator ig = itemMap.get(itemName);
        if (ig != null) {
            return getItem(ig, player);
        } else {
            Material material = getMaterial(itemName);
            if (material != null) return new ItemStack(material);
        }
        return emptyItem;
    }

    /**
     * 获取物品
     *
     * @param ig     IGenerator
     * @param player Player
     * @return ItemStack / null
     */
    public ItemStack getItem(IGenerator ig, Player player) {
        ItemStack item = ig.getItem(player);
        if (item != emptyItem && item != null && ig instanceof IUpdate) {
            NBTItemWrapper wrapper = NbtUtil.getInst().getItemTagWrapper(item);
            wrapper.set(plugin.getName() + ".ItemKey", ig.getKey());
            wrapper.set(plugin.getName() + ".HashCode", ((IUpdate) ig).updateCode());
            wrapper.save();
        }
        SXItemSpawnEvent event = new SXItemSpawnEvent(plugin, player, ig, item);
        Bukkit.getPluginManager().callEvent(event);
        return event.getItem();
    }

    /**
     * 返回是否存在物品
     *
     * @param itemName String
     * @return boolean
     */
    public boolean hasItem(String itemName) {
        return itemMap.containsKey(itemName) || getMaterial(itemName) != null;
    }

    /**
     * 更新物品
     *
     * @param player     PlayerC
     * @param itemStacks ItemStack...
     */
    public void updateItem(Player player, ItemStack... itemStacks) {
        for (ItemStack item : itemStacks) {
            if (item == null) continue;
            NBTTagWrapper wrapper = NbtUtil.getInst().getItemTagWrapper(item);
            updateItemInPlugin(player, item, plugin.getName() + ".ItemKey", wrapper);
        }
    }

    public void updateItemInPlugin(Player player, ItemStack item, String keyPath, NBTTagWrapper oldWrapper) {
        if (item == null) return;
        if (oldWrapper == null) oldWrapper = NbtUtil.getInst().getItemTagWrapper(item);
        IGenerator ig = itemMap.get(oldWrapper.getString(keyPath));
        if (ig == null) return;
        if (ig instanceof IUpdate) {
            IUpdate updateIg = (IUpdate) ig;
            Integer hashCode = oldWrapper.getInt(plugin.getName() + ".HashCode");
            if (updateIg.isUpdate() && (hashCode == null || updateIg.updateCode() != hashCode)) {
                ItemStack newItem = updateIg.update(item, oldWrapper, player);
                NBTItemWrapper wrapper = NbtUtil.getInst().getItemTagWrapper(newItem);
                wrapper.set(plugin.getName() + ".ItemKey", ig.getKey());
                wrapper.set(plugin.getName() + ".HashCode", ((IUpdate) ig).updateCode());
                wrapper.save();

                SXItemUpdateEvent event = new SXItemUpdateEvent(plugin, player, ig, newItem, item);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    item.setType(event.getItem().getType());
                    item.setItemMeta(event.getItem().getItemMeta());
                }
            }
        }
    }

    /**
     * 保存物品
     *
     * @param key  编号
     * @param item 物品
     * @param type 类型
     * @return boolean
     * @throws IOException IOException
     */
    public boolean saveItem(String key, ItemStack item, String type) throws IOException {
        IGenerator generator = getGenerator(type);
        if (generator == null) return false;
        ConfigurationSection config = new MemoryConfiguration();
        config.set("Type", generator.getType());
        config = generator.saveItem(item, config);
        if (config == null) return false;
        File file = new File(itemFiles, "Type-" + generator.getType() + File.separator + "Item.yml");
        YamlConfiguration yaml = file.exists() ? YamlConfiguration.loadConfiguration(file) : new YamlConfiguration();
        yaml.set(key, config);
        yaml.save(file);
        itemMap.put(key, generator.newGenerator(getPathName(file.getParentFile()), key, config));
        return true;
    }

    /**
     * 发送物品列表给指令者
     *
     * @param sender CommandSender
     * @param search String
     */
    public void sendItemMapToPlayer(CommandSender sender, String search) {
        sender.sendMessage();
        if (search != null && search.equals("")) {
            // 文件夹
            MessageUtil.getInst().componentBuilder()
                    .add("§eDirectoryList§8 - §7ClickOpen")
                    .show("§8§o§lTo ItemList")
                    .runCommand("/sxitem give |")
                    .send(sender);

            Map<String, List<String>> map = new TreeMap<>();
            itemMap.forEach((key, ig) -> {
                List<String> list = map.computeIfAbsent(ig.getPathName(), k -> new ArrayList<>());
                list.add("§b" + (list.size() + 1) + " - §a" + key + " §8[§7" + ig.getName() + "§8]§7 - §8[§cType:" + ig.getType() + "§8]");
            });
            map.forEach((key, value) -> MessageUtil.getInst().componentBuilder().runCommand("/sxitem give |" + key + "<")
                    .add(" §8[§c" + key.replace(">", "§b>§c") + "§8]§7 - ")
                    .add("Has §c" + value.size() + "§7 Item")
                    .show(String.join("\n", value))
                    .send(sender));
        } else {
            // 物品
            MessageUtil.getInst().componentBuilder()
                    .add("§eItemList§8 - §7ClickGet " + (search != null ? "§8[§c" + search.replaceAll("(^\\||<$)", "") + "§8]" : ""))
                    .show("§8§o§lTo DirectoryList")
                    .runCommand("/sxitem give")
                    .send(sender);
            Map<String, ComponentBuilder> items = new TreeMap<>();
            itemMap.forEach((key, ig) -> {
                if (search == null || (key + ig.getName() + "|" + ig.getPathName() + "<").contains(search)) {
                    items.put(key, MessageUtil.getInst().componentBuilder()
                            .runCommand("/sxitem give " + key)
                            .add(" §b" + (items.size() + 1) + " - §a" + key + " §8[§7")
                            .add(ig.getNameComponent())
                            .add("§8]§7 - §8[§cType:" + ig.getType() + "§8]")
                            .show("§7" + ig.getConfigString() + "§8§o§lPath: " + ig.getPathName()));
                }
            });
            items.values().forEach(s -> s.send(sender));
            sender.sendMessage("§7Find §c" + items.size() + "§7 Items.");
        }
    }

    @EventHandler
    void on(PlayerItemHeldEvent event) {
        Inventory inv = event.getPlayer().getInventory();
        updateItem(event.getPlayer(), inv.getItem(event.getPreviousSlot()), inv.getItem(event.getNewSlot()));
    }

    @EventHandler
    void on(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {// 这个 instanceof 是认真的吗
            Player player = (Player) event.getPlayer();
            if (player.equals(event.getInventory().getHolder())) {
                checkPlayers.add(player);
            }
        }
    }

    @EventHandler
    void on(PlayerJoinEvent event) {
        checkPlayers.add(event.getPlayer());
    }

    /**
     * 读取Material数据
     */
    @SneakyThrows
    public static void loadMaterialData() {
        materialMap.clear();
        File file = new File(SXItem.getInst().getDataFolder(), "Material.yml");
        if (!file.exists()) {
            SXItem.getInst().saveResource("Material.yml", true);
        }
        boolean change = false;
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        boolean methodUse = NMS.compareTo(1, 13, 1) >= 0;
        for (Map.Entry<String, Object> entry : yaml.getValues(false).entrySet()) {
            Material material = Material.getMaterial(entry.getKey());
            if (material == null) {
                try {
                    if (methodUse) material = Material.getMaterial(entry.getKey(), true);
                    if (material == null) {
                        SXItem.getInst().getLogger().warning("Material.yml No Material - " + entry.getKey());
                        continue;
                    }
                    change = true;
                    SXItem.getInst().getLogger().info("Material.yml Change MaterialName - " + entry.getKey() + " To " + material.name());
                    if (yaml.contains(material.name())) {
                        yaml.set(material.name(), yaml.getString(material.name()) + "," + entry.getValue());
                    } else {
                        yaml.set(material.name(), entry.getValue());
                    }
                    yaml.set(entry.getKey(), null);
                } catch (Exception ignored) {
                    SXItem.getInst().getLogger().warning("Material.yml No Material - " + entry.getKey());
                    continue;
                }
            }
            for (String key : entry.getValue().toString().split(",")) {
                if (!key.isEmpty()) {
                    Object ret = materialMap.put(key, material);
                    if (ret != null) {
                        SXItem.getInst().getLogger().warning("Material.yml Repeat Key - " + key + " (" + ret + "/" + material + ")");
                        materialMap.remove(key);
                    }
                }
            }
        }
        if (change) yaml.save(file);
        SXItem.getInst().getLogger().info("Loaded " + materialMap.size() + " Materials");
    }

    /**
     * 获取物品材质
     *
     * @param key 索引
     * @return
     */
    public static Material getMaterial(String key) {
        Material material = materialMap.get(key);
        return material != null ? material : Material.getMaterial(key.replace(' ', '_').toUpperCase(Locale.ROOT));
    }

    public static Set<String> getMaterialString(Material value) {
        return materialMap.entrySet().stream().filter(e -> e.getValue().equals(value)).map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    /**
     * 注册物品生成器
     *
     * @param generator ItemGenerator
     */
    public static void registerGenerator(IGenerator generator) {
        if (generator.getType() == null || getGenerator(generator.getType()) != null) {
            SXItem.getInst().getLogger().warning("ItemGenerator >>  [" + generator.getClass().getSimpleName() + "] Type Error!");
            return;
        }
        generators.add(generator);
        SXItem.getInst().getLogger().info("ItemGenerator >> Register [" + generator.getClass().getSimpleName() + "] To Type " + generator.getType() + " !");
    }

    public static IGenerator getGenerator(String type) {
        return generators.stream().filter(g -> g.getType().equals(type)).findFirst().orElse(null);
    }
}