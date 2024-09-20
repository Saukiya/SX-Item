package github.saukiya.sxitem.util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

/**
 * 本地化工具
 */
public class LocalizationUtil {

    private static final String systemLanguage = Locale.getDefault().getLanguage();

    /**
     * <pre>
     * 保存本地化资源到插件文件夹中
     * 会将插件内的"Localization/{language}/"中的资源保存到本地
     * 如果当前语言不支持，则会选择supportLanguage中的首个语言作为替代
     * 如果文件已存在，则不会进行覆盖。
     * </pre>
     *
     * @param plugin 当前插件
     * @param supportLanguage 受支持的语言 (zh..en..)
     */
    public static void saveResource(JavaPlugin plugin, String... supportLanguage) throws IOException, URISyntaxException {
        YamlConfiguration pluginYml = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource("plugin.yml")));
        String language = pluginYml.getString("language", systemLanguage);
        if (supportLanguage.length != 0 && !Arrays.asList(supportLanguage).contains(language)) {
            language = supportLanguage[0];
        }
        plugin.getLogger().info("Localization: " + language);
        extractResourceFolder(plugin, language);
    }

    private static void extractResourceFolder(JavaPlugin plugin, String language) throws IOException, URISyntaxException {
        File targetFolder = plugin.getDataFolder();
        URL sourceUri = plugin.getClass().getResource("");
        try (FileSystem fileSystem = FileSystems.newFileSystem(sourceUri.toURI(), Collections.emptyMap())) {
            Path sourcePath = fileSystem.getPath("/Localization/" + language);
            for (Path source : Files.walk(sourcePath).toArray(Path[]::new)) {
                Path targetPath = targetFolder.toPath().resolve(sourcePath.relativize(source).toString());
                if (Files.exists(targetPath)) continue;
                if (Files.isDirectory(source)) {
                    Files.createDirectory(targetPath);
                } else {
                    plugin.getLogger().info("Copy: " + targetPath);
                    Files.copy(source, targetPath);
                }
            }
        }
    }
}
