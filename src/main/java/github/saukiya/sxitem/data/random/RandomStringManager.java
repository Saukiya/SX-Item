package github.saukiya.sxitem.data.random;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.item.IGenerator;
import github.saukiya.sxitem.data.item.IUpdate;
import github.saukiya.sxitem.util.Message;
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
public class RandomStringManager {
    @Getter
    private static final List<IGenerator> generators = new ArrayList<>();

    private final File file = new File(SXItem.getInst().getDataFolder(), "RandomString");

    private final Map<String, List<String>> map = new HashMap<>();

    public RandomStringManager() {
        loadData();
    }
    public void loadData() {
        map.clear();
        if (!file.exists() || Objects.requireNonNull(file.listFiles()).length == 0) {
            SXItem.getInst().saveResource("RandomString/DefaultRandom.yml", true);
            SXItem.getInst().saveResource("RandomString/10Level/Random.yml", true);
        }
        loadRandom(file);
        SXItem.getInst().getLogger().info("Loaded " + map.size() + " RandomString");
    }
    /**
     * 遍历读取随机字符串数据
     *
     * @param files File
     */
    private void loadRandom(File files) {
        for (File file : Objects.requireNonNull(files.listFiles())) {
            if (file.isDirectory()) {
                loadRandom(file);
            } else {
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                for (String name : yml.getKeys(false)) {
                    if (map.containsKey(name)) {
                        SXItem.getInst().getLogger().info("不要重复随机字符组名: " + file.getName().replace("plugins" + File.separator + SXItem.getInst().getName() + File.separator, "") + File.separator + name + " !");
                    }
                    map.put(name, loadRandomData(yml.get(name)));
                }
            }
        }
    }

    public List<String> loadRandomData(Object obj) {
        if (obj instanceof String) {
            return Collections.singletonList(obj.toString());
        }
        List<String> list = new ArrayList<>();
        if (obj instanceof List) {
            for (Object objs : (List) obj) {
                if (objs instanceof List ) {
                    list.add(String.join("/n", (Iterable<? extends CharSequence>) objs));
                } else {
                    list.add(objs.toString());
                }
            }
        }
        return list;
    }
//    /**
//     * 注册物品生成器
//     *
//     * @param generator ItemGenerator
//     */
//    public static void registerGenerator(IGenerator generator) {
//        if (generator.getType() == null || generators.stream().anyMatch((ig) -> ig.getType().equals(generator.getType()))) {
//            SXItem.getInst().getLogger().warning("ItemGenerator >>  [" + generator.getClass().getSimpleName() + "] Type Error!");
//            return;
//        }
//        generators.add(generator);
//        SXItem.getInst().getLogger().info("ItemGenerator >> Register [" + generator.getClass().getSimpleName() + "] To Type " + generator.getType() + " !");
//    }
}