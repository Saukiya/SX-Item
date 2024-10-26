package github.saukiya.sxitem.util;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * @see github.saukiya.tools.util.LogUtil
 * @deprecated
 */
public class LogUtil extends github.saukiya.tools.util.LogUtil {

    @Deprecated
    public LogUtil(JavaPlugin plugin) {
        setup(plugin);
    }

    public void onDisable() {
    }
}
