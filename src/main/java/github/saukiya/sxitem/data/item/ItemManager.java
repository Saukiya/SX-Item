package github.saukiya.sxitem.data.item;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.nms.NBTItemWrapper;
import github.saukiya.sxitem.nms.NBTTagWrapper;
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

    private final File itemFiles = new File(SXItem.getInst().getDataFolder(), "Item");

    private final Map<String, IGenerator> itemMap = new HashMap<>();

    private final Map<String, String> linkMap = new HashMap<>();

    @Getter
    private final Map<String, Material> materialMap = new HashMap<>();

    //? 抱有疑问的线程安全
    private final List<Player> checkPlayers = new ArrayList<>();

    public ItemManager() {
        SXItem.getInst().getLogger().info("Loaded " + generators.size() + " ItemGenerators");
        loadMaterialData();
        loadItemData();
        Bukkit.getPluginManager().registerEvents(this, SXItem.getInst());
        Bukkit.getScheduler().runTaskTimer(SXItem.getInst(), () -> {
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

    /**
     * 读取Material数据
     */
    @SneakyThrows
    public void loadMaterialData() {
        materialMap.clear();
        File file = new File(SXItem.getInst().getDataFolder(), "Material.yml");
        if (!file.exists()) {
            SXItem.getInst().saveResource("Material.yml", true);
        }
        boolean change = false;
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        for (Map.Entry<String, Object> entry : yaml.getValues(false).entrySet()) {
            Material material = Material.getMaterial(entry.getKey());
            try {
                if (material == null) {
                    if (NMS.compareTo("v1_13_R1") >= 0)
                        material = Material.getMaterial(entry.getKey(), true);
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
                }
            } catch (Exception ignored) {
                SXItem.getInst().getLogger().warning("Material.yml No Material - " + entry.getKey());
                continue;
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
        if (change) {
            yaml.save(file);
        }
        SXItem.getInst().getLogger().info("Loaded " + materialMap.size() + " Materials");
    }

    /**
     * 获取物品材质
     *
     * @param key 索引
     * @return
     */
    public Material getMaterial(String key) {
        Material material = materialMap.get(key);
        return material != null ? material : Material.getMaterial(key.replace(' ', '_').toUpperCase(Locale.ROOT));
    }

    public Set<String> getMaterialString(Material value) {
        return materialMap.entrySet().stream().filter(e -> e.getValue().equals(value)).map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    /**
     * 读取物品数据
     */
    public void loadItemData() {
        itemMap.clear();
        linkMap.clear();
        if (!itemFiles.exists() || itemFiles.listFiles().length == 0) {
            SXItem.getInst().saveResource("Item/Default/Default.yml", true);
            SXItem.getInst().saveResource("Item/NoLoad/Default.yml", true);
        }
        loadItem(itemFiles);
        SXItem.getInst().getLogger().info("Loaded " + itemMap.size() + " Items");
        linkMap.forEach((k, v) -> {
            IGenerator ig = itemMap.get(v);
            if (ig != null) {
                itemMap.put(k, ig);
            } else {
                linkMap.remove(k);
                SXItem.getInst().getLogger().info("Linked Error: " + k + "->" + v);
            }
        });
        SXItem.getInst().getLogger().info("Linked " + linkMap.size() + " Items");
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
                        SXItem.getInst().getLogger().warning("Don't Repeat Item Name: " + file.getName() + File.separator + key + " !");
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
                        SXItem.getInst().getLogger().warning("Don't Item Type: " + file.getName() + File.separator + key + " - " + type + " !");
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
    public ItemStack getItem(String itemName, Player player) {
        IGenerator ig = itemMap.get(itemName);
        if (ig != null) {
            return getItem(ig, player);
        } else {
            Material material = getMaterial(itemName);
            if (material != null) {
                return new ItemStack(material);
            }
        }
        return null;
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
        if (ig instanceof IUpdate) {
            NBTItemWrapper wrapper = NbtUtil.getInst().getItemTagWrapper(item);
            wrapper.set(SXItem.getInst().getName() + ".ItemKey", ig.getKey());
            wrapper.set(SXItem.getInst().getName() + ".HashCode", ((IUpdate) ig).updateCode());
            wrapper.save();
        }
        return item;
    }

    /**
     * 返回是否存在物品
     *
     * @param itemName String
     * @return boolean
     */
    public boolean hasItem(String itemName) {
        return itemMap.containsKey(itemName) || Material.getMaterial(itemName.replace(' ', '_').toUpperCase()) != null;
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
            // TODO 添加兼容设置->需测试
            if (!updateItemInPlugin(player, item, SXItem.getInst().getName() + ".ItemKey", SXItem.getInst().getName() + ".HashCode", wrapper)) {
                updateItemInPlugin(player, item, "SX-Attribute-Name", "SX-Attribute-HashCode", wrapper);
            }
        }
    }

    private boolean updateItemInPlugin(Player player, ItemStack item, String keyName, String hashCodeName, NBTTagWrapper oldWrapper) {
        if (item == null) return false;
        if (oldWrapper == null) oldWrapper = NbtUtil.getInst().getItemTagWrapper(item);
        IGenerator ig = itemMap.get(oldWrapper.getString(keyName));
        if (ig == null) return false;
        if (ig instanceof IUpdate) {
            IUpdate updateIg = (IUpdate) ig;
            Integer hashCode = oldWrapper.getInt(hashCodeName);
            if (updateIg.isUpdate() && (hashCode == null || updateIg.updateCode() != hashCode)) {
                ItemStack newItem = updateIg.update(item, oldWrapper, player);
                item.setType(newItem.getType());
                item.setItemMeta(newItem.getItemMeta());
                NBTItemWrapper itemWrapper = NbtUtil.getInst().getItemTagWrapper(item);
                itemWrapper.set(SXItem.getInst().getName() + ".ItemKey", ig.getKey());
                itemWrapper.set(SXItem.getInst().getName() + ".HashCode", ((IUpdate) ig).updateCode());
                itemWrapper.save();
            }
        }
        return true;
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
    public void sendItemMapToPlayer(CommandSender sender, String... search) {
        sender.sendMessage("");
        // 文件
        if (search.length > 0 && search[0].equals("")) {
            MessageUtil.getInst().componentBuilder().add("§eDirectoryList§8 - §7ClickOpen").show("§8§o§lTo ItemList").runCommand("/sxitem give |").send(sender);

//            Map<String, String> map = new HashMap<>();
//            for (Map.Entry<String, IGenerator> entry : itemMap.entrySet()) {
//                IGenerator ig = entry.getValue();
//                String str = map.computeIfAbsent(ig.getPathName(), k -> "");
//                map.put(ig.getPathName(), str + "§b" + (str.replaceAll("[^\n]", "").length() + 1) + " - §a" + entry.getKey() + " §8[§7" + ig.getName() + "§8]§7 - §8[§cType:" + ig.getType() + "§8]\n");
//            }
            Map<String, List<String>> map = new HashMap<>();
            for (Map.Entry<String, IGenerator> entry : itemMap.entrySet()) {
                IGenerator ig = entry.getValue();
                List<String> list = map.computeIfAbsent(ig.getPathName(), k -> new ArrayList<>());
                list.add("§b" + list.size() + 1 + " - §a" + entry.getKey() + " §8[§7" + ig.getName() + "§8]§7 - §8[§cType:" + ig.getType() + "§8]");
            }
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                MessageUtil.getInst().componentBuilder().runCommand("/sxitem give |" + entry.getKey() + "<")
                        .add(" §8[§c" + entry.getKey().replace(">", "§b>§c") + "§8]")
                        .add("§7 - Has §c" + entry.getValue().size() + "§7 Item")
                        .show(String.join("\n", entry.getValue()))
                        .send(sender);
            }
        } else
        // 物品
        {
            int filterSize = 0, size = 0;
            MessageUtil.getInst().componentBuilder()
                    .add("§eItemList§8 - §7ClickGet " + (search.length > 0 ? "§8[§c" + search[0].replaceAll("^\\|", "").replaceAll("<$", "") + "§8]" : ""))
                    .show("§8§o§lTo DirectoryList")
                    .runCommand("/sxitem give")
                    .send(sender);
            for (Map.Entry<String, IGenerator> entry : itemMap.entrySet()) {
                IGenerator ig = entry.getValue();
                if (search.length > 0 && !(entry.getKey() + ig.getName() + "|" + ig.getPathName() + "<").contains(search[0])) {
                    filterSize++;
                    continue;
                }
                MessageUtil.getInst().componentBuilder()
                        .runCommand("/sxitem give " + entry.getKey())
                        .add(" §b" + ++size + " - §a" + entry.getKey() + " §8[§7")
                        .add(ig.getNameComponent())
                        .add("§8]§7 - ")
                        .add("§8[§cType:" + ig.getType() + "§8]")
                        .show("§7" + ig.getConfigString() + "§8§o§lPath: " + ig.getPathName())
                        .send(sender);
            }
            if (search.length > 0 && filterSize != 0) {
                sender.sendMessage("§7> Filter§c " + filterSize + " §7Items, Find §c" + size + "§7 Items.");
            }
        }
        sender.sendMessage("");
    }

    @EventHandler
    void on(PlayerItemHeldEvent event) {
        Inventory inv = event.getPlayer().getInventory();
        ItemStack oldItem = inv.getItem(event.getPreviousSlot());
        ItemStack newItem = inv.getItem(event.getNewSlot());
        updateItem(event.getPlayer(), oldItem, newItem);
    }

    @EventHandler
    void on(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inv = event.getInventory();
        if (player.equals(inv.getHolder())) {
            checkPlayers.add(player);
        }
    }

    @EventHandler
    void on(PlayerJoinEvent event) {
        checkPlayers.add(event.getPlayer());
    }
}