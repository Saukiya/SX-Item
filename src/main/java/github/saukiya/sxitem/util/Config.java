package github.saukiya.sxitem.util;

import github.saukiya.sxitem.SXItem;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.logging.Level;

/**
 * 管理本地化 Config.yml 的固定路径，并在重载时把运行参数同步到各功能模块。
 */
@Getter
public class Config {

    public static final String LOGGER_LEVEL = "LoggerLevel";
    public static final String LOGGER_RECORD = "LoggerRecord";
    public static final String DECIMAL_PRECISION = "DecimalPrecision";
    public static final String TIME_FORMAT = "TimeFormat";
    public static final String SCRIPT_ENGINE = "ScriptEngine";
    public static final String PROTECT_NBT = "ProtectNBT";
    public static final String GIVE_OVERFLOW_DROP = "GiveOverflowDrop";
    public static final String MOB_DROP_TO_PLAYER_INVENTORY = "MobDropToPlayerInventory";
    public static final String COMPATIBILITY_MYTHIC_MOBS = "Compatibility.MythicMobs";
    /** 以下路径分别控制合成台、附魔台和方块放置限制，缺省时必须按启用处理。 */
    public static final String RESTRICTION_CRAFTING_TABLE = "Restrictions.CraftingTable";
    public static final String RESTRICTION_ENCHANTING_TABLE = "Restrictions.EnchantingTable";
    public static final String RESTRICTION_BLOCK_PLACE = "Restrictions.BlockPlace";

    @Getter
    private static YamlConfiguration config;

    public static void loadConfig() {
        File file = new File(SXItem.getInst().getDataFolder(), "Config.yml");
        if (!file.exists()) {
            SXItem.getInst().getLogger().warning("File is not exists: Config.yml");
            config = new YamlConfiguration();
            return;
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public static void setup() {
        SXItem.getInst().getLogger().setLevel(Level.parse(config.getString(LOGGER_LEVEL, "INFO")));
        Util.decimalPrecision = Math.pow(10, Math.max(config.getInt(DECIMAL_PRECISION, 2), 0));
        SXItem.getItemManager().getProtectNbtList().clear();
        SXItem.getItemManager().getProtectNbtList().addAll(Config.getConfig().getStringList(Config.PROTECT_NBT));
    }
}
