package github.saukiya.sxitem;

import github.saukiya.sxitem.command.*;
import github.saukiya.sxitem.data.ScriptManager;
import github.saukiya.sxitem.data.expression.ExpressionHandler;
import github.saukiya.sxitem.data.expression.ExpressionManager;
import github.saukiya.sxitem.data.expression.impl.*;
import github.saukiya.sxitem.data.item.ItemManager;
import github.saukiya.sxitem.data.item.impl.GeneratorDefault;
import github.saukiya.sxitem.data.item.impl.GeneratorImport;
import github.saukiya.sxitem.data.random.RandomManager;
import github.saukiya.sxitem.helper.MythicMobsHelper;
import github.saukiya.sxitem.util.Config;
import github.saukiya.sxitem.util.Message;
import github.saukiya.tools.command.MainCommand;
import github.saukiya.tools.helper.PlaceholderHelper;
import github.saukiya.tools.nms.ComponentUtil;
import github.saukiya.tools.nms.ItemUtil;
import github.saukiya.tools.nms.MessageUtil;
import github.saukiya.tools.nms.NbtUtil;
import github.saukiya.tools.util.LocalizationUtil;
import github.saukiya.tools.util.LogUtil;
import github.saukiya.tools.util.ReMaterial;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Random;

public class SXItem extends JavaPlugin {

    @Getter
    private static final ThreadLocal<SimpleDateFormat> sdf = ThreadLocal.withInitial(() -> new SimpleDateFormat(Config.getConfig().getString(Config.TIME_FORMAT, "yyyy/MM/dd HH:mm")));
    @Getter
    private static final Random random = new Random();
    @Getter
    @Deprecated
    private static final DecimalFormat df = new DecimalFormat("#.##");
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

    @SneakyThrows
    @Override
    public void onLoad() {
        inst = this;
        LocalizationUtil.saveResource(this);
        Config.loadConfig();
        Message.loadMessage();
        mainCommand = new MainCommand(this, Message::getString);
        mainCommand.register(new GiveCommand());
        mainCommand.register(new SaveCommand());
        mainCommand.register(new InfoCommand());
        mainCommand.register(new NBTCommand());
        mainCommand.register(new ComponentCommand());
        mainCommand.register(new ScriptCommand());
        mainCommand.register(new ReloadCommand());
        mainCommand.register(new TestCommand());

        ItemManager.register("Default", GeneratorDefault::new, GeneratorDefault::save);
        ItemManager.register("Import", GeneratorImport::new, GeneratorImport::save);

        ExpressionManager.register(new BooleanExpression(), 'b');
        ExpressionManager.register(new CalculatorExpression(), 'c');
        ExpressionManager.register(new LockStringExpression(), 'l');
        ExpressionManager.register(new StringRandomExpression(), 's');
        ExpressionManager.register(new TimeExpression(), 't');
        ExpressionManager.register(new DecimalExpression(), 'd');
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
        LogUtil.setup(this, Config.getConfig().getBoolean(Config.LOGGER_RECORD, true));

        ReMaterial.values();
        ComponentUtil.getInst();
        NbtUtil.getInst();
        ItemUtil.getInst();
        MessageUtil.getInst();

        scriptManager = new ScriptManager(this, Config.getConfig().getString(Config.SCRIPT_ENGINE, "js"));
        randomManager = new RandomManager(this);
        itemManager = new ItemManager(this);

        Config.setup();
        PlaceholderHelper.setup(this, (player, params) -> ExpressionHandler.getInst().replace(params));
        MythicMobsHelper.setup();
        mainCommand.onEnable("SxItem");
        getLogger().info("Loading Time: " + (System.currentTimeMillis() - oldTimes) + " ms");
        getLogger().info("Author: " + getDescription().getAuthors());
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(HumanEntity::closeInventory);
        mainCommand.onDisable();
    }
}
