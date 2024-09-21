package github.saukiya.sxitem.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

import java.util.List;

@Deprecated
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlaceholderHelper {

    @Deprecated
    public static List<String> setPlaceholders(Player player, List<String> list) {
        return github.saukiya.util.helper.PlaceholderHelper.setPlaceholders(player, list);
    }

    @Deprecated
    public static String setPlaceholders(Player player, String text) {
        return github.saukiya.util.helper.PlaceholderHelper.setPlaceholders(player, text);
    }
}
