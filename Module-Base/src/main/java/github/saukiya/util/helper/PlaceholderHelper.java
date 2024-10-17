package github.saukiya.util.helper;

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

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlaceholderHelper {

    static boolean enabled;

    public static void setup(JavaPlugin plugin) {
        setup(plugin, null, null);
    }

    public static void setup(JavaPlugin plugin, PlaceholderRequest placeholderRequest) {
        setup(plugin, plugin.getName().toLowerCase(Locale.ROOT).replace("-", ""), placeholderRequest);
    }

    public static void setup(JavaPlugin plugin, String identifier, PlaceholderRequest placeholderRequest) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            enabled = true;
            plugin.getLogger().info("PlaceholderHelper Enabled");
        } else {
            enabled = false;
            plugin.getLogger().info("PlaceholderHelper Disable");
            return;
        }

        if (identifier == null || placeholderRequest == null) return;
        Placeholder.register(plugin, identifier, placeholderRequest);
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

    public interface PlaceholderRequest {
        String onPlaceholderRequest(Player player, String params);
    }

    @AllArgsConstructor
    static class Placeholder extends PlaceholderExpansion {

        final JavaPlugin plugin;
        final String identifier;
        final PlaceholderRequest handle;

        public static void register(JavaPlugin plugin, String identifier, PlaceholderRequest placeholderRequest) {
            new Placeholder(plugin, identifier, placeholderRequest).register();
        }

        @Nonnull
        @Override
        public String getIdentifier() {
            return identifier;
        }

        @Nonnull
        @Override
        public String getAuthor() {
            return plugin.getDescription().getAuthors().toString();
        }

        @Nonnull
        @Override
        public String getVersion() {
            return plugin.getDescription().getVersion();
        }

        @Override
        public String onPlaceholderRequest(Player player, @Nonnull String params) {
            return handle.onPlaceholderRequest(player, params);
        }
    }
}
