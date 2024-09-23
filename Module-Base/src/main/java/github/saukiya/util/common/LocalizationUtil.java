package github.saukiya.util.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * 本地化工具
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocalizationUtil {

    private static final String systemLanguage = Locale.getDefault().getLanguage();

    /**
     * <pre>
     * 保存本地化资源到插件文件夹中
     * 会将插件内的"Localization/{language}/"中的资源保存到本地
     * 如果当前语言不支持，则会选择Localization中的首个语言作为替代
     * 如果文件已存在，则不会进行覆盖。
     * </pre>
     *
     * @param plugin          当前插件
     */
    public static void saveResource(JavaPlugin plugin) throws IOException, URISyntaxException {
        saveResource(plugin, "en");
    }

    /**
     * <pre>
     * 保存本地化资源到插件文件夹中
     * 会将插件内的"Localization/{language}/"中的资源保存到本地
     * 如果当前语言不支持，则会选择Localization中的首个语言作为替代
     * 如果文件已存在，则不会进行覆盖。
     * </pre>
     *
     * @param plugin      当前插件
     * @param defLanguage 默认语言
     */
    public static void saveResource(JavaPlugin plugin, String defLanguage) throws IOException, URISyntaxException {
        try (InputStreamReader reader = new InputStreamReader(plugin.getResource("plugin.yml"))) {
            YamlConfiguration pluginYml = YamlConfiguration.loadConfiguration(reader);
            String curLanguage = pluginYml.getString("language", systemLanguage);
            reader.close();
            extractResourceFolder(plugin, curLanguage, defLanguage);
        }
    }

    private static void extractResourceFolder(JavaPlugin plugin, String curLanguage, String defaultLanguage) throws IOException, URISyntaxException {
        File targetFolder = plugin.getDataFolder();
        URL sourceUrl = plugin.getClass().getResource("");
        try (val fileSystem = FileSystems.newFileSystem(sourceUrl.toURI(), Collections.emptyMap())) {
            Path localizationPath = fileSystem.getPath("/Localization/");
            // 判断首选语言是否存在
            Path languagePath = localizationPath.resolve(curLanguage);
            if (!Files.exists(languagePath)) {
                // 判断默认语言是否存在
                languagePath = localizationPath.resolve(defaultLanguage);
            }
            // 两个都不在就随便给一个
            if (!Files.exists(languagePath)) {
                try (val localizationStream = Files.list(localizationPath)) {
                    languagePath = localizationStream.findFirst().orElse(null);
                }
            }
            if (languagePath == null) {
                plugin.getLogger().warning("Localization: No Language");
                return;
            }
            plugin.getLogger().info("Localization: " + languagePath.getFileName());
            try (Stream<Path> sourcePaths = Files.walk(languagePath)) {
                for (Path source : sourcePaths.toArray(Path[]::new)) {
                    Path targetPath = targetFolder.toPath().resolve(languagePath.relativize(source).toString());
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
}
