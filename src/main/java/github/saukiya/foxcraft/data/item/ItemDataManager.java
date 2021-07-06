package github.saukiya.foxcraft.data.item;

import github.saukiya.foxcraft.SXItem;
import github.saukiya.foxcraft.util.Message;
import lombok.Getter;
import net.md_5.bungee.api.chat.TextComponent;
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

/**
 * @author Saukiya
 */
public class ItemDataManager implements Listener {
    @Getter
    private static final List<IGenerator> generators = new ArrayList<>();

    private final File itemFiles = new File(SXItem.getInst().getDataFolder(), "Item");

    private final Map<String, IGenerator> itemMap = new HashMap<>();

    private final List<Player> checkPlayers = new ArrayList<>();

    public ItemDataManager() {
        SXItem.getInst().getLogger().info("Loaded " + generators.size() + " ItemGenerators");
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
        if (generator.getType() == null || generators.stream().anyMatch((ig) -> ig.getType().equals(generator.getType()))) {
            SXItem.getInst().getLogger().warning("ItemGenerator >>  [" + generator.getClass().getSimpleName() + "] Type Error!");
            return;
        }
        generators.add(generator);
        SXItem.getInst().getLogger().info("ItemGenerator >> Register [" + generator.getClass().getSimpleName() + "] To Type " + generator.getType() + " !");
    }

