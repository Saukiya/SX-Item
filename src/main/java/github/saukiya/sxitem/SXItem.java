package github.saukiya.sxitem;

import github.saukiya.sxitem.command.*;
import github.saukiya.sxitem.data.ScriptManager;
import github.saukiya.sxitem.data.expression.ExpressionManager;
import github.saukiya.sxitem.data.expression.ExpressionSpace;
import github.saukiya.sxitem.data.expression.impl.*;
import github.saukiya.sxitem.data.item.ItemManager;
import github.saukiya.sxitem.data.item.sub.GeneratorDefault;
import github.saukiya.sxitem.data.item.sub.GeneratorImport;
import github.saukiya.sxitem.data.random.RandomManager;
import github.saukiya.sxitem.helper.MythicMobsHelper;
import github.saukiya.sxitem.util.Config;
import github.saukiya.sxitem.util.Message;
import github.saukiya.util.command.MainCommand;
import github.saukiya.util.common.LocalizationUtil;
import github.saukiya.util.common.LogUtil;
import github.saukiya.util.helper.PlaceholderHelper;
import github.saukiya.util.nms.*;
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
    @Deprecated
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
        LocalizationUtil.saveResource(this);
        Config.loadConfig();
        Message.loadMessage();
        mainCommand = new MainCommand(this, Message::getStatic);
        mainCommand.register(new GiveCommand());
        mainCommand.register(new SaveCommand());
        mainCommand.register(new InfoCommand());
        mainCommand.register(new NBTCommand());
        if (NMS.compareTo(1,20,5) >= 0) {
            mainCommand.register(new ComponentCommand());
        }
        mainCommand.register(new ScriptCommand());
        mainCommand.register(new ReloadCommand());
        mainCommand.register(new TestCommand());

        ItemManager.loadMaterialData();
        ItemManager.register("Default", GeneratorDefault::new, GeneratorDefault::save);
        ItemManager.register("Import", GeneratorImport::new, GeneratorImport::save);

        ExpressionManager.register(new BooleanExpression(), 'b');
        ExpressionManager.register(new CalculatorExpression(), 'c');
        ExpressionManager.register(new LockStringExpression(), 'l');
        ExpressionManager.register(new StringRandomExpression(), 's');
        ExpressionManager.register(new TimeExpression(), 't');
        ExpressionManager.register(new DoubleExpression(), 'd');
        ExpressionManager.register(new IntExpression(), 'i', 'r');
        ExpressionManager.register(new ScriptExpression(), 'j');
        ExpressionManager.register(new UUIDExpression(), 'u');
        ExpressionManager.register(new MaxExpression(), "max");
        ExpressionManager.register(new MinExpression(), "min");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onEnable() {
        new Metrics(this, 11948);
        long oldTimes = System.currentTimeMillis();

        ComponentUtil.getInst();
        NbtUtil.getInst();
        ItemUtil.getInst();
        MessageUtil.getInst();

        scriptManager = new ScriptManager(this);
        randomManager = new RandomManager(this);
        itemManager = new ItemManager(this);

        Config.setup();
        PlaceholderHelper.setup(this, (player, params) -> ExpressionSpace.getInst().replace(params));
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
