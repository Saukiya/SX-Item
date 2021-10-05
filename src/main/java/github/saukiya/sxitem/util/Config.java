package github.saukiya.sxitem.util;

import github.saukiya.sxitem.SXItem;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.text.DecimalFormat;

/**
 * @author Saukiya
 */

@Getter
public class Config {

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
        SXItem.setDf(new DecimalFormat(config.getString(DECIMAL_FORMAT)));
    }
}
