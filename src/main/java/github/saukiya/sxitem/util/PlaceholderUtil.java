package github.saukiya.sxitem.util;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.RandomDocker;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author 格洛
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlaceholderUtil {

    static boolean enabled;

    public static void setup() {
        if (enabled = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            try {
                new Placeholder().register();
            } catch (Exception e) {
                SXItem.getInst().getLogger().warning("Placeholder error");
                enabled = false;
            }
        }
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

    static class Placeholder extends PlaceholderExpansion {

        @Override
        public String getIdentifier() {
            return "sxitem";
        }

        @Override
        public String getAuthor() {
            return SXItem.getInst().getDescription().getAuthors().toString();
        }

        @Override
        public String getVersion() {
            return SXItem.getInst().getDescription().getVersion();
        }

        @Override
        public String onPlaceholderRequest(Player player, String str) {
            return RandomDocker.getINST().replace(str);
        }
    }
}
