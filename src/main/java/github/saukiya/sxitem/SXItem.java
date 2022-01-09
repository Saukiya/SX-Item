package github.saukiya.sxitem;

import github.saukiya.sxitem.command.MainCommand;
import github.saukiya.sxitem.data.item.ItemManager;
import github.saukiya.sxitem.data.item.sub.GeneratorDefault;
import github.saukiya.sxitem.data.item.sub.GeneratorImport;
import github.saukiya.sxitem.data.random.RandomManager;
import github.saukiya.sxitem.util.*;
import lombok.Getter;
import lombok.Setter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Random;

/**
 * @author Saukiya
 */

public class SXItem extends JavaPlugin {

    @Getter
    private static final ThreadLocal<SimpleDateFormat> sdf = ThreadLocal.withInitial(() -> new SimpleDateFormat(Config.getConfig().getString(Config.TIME_FORMAT)));
    @Getter
    private static final Random random = new Random();
    @Getter
    @Setter
    private static DecimalFormat df = new DecimalFormat("#.##");
    @Getter
    private static SXItem inst = null;
    @Getter
    private static MainCommand mainCommand;
    @Getter
    private static RandomManager randomManager;
    @Getter
    private static ItemManager itemManager;

    @Override
    public void onLoad() {
        inst = this;
        Config.loadConfig();
        Message.loadMessage();
        mainCommand = new MainCommand();

        ItemManager.loadMaterialData();
        ItemManager.registerGenerator(new GeneratorDefault());
        ItemManager.registerGenerator(new GeneratorImport());
    }

    @Override
    public void onEnable() {
        new Metrics(this, 11948);
        long oldTimes = System.currentTimeMillis();
        NbtUtil.getInst();
        ItemUtil.getInst();
        MessageUtil.getInst();

        randomManager = new RandomManager();
        itemManager = new ItemManager();

        Config.setup();
        PlaceholderUtil.setup();
        mainCommand.setup("sxitem");
        getLogger().info("Loading Time: " + (System.currentTimeMillis() - oldTimes) + " ms");
        getLogger().info("Author: Saukiya");
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(HumanEntity::closeInventory);
    }
}
