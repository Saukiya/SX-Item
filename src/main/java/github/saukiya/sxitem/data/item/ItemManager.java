package github.saukiya.sxitem.data.item;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.item.impl.GeneratorReMaterial;
import github.saukiya.sxitem.event.SXItemSpawnEvent;
import github.saukiya.sxitem.event.SXItemUpdateEvent;
import github.saukiya.tools.base.Tuple;
import github.saukiya.tools.nms.MessageUtil;
import github.saukiya.tools.nms.NbtUtil;
import github.saukiya.tools.util.ReMaterial;
import lombok.Getter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 物品管理器
 */
public class ItemManager implements Listener {
    @Getter
    private static final Map<String, IGenerator.Loader> loadFunction = new HashMap<>();
    @Getter
    private static final Map<String, IGenerator.Saver> saveFunction = new HashMap<>();
    @Getter
    private static final ItemStack emptyItem = new ItemStack(Material.AIR, 0);

    private final JavaPlugin plugin;

    private final String itemKey;

    private final String hashCodeKey;

    private final String defaultType;

    private final File rootDirectory;

    private final List<Player> checkPlayers = new ArrayList<>();

    private final Map<String, IGenerator> itemMap = new HashMap<>();

    private final Map<Tuple<String, String>, String> linkMap = new HashMap<>();

    private final Map<String, List<Tuple<String, List<IGenerator>>>> informationMap = new HashMap<>();

    private final IGenerator reMaterial = new GeneratorReMaterial();

    @Getter
    private final HashSet<String> protectNbtList = new HashSet<>();

    public ItemManager(JavaPlugin plugin) {
        this(plugin, plugin.getName() + ".ItemKey", plugin.getName() + ".HashCode");
    }

    public ItemManager(JavaPlugin plugin, String itemKey, String hashCodeKey) {
        this(plugin, itemKey, hashCodeKey, "Default");
    }

