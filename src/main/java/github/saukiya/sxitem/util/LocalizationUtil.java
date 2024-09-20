package github.saukiya.sxitem.util;

import github.saukiya.sxitem.SXItem;
import lombok.var;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Locale;

public class LocalizationUtil {

    private static final JavaPlugin plugin = SXItem.getInst();

    private static final String language = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource("plugin.yml"))).getString("language", Locale.getDefault().getLanguage());

    static {
        plugin.getLogger().info("Localization: " + language);
    }

    public static void saveResource(JavaPlugin plugin) {
        saveResource(plugin, "");
    }

    public static void saveResource(JavaPlugin plugin, String resourcePath) {
        try {
            extractResourceFolder(plugin, resourcePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void extractResourceFolder(JavaPlugin plugin, String resourcePath) throws IOException, URISyntaxException {
        var targetFolder = new File(plugin.getDataFolder(), resourcePath);
        var sourceUri = plugin.getClass().getResource("");
        try (FileSystem fileSystem = FileSystems.newFileSystem(sourceUri.toURI(), Collections.emptyMap())) {
            Path sourcePath = fileSystem.getPath("/Localization/" + language + '/' + resourcePath);
            for (Path source : Files.walk(sourcePath).toArray(Path[]::new)) {
                Path targetPath = targetFolder.toPath().resolve(sourcePath.relativize(source).toString());
                if (Files.exists(targetPath)) continue;
                plugin.getLogger().info("copy: " + targetPath);
                if (Files.isDirectory(source)) {
                    Files.createDirectory(targetPath);
                } else {
                    Files.copy(source, targetPath);
                }
            }
        }
    }
}
