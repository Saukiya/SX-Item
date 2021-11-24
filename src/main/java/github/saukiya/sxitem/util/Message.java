package github.saukiya.sxitem.util;

import github.saukiya.sxitem.SXItem;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * NMS化
 *
 * @author Saukiya
 */

public enum Message {
    ELEVATOR__UP,
    ELEVATOR__DOWN,

    ADMIN__NO_ITEM,
    ADMIN__GIVE_ITEM,
    ADMIN__HAS_ITEM,
    ADMIN__SAVE_ITEM,
    ADMIN__SAVE_NO_TYPE,
    ADMIN__SAVE_ITEM_ERROR,
    ADMIN__GIVE_KIT,
    ADMIN__NO_PERMISSION_CMD,
    ADMIN__NO_CMD,
    ADMIN__NO_FORMAT,
    ADMIN__NO_ONLINE,
    ADMIN__NO_CONSOLE,
    ADMIN__PLUGIN_RELOAD,

    COMMAND__GIVE,
    COMMAND__SAVE,
    COMMAND__NBT,
    COMMAND__KIT,
    COMMAND__CHAT,
    COMMAND__CMD,
    COMMAND__BLOCK,
    COMMAND__CHEST,
    COMMAND__ENTITY,
    COMMAND__ATTACK,
    COMMAND__PLUGINS,
    COMMAND__RELOAD;

    private static final Map<Material, String> itemRam = new HashMap<>();
    @Getter
    private static YamlConfiguration messages;

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

    /**
     * 获取String
     *
     * @param loc  Message
     * @param args Object...
     * @return String
     */
    public static String getMsg(Message loc, Object... args) {
        return ChatColor.translateAlternateColorCodes('&', MessageFormat.format(messages.getString(loc.toString(), "Null Message: " + loc), args));
    }

    /**
     * 获取List
     *
     * @param loc  Message
     * @param args Object...
     * @return List
     */
    public static List<String> getStringList(Message loc, Object... args) {
        List<String> list = messages.getStringList(loc.toString());
        if (list.size() == 0) return Collections.singletonList("Null Message: " + loc);
        IntStream.range(0, list.size()).forEach(i -> list.set(i, MessageFormat.format(list.get(i), args).replace("&", "§")));
        return list;
    }

    @Override
    public String toString() {
        return name().replace("__", ".");
    }
}