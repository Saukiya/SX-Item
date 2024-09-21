package github.saukiya.sxitem;

import github.saukiya.sxitem.command.*;
import github.saukiya.sxitem.data.ScriptManager;
import github.saukiya.sxitem.data.item.ItemManager;
import github.saukiya.sxitem.data.item.sub.GeneratorDefault;
import github.saukiya.sxitem.data.item.sub.GeneratorImport;
import github.saukiya.sxitem.data.random.RandomDocker;
import github.saukiya.sxitem.data.random.RandomManager;
import github.saukiya.sxitem.data.random.randoms.*;
import github.saukiya.sxitem.helper.MythicMobsHelper;
import github.saukiya.sxitem.util.Config;
import github.saukiya.sxitem.util.Message;
import github.saukiya.util.command.MainCommand;
import github.saukiya.util.common.LocalizationUtil;
import github.saukiya.util.common.LogUtil;
import github.saukiya.util.helper.PlaceholderHelper;
import github.saukiya.util.nms.ComponentUtil;
import github.saukiya.util.nms.ItemUtil;
import github.saukiya.util.nms.MessageUtil;
import github.saukiya.util.nms.NbtUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
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
    private static final ThreadLocal<SimpleDateFormat> sdf = ThreadLocal.withInitial(() -> new SimpleDateFormat(Config.getConfig().getString(Config.TIME_FORMAT, "yyyy/MM/dd HH:mm")));
    @Getter
    private static final Random random = new Random();
    @Setter
    @Getter
    private static DecimalFormat df = new DecimalFormat("#.##");
    @Getter
    private static SXItem inst;
    @Getter
    private static MainCommand mainCommand;
    @Getter
    private static ScriptManager scriptManager;
    @Getter
    private static RandomManager randomManager;
    @Getter
    private static ItemManager itemManager;

    private static LogUtil logUtil;

    @SneakyThrows
    @Override
    public void onLoad() {
        inst = this;
        logUtil = new LogUtil(inst);
        LocalizationUtil.saveResource(this, "zh", "en");
        Config.loadConfig();
        Message.loadMessage();
        mainCommand = new MainCommand(this, Message::getStatic);
        mainCommand.register(new GiveCommand());
        mainCommand.register(new SaveCommand());
        mainCommand.register(new NBTCommand());
        mainCommand.register(new ScriptCommand());
        mainCommand.register(new ReloadCommand());
        mainCommand.register(new TestCommand());

        ItemManager.loadMaterialData();
        ItemManager.register("Default", GeneratorDefault::new, GeneratorDefault.saveFunc());
        ItemManager.register("Import", GeneratorImport::new, GeneratorImport.saveFunc());

        RandomManager.register(new BooleanRandom(), 'b');
        RandomManager.register(new CalculatorRandom(), 'c');
        RandomManager.register(new LockStringRandom(), 'l');
        RandomManager.register(new StringRandom(), 's');
        RandomManager.register(new TimeRandom(), 't');
        RandomManager.register(new DoubleRandom(), 'd');
        RandomManager.register(new IntRandom(), 'i', 'r');
        RandomManager.register(new ScriptRandom(), 'j');
        RandomManager.register(new UUIDRandom(), 'u');
    }

    @Override
    public void onEnable() {
        new Metrics(this, 11948);
        long oldTimes = System.currentTimeMillis();
        NbtUtil.getInst();
        ComponentUtil.getInst();

        ItemUtil.getInst();
        MessageUtil.getInst();

        scriptManager = new ScriptManager(this);
        randomManager = new RandomManager(this);
        itemManager = new ItemManager(this);

        Config.setup();
        PlaceholderHelper.setup(this, (player, params) -> RandomDocker.getInst().replace(params));
        MythicMobsHelper.setup();
        mainCommand.onEnable("SxItem");
        getLogger().info("Loading Time: " + (System.currentTimeMillis() - oldTimes) + " ms");
        getLogger().info("Author: " + getDescription().getAuthors());
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(HumanEntity::closeInventory);
        mainCommand.onDisable();
        logUtil.onDisable();
    }
}
