package github.saukiya.sxitem.util;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @see github.saukiya.util.common.LocalizationUtil
 * @deprecated
 */
public class LocalizationUtil {

    @Deprecated
    public static void saveResource(JavaPlugin plugin, String... supportLanguage) throws IOException, URISyntaxException {
        github.saukiya.util.common.LocalizationUtil.saveResource(plugin, supportLanguage);
    }
}
