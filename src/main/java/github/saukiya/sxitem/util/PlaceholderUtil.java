package github.saukiya.sxitem.util;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.RandomDocker;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @Author 格洛
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlaceholderUtil extends PlaceholderExpansion {

    private final RandomDocker random = new RandomDocker();

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
        return random.lookup(str);
    }

    public static void setup() {
        new PlaceholderUtil().register();
    }

    public static List<String> setPlaceholders(Player player, List<String> list) {
        if (list == null) return null;

        return PlaceholderAPI.setPlaceholders(player, list);
    }

    public static String setPlaceholders(Player player, String text) {
        if (text == null) return null;

        return PlaceholderAPI.setPlaceholders(player, text);
    }
}