    public ItemManager(JavaPlugin plugin, String itemKey, String hashCodeKey, String defaultType) {
        this.plugin = plugin;
        this.itemKey = itemKey;
        this.hashCodeKey = hashCodeKey;
        this.defaultType = defaultType;
        this.rootDirectory = new File(plugin.getDataFolder(), "Item");
        plugin.getLogger().info("Loaded " + loadFunction.size() + " ItemGenerators");
        loadItemData();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!checkPlayers.isEmpty()) {
                List<Player> checkPlayers = new ArrayList<>(this.checkPlayers);
                this.checkPlayers.clear();
                for (Player player : checkPlayers) {
                    if (player != null) {
                        checkUpdateItem(player, player.getInventory().getContents());
                    }
                }
            }
        }, 20, 20);
    }

    /**
     * 注册物品生成器
     *
     * @param type     类型
     * @param loadFunc 加载方法
     * @param saveFunc 保存方法
     */
    public static void register(String type, @Nonnull IGenerator.Loader loadFunc, @Nullable IGenerator.Saver saveFunc) {
        if (type == null || loadFunction.containsKey(type)) {
            SXItem.getInst().getLogger().severe("ItemGenerator >> Type Error: " + type);
            return;
        }
        loadFunction.put(type, loadFunc);
        if (saveFunc != null) saveFunction.put(type, saveFunc);
        SXItem.getInst().getLogger().info("ItemGenerator >> Type Register: " + type);
    }

    @Deprecated
    public static Material getMaterial(String key) {
        return ReMaterial.getMaterial(key);
    }

    /**
     * 读取物品数据
     */
    public void loadItemData() {
        if (!rootDirectory.exists()) {
            plugin.getLogger().warning("Directory is not exists: " + rootDirectory.getName());
            return;
        }
        // 加载物品
        loadItem(plugin.getName(), rootDirectory);
        plugin.getLogger().info("Loaded " + itemMap.size() + " Items");
    }

    /**
     * 获取物品编号列表
     */
    public Set<String> getItemList() {
        return itemMap.keySet();
    }

    /**
     * 获取物品生成器列表
     */
    public Collection<IGenerator> getGeneratorList() {
        return itemMap.values();
    }

    /**
     * 加载物品列表
     *
     * @param group     组名
     * @param directory 文件夹
     */
    public void loadItem(String group, File directory) {
        Map<String, ConfigurationSection> configs = new HashMap<>();
        loadConfigs(directory, configs, "");
        loadItem(group, configs);
    }

    /**
     * 加载物品列表
     *
     * @param group   组名
     * @param configs 配置列表
     */
    public void loadItem(String group, ConfigurationSection... configs) {
        Map<String, ConfigurationSection> mapConfig = new HashMap<>();
        for (int i = 0; i < configs.length; i++) {
            mapConfig.put(String.valueOf(i), configs[i]);
        }
        loadItem(group, mapConfig);
    }

    /**
     * 加载物品列表
     * 注意事项:
     * 1.不可以覆盖其他group的key值
     * 2.不会被SX重载时清除
     * 3.每次加载清空上次的存储
     *
     * @param group   组名
     * @param configs 带名字的配置列表
     */
    public void loadItem(String group, Map<String, ConfigurationSection> configs) {
        itemMap.values().removeIf(generator -> generator.group.equals(group));
        List<Tuple<String, List<IGenerator>>> information = new ArrayList<>();
        informationMap.put(group, information);
        Iterator<Tuple<String, String>> linkKeys = linkMap.keySet().iterator();
        while (linkKeys.hasNext()) {
            Tuple<String, String> linkEntry = linkKeys.next();
            if (!linkEntry.a().equals(group)) continue;
            itemMap.remove(linkEntry.b());
            linkKeys.remove();
        }
        configs.forEach((path, config) -> {
            List<IGenerator> informationList = new ArrayList<>();
            information.add(new Tuple<>(path, informationList));
            for (String key : config.getKeys(false)) {
                if (key.startsWith("NoLoad")) continue;
                if (itemMap.containsKey(key)) {
                    plugin.getLogger().warning("Don't Repeat Item Name: " + path + File.separator + key + " !");
                    continue;
                }
                if (config.isString(key)) {
                    linkMap.put(new Tuple<>(group, key), config.getString(key));
                    itemMap.put(key, null);
                    continue;
                }
                String type = config.getString(key + ".Type", defaultType);
                IGenerator.Loader loadFunction = getLoadFunction().get(type);
                if (loadFunction != null) {
                    config.set(key + ".Path", path);
                    IGenerator generator = loadFunction.apply(key, config.getConfigurationSection(key), group);
                    informationList.add(generator);
                    itemMap.put(key, generator);
                } else {
                    plugin.getLogger().warning("Don't Item Type: " + path + File.separator + key + " - " + type + " !");
                }
            }
        });
        ReLoadLink();
    }

    /**
     * 通过识别物品key获取该物品的Id (需要支持接口IUpdate)
     */
    @Nullable
    public String getItemKey(ItemStack item) {
        if (item != null && !item.getType().equals(Material.AIR) && item.hasItemMeta()) {
            return NbtUtil.getInst().getItemTagWrapper(item).getString(itemKey);
        }
        return null;
    }

    /**
     * 通过key获取该物品的生成器
     *
     * @param itemKey
     * @return
     */
    @Nullable
    public IGenerator getGenerator(String itemKey) {
        IGenerator result = itemMap.get(itemKey);
        if (result == null && ReMaterial.has(itemKey)) {
            return reMaterial;
        }
        return result;
    }

    /**
     * 通过识别物品key获取该物品的生成器 (需要支持接口IUpdate)
     */
    @Nullable
    public IGenerator getGenerator(ItemStack item) {
        String key = getItemKey(item);
        return itemMap.get(key);
    }

    /**
     * 返回是否存在物品(或者材质ID)
     */
    public boolean hasItem(String itemKey) {
        return itemMap.containsKey(itemKey) || ReMaterial.has(itemKey);
    }

    /**
     * 获取物品 - 不带玩家参数
     */
    @Nonnull
    public ItemStack getItem(String itemKey, Object... args) {
        return getItem(itemKey, null, args);
    }

    /**
     * 获取物品 - 不带玩家参数
     */
    @Nonnull
    public ItemStack getItem(IGenerator generator, Object... args) {
        return getItem(generator, null, args);
    }

    /**
     * 获取物品
     */
    @Nonnull
    public ItemStack getItem(String itemKey, Player player, Object... args) {
        if (itemKey == null) return emptyItem;
        IGenerator generator = itemMap.get(itemKey);
        if (generator != null) {
            return getItem(generator, player, args);
        } else {
            ItemStack item = ReMaterial.getItem(itemKey);
            if (item != null) return item;
        }
        return emptyItem;
    }

    /**
     * 获取物品
     */
    @Nonnull
    public ItemStack getItem(IGenerator generator, Player player, Object... args) {
        if (generator == null) return emptyItem;
        ItemStack item = generator.getItem(player, args);
        if (item != emptyItem && item != null && generator instanceof IUpdate) {
            NbtUtil.getInst().getItemTagWrapper(item).builder()
                    .set(itemKey, generator.getKey())
                    .set(hashCodeKey, ((IUpdate) generator).updateCode())
                    .save();
        }
        SXItemSpawnEvent event = new SXItemSpawnEvent(plugin, player, generator, item);
        Bukkit.getPluginManager().callEvent(event);
        return event.getItem();
    }

    /**
     * 检查更新物品
     */
    public void checkUpdateItem(Player player, ItemStack... itemStacks) {
        for (ItemStack item : itemStacks) {
            if (item == null || item.getType().equals(Material.AIR)) continue;
            val oldWrapper = NbtUtil.getInst().getItemTagWrapper(item);
            IGenerator generator = itemMap.get(oldWrapper.getString(itemKey));
            if (generator instanceof IUpdate) {
                IUpdate updateIg = (IUpdate) generator;
                Integer hashCode = oldWrapper.getInt(hashCodeKey);
                if (!updateIg.isUpdate() || (hashCode != null && updateIg.updateCode() == hashCode)) continue;
                updateItem(player, item, updateIg, oldWrapper);
            }
        }
    }

    /**
     * 强制更新物品
     */
    public void updateItem(Player player, ItemStack item) {
        val oldWrapper = NbtUtil.getInst().getItemTagWrapper(item);
        IGenerator generator = itemMap.get(oldWrapper.getString(itemKey));
        if (!(generator instanceof IUpdate)) return;
        updateItem(player, item, (IUpdate) generator, oldWrapper);
    }

    /**
     * 强制更新物品
     */
    public void updateItem(Player player, ItemStack item, IUpdate updateIg, NbtUtil.Wrapper oldWrapper) {
        ItemStack newItem = updateIg.update(item, oldWrapper, player);
        val wrapper = NbtUtil.getInst().getItemTagWrapper(newItem);
        wrapper.set(itemKey, updateIg.getKey());
        wrapper.set(hashCodeKey, updateIg.updateCode());
        updateIg.protectNBT(wrapper, oldWrapper, protectNbtList);

        SXItemUpdateEvent event = new SXItemUpdateEvent(plugin, player, (IGenerator) updateIg, newItem, item);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        item.setType(event.getItem().getType());
        item.setItemMeta(event.getItem().getItemMeta());
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
        if (itemMap.containsKey(key)) return false;
        IGenerator.Saver function = saveFunction.get(type);
        if (function == null) return false;
        ConfigurationSection config = new MemoryConfiguration();
        config.set("Type", type);
        function.apply(item, config);
        if (config.getKeys(false).size() == 1) return false;
        String filePath = "Type-" + type + File.separator + "Item.yml";
        File file = new File(rootDirectory, filePath);
        YamlConfiguration yaml = file.exists() ? YamlConfiguration.loadConfiguration(file) : new YamlConfiguration();
        yaml.set(key, config);
        yaml.save(file);
        config.set("Path", plugin.getName() + "#" + filePath);
        itemMap.put(key, loadFunction.get(type).apply(key, config, plugin.getName()));
        return true;
    }

    /**
     * 发送物品列表给指令者
     *
     * @param sender CommandSender
     * @param search String
     */
    public void sendItemInfoToPlayer(CommandSender sender, String search) {
        sender.sendMessage("");
        if (search != null && search.isEmpty() && (informationMap.size() > 1 || informationMap.values().stream().anyMatch(information -> information.size() > 1))) {
            // 文件夹
            MessageUtil.getInst().builder()
                    .add("§eDirectoryList§8 - §7ClickOpen")
                    .show("§8§o§lTo ItemList")
                    .runCommand("/sxitem give |")
                    .send(sender);
            informationMap.forEach((group, information) -> {
                MessageUtil.getInst().builder().runCommand("/sxitem give |" + group).add(" §c§l" + group + " §7§l>>").send(sender);
                information.forEach(pathTuple -> MessageUtil.getInst().builder().runCommand("/sxitem give |" + group + "#" + pathTuple.a() + "<")
                        .add("  §8[§a" + pathTuple.a() + "§8]§7 - Has §c" + pathTuple.b().size() + "§7 Item")
                        .show(pathTuple.b().stream().map(ig -> "§a" + ig.key + " §8[§7" + ig.getName() + "§8]§7 - §8[§cType:" + ig.getType() + "§8]").collect(Collectors.joining("\n")))
                        .send(sender));
            });
        } else {
            // 物品
            MessageUtil.getInst().builder()
                    .add("§eItemList§8 - §7ClickGet " + (search != null ? "§8[§c" + search.replaceAll("(^\\||<$)", "") + "§8]" : ""))
                    .show("§8§o§lTo DirectoryList")
                    .runCommand("/sxitem give")
                    .send(sender);
            List<MessageUtil.Builder> items = new ArrayList<>();
            informationMap.forEach((group, information) -> information.forEach(pathTuple -> pathTuple.b().forEach(ig -> {
                if (search == null || (ig.key + ig.getName() + "|" + ig.group + "#" + ig.getConfig().getString("Path") + "<").contains(search)) {
                    items.add(MessageUtil.getInst().builder()
                            .runCommand("/sxitem give " + ig.key)
                            .add(" §b" + (items.size() + 1) + " - §a" + ig.key + " §8[§7")
                            .add(ig.getNameComponent())
                            .add("§8]§7 - ")
                            .add("§8[§cT:" + ig.getType() + "§8]")
                            .show("§7" + ig.getConfigString()));
                }
            })));
            items.forEach(s -> s.send(sender));
            sender.sendMessage("§7Find §c" + items.size() + "§7 Items");
        }
    }

    private void ReLoadLink() {
        Iterator<Map.Entry<Tuple<String, String>, String>> iterator = linkMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Tuple<String, String>, String> entry = iterator.next();
            IGenerator generator = itemMap.get(entry.getKey().b());
            if (generator != null) {
                itemMap.put(entry.getKey().b(), generator);
            } else {
                itemMap.remove(entry.getKey().b());
                iterator.remove();
                plugin.getLogger().info("Linked Null: " + entry.getKey().a() + ":" + entry.getKey().b() + "->" + entry.getValue());
            }
        }
    }

    @EventHandler
    void on(PlayerItemHeldEvent event) {
        Inventory inv = event.getPlayer().getInventory();
        checkUpdateItem(event.getPlayer(), inv.getItem(event.getPreviousSlot()), inv.getItem(event.getNewSlot()));
    }

    @EventHandler
    void on(InventoryOpenEvent event) {
        if (event.getPlayer() instanceof Player) {
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

    private void loadConfigs(File directory, Map<String, ConfigurationSection> configs, String path) {
        for (File file : directory.listFiles()) {
            if (file.getName().startsWith("NoLoad")) continue;
            String filePath = !path.isEmpty() ? path + File.separator + file.getName() : file.getName();
            if (file.isDirectory()) {
                loadConfigs(file, configs, filePath);
            } else if (file.getName().endsWith(".yml")) {
                configs.put(filePath, YamlConfiguration.loadConfiguration(file));
            }
        }
    }
}