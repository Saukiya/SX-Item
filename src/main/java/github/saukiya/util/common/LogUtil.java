package github.saukiya.util.common;

import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogUtil {

    private final JavaPlugin plugin;

    private final FileHandler fileHandler;

    @SneakyThrows
    public LogUtil(JavaPlugin plugin) {
        this.plugin = plugin;
        File logsFile = new File(plugin.getDataFolder(), "logs");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(new Date());
        int index = 1;
        if (logsFile.exists()) {
            index += Arrays.stream(logsFile.listFiles()).filter(file -> file.getName().startsWith(dateStr)).count();
        } else {
            logsFile.mkdirs();
        }
        fileHandler = new FileHandler(logsFile.getAbsolutePath() + File.separator + dateStr + "-" + index + ".log");
        fileHandler.setEncoding("UTF-8");
        fileHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return MessageFormat.format("[{0}] {1}\n", record.getLevel(), record.getMessage());
            }
        });
        plugin.getLogger().addHandler(fileHandler);
    }

    public void onDisable() {
        plugin.getLogger().removeHandler(fileHandler);
        fileHandler.close();
    }
}