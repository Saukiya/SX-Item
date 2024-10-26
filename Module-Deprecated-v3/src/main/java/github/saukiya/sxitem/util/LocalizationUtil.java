package github.saukiya.sxitem.util;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @see github.saukiya.tools.util.LocalizationUtil
 * @deprecated
 */
public class LocalizationUtil {

    @Deprecated
    public static void saveResource(JavaPlugin plugin) throws IOException, URISyntaxException {
        github.saukiya.tools.util.LocalizationUtil.saveResource(plugin);
    }
}
