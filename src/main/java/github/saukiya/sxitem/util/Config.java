package github.saukiya.sxitem.util;

import github.saukiya.sxitem.SXItem;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.text.DecimalFormat;
import java.util.logging.Level;

/**
 * @author Saukiya
 */

@Getter
public class Config {

    public static final String LOGGER_LEVEL = "LoggerLevel";
    public static final String ITEM_MAP_SORT = "ItemMapSort";
    public static final String DECIMAL_FORMAT = "DecimalFormat";
    public static final String TIME_FORMAT = "TimeFormat";

    @Getter
    private static YamlConfiguration config;

    /**
     * 加载Config类
     */
    public static void loadConfig() {
        File file = new File(SXItem.getInst().getDataFolder(), "Config.yml");
        if (!file.exists()) {
            SXItem.getInst().getLogger().info("Create Config.yml");
            SXItem.getInst().saveResource("Config.yml", true);
        }
        config = YamlConfiguration.loadConfiguration(file);
        SXItem.getInst().getLogger().setLevel(Level.parse(config.getString(LOGGER_LEVEL, "ALL")));
        SXItem.getItemManager().setItemMapSort(config.getBoolean(ITEM_MAP_SORT));
        SXItem.setDf(new DecimalFormat(config.getString(DECIMAL_FORMAT)));
    }
}
