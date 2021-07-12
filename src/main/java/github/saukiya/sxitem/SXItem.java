package github.saukiya.sxitem;

import github.saukiya.sxitem.command.MainCommand;
import github.saukiya.sxitem.data.item.ItemDataManager;
import github.saukiya.sxitem.data.item.sub.GeneratorDefault;
import github.saukiya.sxitem.data.item.sub.GeneratorImport;
import github.saukiya.sxitem.data.player.PlayerDataManager;
import github.saukiya.sxitem.util.Config;
import github.saukiya.sxitem.util.Message;
import github.saukiya.sxitem.util.NbtUtil;
import github.saukiya.sxitem.util.Placeholders;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.inventory.util.CraftCustomInventoryConverter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.java.JavaPlugin;

import javax.script.ScriptEngineManager;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * @author Saukiya
 */

public class SXItem extends JavaPlugin {

    @Getter
    private static final DecimalFormat df = new DecimalFormat("#.##");

    @Getter
    private static final Random random = new Random();

    @Getter
    private static final ScriptEngineManager jsManager = new ScriptEngineManager();

    @Getter
    private static SXItem inst = null;

    @Getter
    private static boolean placeholderApi = false;

    @Getter
    private static final boolean vault = false;

    @Getter
    private static MainCommand mainCommand;

    @Getter
    private static ItemDataManager itemDataManager;

    @Getter
    private static PlayerDataManager playerDataManager;

    @Getter
    private static CraftCustomInventoryConverter converter;

    @Getter
    private static NbtUtil nbtUtil;

    @Override
    public void onLoad() {
        inst = this;
        Config.loadConfig();
        Message.loadMessage();
        mainCommand = new MainCommand();

        ItemDataManager.registerGenerator(new GeneratorDefault());
        ItemDataManager.registerGenerator(new GeneratorImport());
    }

    @Override
    public void onEnable() {
        Metrics metrics = new Metrics(this, 11948);
        long oldTimes = System.currentTimeMillis();
        converter = new CraftCustomInventoryConverter();
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            Placeholders.setup();
            placeholderApi = true;
            getLogger().info("Find PlaceholderAPI");
        } else {
            getLogger().warning("No Find PlaceholderAPI");
        }
        nbtUtil = new NbtUtil();
        itemDataManager = new ItemDataManager();
        playerDataManager = new PlayerDataManager();

        mainCommand.setup("sxitem");
        getLogger().info("Loading Time: " + (System.currentTimeMillis() - oldTimes) + " ms");
        getLogger().info("Author: Saukiya");
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(HumanEntity::closeInventory);
    }
}
