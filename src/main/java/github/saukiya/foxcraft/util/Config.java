package github.saukiya.foxcraft.util;

import github.saukiya.foxcraft.SXItem;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Saukiya
 */

@Getter
public class Config {

    @Getter
    private static final Map<String, Config> configs = new HashMap<>();

    private final String key;
    private final File file;
    private YamlConfiguration config;

    private Config(String key) {
        this.key = key;
        this.file = new File(SXItem.getInst().getDataFolder(), key + ".yml");
        load();

    }

    public static void regConfig(String key) {
        configs.put(key, new Config(key));
    }

    public static YamlConfiguration getConfig(String key) {
        return configs.get(key).getConfig();
    }

    /**
     * 加载Config类
     */
    public static void loadConfig() {
        for (Config config : configs.values()) {
            config.load();
        }
    }

    void load() {
        if (!file.exists()) {
            SXItem.getInst().getLogger().info("create " + key + ".yml");
            SXItem.getInst().saveResource(key + ".yml", true);
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }
}