    /**
     * 读取物品数据
     */
    public void loadItemData() {
        itemMap.clear();
        if (!itemFiles.exists() || Objects.requireNonNull(itemFiles.listFiles()).length == 0) {
            SXItem.getInst().saveResource("Item/head/head.yml", true);
            SXItem.getInst().saveResource("Item/kit/kit.yml", true);
            SXItem.getInst().saveResource("Item/Test.yml", true);
            SXItem.getInst().saveResource("Item/NoLoad/Default.yml", true);
        }
        loadItem(itemFiles);
        SXItem.getInst().getLogger().info("Loaded " + itemMap.size() + " Items");
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
        for (File file : Objects.requireNonNull(files.listFiles())) {
            if (file.isDirectory()) {
                if (!file.getName().startsWith("NoLoad")) {
                    loadItem(file);
                }
            } else {
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
                do_1:
                for (String key : yaml.getKeys(false)) {
                    if (itemMap.containsKey(key)) {
                        SXItem.getInst().getLogger().warning("Don't Repeat Item Name: " + file.getName() + File.separator + key + " !");
                        continue;
                    }
                    String pathName = getPathName(files);
                    String type = yaml.getString(key + ".Type", "Default");
                    for (IGenerator generator : generators) {
                        if (generator.getType().equals(type)) {
                            itemMap.put(key, generator.newGenerator(pathName, key, yaml.getConfigurationSection(key)));
                            continue do_1;
                        }
                    }
                    SXItem.getInst().getLogger().warning("Don't Item Type: " + file.getName() + File.separator + key + " - " + type + " !");
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
        return file.toString().replace("plugins" + File.separator + "Foxcraft" + File.separator, "").replace(File.separator, ">");
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
            ItemStack item = ig.getItem(player);
            if (ig instanceof IUpdate) {
                SXItem.getNbtUtil().setNBT(item, SXItem.getInst().getName() + "-Name", ig.getKey());
                SXItem.getNbtUtil().setNBT(item, SXItem.getInst().getName() + "-HashCode", ((IUpdate) ig).updateCode());
            }
            return item;
        } else {
            Material material = Material.getMaterial(itemName.replace(' ', '_').toUpperCase());
            if (material != null) {
                return new ItemStack(material);
            }
        }
        return null;
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
     * @param player   Player
     * @param oldItems ItemStack...
     */
    public void updateItem(Player player, ItemStack... oldItems) {
        for (ItemStack oldItem : oldItems) {
            String dataName;
            if (oldItem != null && (dataName = SXItem.getNbtUtil().getNBT(oldItem, SXItem.getInst().getName() + "-Name")) != null) {
                IGenerator ig = itemMap.get(dataName);
                if (ig instanceof IUpdate && ((IUpdate) ig).isUpdate() && SXItem.getNbtUtil().hasNBT(oldItem, SXItem.getInst().getName() + "-HashCode")) {
                    int hashCode = Integer.parseInt(SXItem.getNbtUtil().getNBT(oldItem, SXItem.getInst().getName() + "-HashCode"));
                    if (((IUpdate) ig).updateCode() != hashCode) {
                        ItemStack item = ig.getItem(player);
                        SXItem.getNbtUtil().setNBT(item, SXItem.getInst().getName() + "-Name", ig.getKey());
                        SXItem.getNbtUtil().setNBT(item, SXItem.getInst().getName() + "-HashCode", ((IUpdate) ig).updateCode());
                        oldItem.setType(item.getType());
                        oldItem.setItemMeta(item.getItemMeta());
                    }
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
        for (IGenerator ig : generators) {
            if (ig.getType().equals(type)) {
                ConfigurationSection config = new MemoryConfiguration();
                config.set("Type", ig.getType());
                config = ig.saveItem(item, config);
                if (config != null) {
                    File file = new File(itemFiles, "Type-" + ig.getType() + File.separator + "Item.yml");
                    YamlConfiguration yaml = file.exists() ? YamlConfiguration.loadConfiguration(file) : new YamlConfiguration();
                    yaml.set(key, config);
                    yaml.save(file);
                    itemMap.put(key, ig.newGenerator(getPathName(file.getParentFile()), key, config));
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 发送物品列表给指令者
     *
     * @param sender CommandSender
     * @param search String
     */
    public void sendItemMapToPlayer(CommandSender sender, String... search) {
        sender.sendMessage("");
        int filterSize = 0, size = 0;
        // 文件
        if (search.length > 0 && search[0].equals("")) {
            sender.spigot().sendMessage(Message.getTextComponent("§eDirectoryList§8 - §7ClickOpen", "/fox give |", "§8§o§lTo ItemList"));

            Map<String, String> map = new HashMap<>();
            for (IGenerator ig : itemMap.values()) {
                String str = map.computeIfAbsent(ig.getPathName(), k -> "");
                map.put(ig.getPathName(), str + "§b" + (str.replaceAll("[^\n]", "").length() + 1) + " - §a" + ig.getKey() + " §8[§7" + ig.getName() + "§8]§7 - §8[§cType:" + ig.getType() + "§8]\n");
            }
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey(), value = entry.getValue(), command = "/fox give |" + key + "<";
                TextComponent tc = Message.getTextComponent(" §8[§c" + key.replace(">", "§b>§c") + "§8]", command, null);
                tc.addExtra(Message.getTextComponent("§7 - Has §c" + value.split("\n").length + "§7 Item", command, value.substring(0, value.length() - 1)));
                sender.spigot().sendMessage(tc);
            }
        } else
        // 物品
        {
            sender.spigot().sendMessage(Message.getTextComponent("§eItemList§8 - §7ClickGet " + (search.length > 0 ? "§8[§c" + search[0].replaceAll("^\\|", "").replaceAll("<$", "") + "§8]" : ""), "/fox give", "§8§o§lTo DirectoryList"));

            String right = "§8]§7 - ";
            for (IGenerator ig : itemMap.values()) {
                String left = " §b" + size + " - §a" + ig.getKey() + " §8[§7";
                if (search.length > 0 && !(left + ig.getName() + "|" + ig.getPathName() + "<").contains(search[0])) {
                    filterSize++;
                    continue;
                }

                String end = "§8[§cType:" + ig.getType() + "§8]";
                size++;
                if (sender instanceof Player) {
                    TextComponent tc = Message.getTextComponent(left, "/fox give " + ig.getKey(), null);
                    tc.addExtra(ig.getNameComponent());
                    tc.addExtra(right);
                    YamlConfiguration yaml = new YamlConfiguration();
                    ig.getConfig().getValues(false).forEach(yaml::set);
                    tc.addExtra(Message.getTextComponent(end, "/fox give " + ig.getKey(), "§7" + yaml.saveToString() + "§8§o§lPath: " + ig.getPathName()));
                    sender.spigot().sendMessage(tc);
                } else {
                    sender.sendMessage(left + ig.getName() + right + end);
                }
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
    void onInventoryCloseEvent(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inv = event.getInventory();
        if (player.equals(inv.getHolder()) && !checkPlayers.contains(player)) {
            checkPlayers.add(player);
        }
    }


    @EventHandler
    void onPlayerJoinEvent(PlayerJoinEvent event) {
        if (!checkPlayers.contains(event.getPlayer())) {
            checkPlayers.add(event.getPlayer());
        }
    }
}