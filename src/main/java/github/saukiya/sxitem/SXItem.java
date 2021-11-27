package github.saukiya.sxitem;

import github.saukiya.sxitem.command.MainCommand;
import github.saukiya.sxitem.data.item.ItemDataManager;
import github.saukiya.sxitem.data.item.sub.GeneratorDefault;
import github.saukiya.sxitem.data.item.sub.GeneratorImport;
import github.saukiya.sxitem.data.random.RandomStringManager;
import github.saukiya.sxitem.util.Config;
import github.saukiya.sxitem.util.Message;
import github.saukiya.sxitem.util.NbtUtilDeprecated;
import github.saukiya.sxitem.util.Placeholders;
import lombok.Getter;
import lombok.Setter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.java.JavaPlugin;

import javax.script.ScriptEngineManager;
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
    @Setter
    private static DecimalFormat df = new DecimalFormat("#.##");

    @Getter
    private static final Random random = new Random();

    @Getter
    private static final ScriptEngineManager jsManager = new ScriptEngineManager();

    @Getter
    private static SXItem inst = null;

    @Getter
    private static MainCommand mainCommand;

    @Getter
    private static RandomStringManager randomStringManager;

    @Getter
    private static ItemDataManager itemDataManager;

    @Getter
    private static NbtUtilDeprecated nbtUtil;

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
        new Metrics(this, 11948);
        long oldTimes = System.currentTimeMillis();
        Placeholders.setup();
        nbtUtil = new NbtUtilDeprecated();

        randomStringManager = new RandomStringManager();
        itemDataManager = new ItemDataManager();

        mainCommand.setup("sxitem");
        getLogger().info("Loading Time: " + (System.currentTimeMillis() - oldTimes) + " ms");
        getLogger().info("Author: Saukiya");
        getLogger().info("File: " + getDataFolder());
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(HumanEntity::closeInventory);
    }
}
