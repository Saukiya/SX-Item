package github.saukiya.tools.helper;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;

/**
 * Placeholder辅助类
 * <pre>{@code
 *  PlaceholderHelper.setup(plugin, (player, params) -> params); // %plugin_params%
 *  PlaceholderHelper.setup(plugin, identifier, (player, params) -> params); // %identifier_params%
 *
 *  String v1 = PlaceholderHelper.setPlaceholders(player, "%player_name%");
 *  List<String> v2 = PlaceholderHelper.setPlaceholders(player, Arrays.asList("%player_name%", "%player_exp%"));
 * }</pre>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlaceholderHelper {

    static boolean enabled;

    public static void setup(JavaPlugin plugin) {
        setup(plugin, null, null);
    }

    /**
     * 初始化并创建Placeholder占位符, 并根据插件名称生成identifier(标识符)
     * <br>
     * identifier 默认为插件名称小写, 并省略'-'
     *
     * @param plugin 插件本体
     * @param hook   占位符钩子
     */
    public static void setup(JavaPlugin plugin, BiFunction<Player, String, String> hook) {
        String identifier = plugin.getName().toLowerCase(Locale.ROOT).replace("-", "");
        setup(plugin, identifier, hook);
    }

    /**
     * 初始化并创建Placeholder占位符
     *
     * @param plugin     插件本体
     * @param identifier 标识符(不可重复)
     * @param hook       占位符钩子
     */
    public static void setup(JavaPlugin plugin, String identifier, BiFunction<Player, String, String> hook) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            enabled = true;
            plugin.getLogger().info("PlaceholderHelper Enabled");
        } else {
            enabled = false;
            plugin.getLogger().info("PlaceholderHelper Disable");
            return;
        }

        if (identifier == null || hook == null) return;
        Placeholder.register(plugin, identifier, hook);
    }

    public static List<String> setPlaceholders(Player player, List<String> list) {
        if (list == null) return null;
        if (!enabled || player == null) return new ArrayList<>(list);

        return PlaceholderAPI.setPlaceholders(player, list);
    }

    public static String setPlaceholders(Player player, String text) {
        if (text == null) return null;
        if (!enabled || player == null) return text;

        return PlaceholderAPI.setPlaceholders(player, text);
    }

    @AllArgsConstructor
    static class Placeholder extends PlaceholderExpansion {

        final JavaPlugin plugin;
        final String identifier;
        final BiFunction<Player, String, String> handle;

        public static void register(JavaPlugin plugin, String identifier, BiFunction<Player, String, String> hook) {
            new Placeholder(plugin, identifier, hook).register();
        }

        @Override
        public String getIdentifier() {
            return identifier;
        }

        @Override
        public String getAuthor() {
            return plugin.getDescription().getAuthors().toString();
        }

        @Override
        public String getVersion() {
            return plugin.getDescription().getVersion();
        }

        @Override
        public String onPlaceholderRequest(Player player, @Nonnull String params) {
            return handle.apply(player, params);
        }
    }
}
