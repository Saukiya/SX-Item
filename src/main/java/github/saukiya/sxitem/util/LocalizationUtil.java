package github.saukiya.sxitem.util;

import github.saukiya.sxitem.SXItem;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Files;

public class LocalizationUtil {

    private static final JavaPlugin plugin = SXItem.getInst();

    private static final String language = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource("plugin.yml"))).getString("language", "zh");

    public static void saveResource(String fileName) {
        int lastIndex = fileName.lastIndexOf(".");
        if (lastIndex != -1) {
            String name = fileName.substring(0, lastIndex);
            String suffix = fileName.substring(lastIndex);
            String localizationFileName = name + "_" + language + suffix;
            plugin.getLogger().config("SaveResource: " + fileName);
            try (InputStream in = plugin.getResource(localizationFileName)) {
                if (in != null) {
                    File outFile = new File(plugin.getDataFolder(), fileName);
                    File outDir = outFile.getParentFile();
                    if (outDir.exists() || outDir.mkdirs()) {
                        Files.copy(in, outFile.toPath());
                        return;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        plugin.saveResource(fileName, true);
    }
}
