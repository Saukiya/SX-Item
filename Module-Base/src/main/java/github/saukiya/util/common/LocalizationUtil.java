package github.saukiya.util.common;

import lombok.val;
import lombok.var;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * 本地化工具
 */
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
        try (InputStreamReader reader = new InputStreamReader(plugin.getResource("plugin.yml"))) {
            YamlConfiguration pluginYml = YamlConfiguration.loadConfiguration(reader);
            String language = pluginYml.getString("language", systemLanguage);
            reader.close();
            extractResourceFolder(plugin, language);
        }
    }

    private static void extractResourceFolder(JavaPlugin plugin, String language) throws IOException, URISyntaxException {
        File targetFolder = plugin.getDataFolder();
        URL sourceUrl = plugin.getClass().getResource("");
        try (FileSystem fileSystem = FileSystems.newFileSystem(sourceUrl.toURI(), Collections.emptyMap())) {
            Path fileSystemPath = fileSystem.getPath("/Localization/");
            plugin.getLogger().warning("fileSystemPath: " + fileSystemPath);
            try (val languagePaths = Files.list(fileSystemPath)) {
                val sourcePath = languagePaths.filter(path -> path.getFileName().toString().equals(language)).findFirst().orElseGet(() -> {
                    try (val elsePath = Files.list(fileSystemPath)) {
                        return elsePath.findFirst().get();
                    } catch (IOException e) {
                        return null;
                    }
                });
                if (sourcePath ==  null) {
                    plugin.getLogger().warning("Localization: No Language");
                    return;
                }
                plugin.getLogger().info("Localization: " + sourcePath.getFileName());
                try (Stream<Path> sourcePaths = Files.walk(sourcePath)) {
                    for (Path source : sourcePaths.toArray(Path[]::new)) {
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
    }
}
