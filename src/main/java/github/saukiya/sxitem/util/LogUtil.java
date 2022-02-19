package github.saukiya.sxitem.util;

import github.saukiya.sxitem.SXItem;
import lombok.SneakyThrows;

import java.io.File;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogUtil {

    static File file = new File(SXItem.getInst().getDataFolder(), "logs");

    static FileHandler fileHandler;

    @SneakyThrows
    public static void setup() {
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
        fileHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return MessageFormat.format("[{0}] {1}\n", record.getLevel(), record.getMessage());
            }
        });
        SXItem.getInst().getLogger().addHandler(fileHandler);
    }

    public static void close() {
        SXItem.getInst().getLogger().removeHandler(fileHandler);
        fileHandler.close();
    }
}