package github.saukiya.sxitem.util;

import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogUtil {

    JavaPlugin plugin;

    File file;

    FileHandler fileHandler;

    @SneakyThrows
    public LogUtil(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "logs");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(new Date());
        int index = 1;
        if (file.exists()) {
            for (File listFile : file.listFiles()) {
                if (listFile.getName().startsWith(dateStr)) {
                    index++;
                }
            }
        } else {
            file.mkdirs();
        }
        fileHandler = new FileHandler(file.getAbsolutePath() + File.separator + dateStr + "-" + index + ".log");
        fileHandler.setEncoding("UTF-8");
        fileHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return MessageFormat.format("[{0}] {1}\n", record.getLevel(), record.getMessage());
            }
        });
        plugin.getLogger().addHandler(fileHandler);
    }

    public void destroy() {
        plugin.getLogger().removeHandler(fileHandler);
        fileHandler.close();
    }
}