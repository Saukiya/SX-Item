package github.saukiya.tools.util;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * 记录日志工具
 * <pre>{@code
 *  @Override
 *  public void onEnable() {
 *      LogUtil.setup(this);
 *      //...
 *  }
 * }</pre>
 */
public class LogUtil {

    private static int timeOffset = ZonedDateTime.now().getOffset().getTotalSeconds();

    /**
     * 创建日志工具类并自动记录日志 (需要在{@link JavaPlugin#onEnable()}后调用)
     *
     * @param plugin JavaPlugin
     */
    @SneakyThrows
    public static void setup(JavaPlugin plugin) {
        if (!plugin.isEnabled()) {
            plugin.getLogger().info("Plugin not enabled");
            return;
        }
        File logsFile = new File(plugin.getDataFolder(), "logs");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(new Date());
        int index = 1;
        if (logsFile.exists()) {
            index += Arrays.stream(logsFile.listFiles()).filter(file -> file.getName().startsWith(dateStr)).count();
        } else {
            logsFile.mkdirs();
        }
        val fileHandler = new FileHandler(logsFile.getAbsolutePath() + File.separator + dateStr + "-" + index + ".log");
        val pluginNameIndex = plugin.getName().length() + 3;
        fileHandler.setEncoding("UTF-8");
        fileHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                val time = record.getMillis() / 1000 + timeOffset;
                val hours = time % 86400 / 3600;
                val minutes = time % 3600 / 60;
                val seconds = time % 60;
                return String.format("[%s] [%02d:%02d:%02d] %s\n",
                        record.getLevel(),
                        hours, minutes, seconds,
                        record.getMessage().substring(Math.min(pluginNameIndex, record.getMessage().length())));
            }
        });
        plugin.getLogger().addHandler(fileHandler);
        Bukkit.getPluginManager().registerEvents(new EventListener(plugin, fileHandler), plugin);
    }

    /**
     * 不想写if导致的
     */
    public static void setup(JavaPlugin plugin, boolean enabled) {
        if (!enabled) return;
        setup(plugin);
    }

    @AllArgsConstructor
    static class EventListener implements Listener {

        private final JavaPlugin plugin;
        private final FileHandler fileHandler;

        @EventHandler
        public void on(PluginDisableEvent event) {
            if (event.getPlugin() != plugin) return;
            plugin.getLogger().removeHandler(fileHandler);
            fileHandler.close();
        }
    }
}