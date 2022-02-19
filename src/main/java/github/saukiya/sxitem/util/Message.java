package github.saukiya.sxitem.util;

import github.saukiya.sxitem.SXItem;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

/**
 * NMS化
 *
 * @author Saukiya
 */

public enum Message {

    ADMIN__NO_ITEM,
    ADMIN__GIVE_ITEM,
    ADMIN__HAS_ITEM,
    ADMIN__SAVE_ITEM,
    ADMIN__SAVE_NO_TYPE,
    ADMIN__SAVE_ITEM_ERROR,
    ADMIN__NO_PERMISSION_CMD,
    ADMIN__NO_CMD,
    ADMIN__NO_FORMAT,
    ADMIN__NO_ONLINE,
    ADMIN__PLUGIN_RELOAD,

    INFO__CLICK_COPY,

    COMMAND__GIVE,
    COMMAND__SAVE,
    COMMAND__NBT,
    COMMAND__RELOAD;

    @Getter
    private static YamlConfiguration messages;

    @Override
    public String toString() {
        return name().replace("__", ".");
    }

    /**
     * 获取String
     *
     * @param args Object...
     * @return String
     */
    public String get(Object... args) {
        return MessageFormat.format(messages.getString(toString(), "Null Message: " + this), args).replace("&", "§");
    }

    /**
     * 获取List
     *
     * @param args Object...
     * @return List
     */
    public List<String> getList(Object... args) {
        List<String> list = messages.getStringList(toString());
        if (list.size() == 0) return Collections.singletonList("Null Message: " + this);
        list.replaceAll(str -> MessageFormat.format(str, args).replace("&", "§"));
        return list;
    }

    /**
     * 加载Message类
     */
    public static void loadMessage() {
        File file = new File(SXItem.getInst().getDataFolder(), "Message.yml");
        if (!file.exists()) {
            SXItem.getInst().getLogger().info("create Message.yml");
            SXItem.getInst().saveResource("Message.yml", true);
        }
        messages = YamlConfiguration.loadConfiguration(file);
    }
}