package github.saukiya.sxitem.data.random.randoms.script;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.helper.PlaceholderHelper;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JavaScriptUtils {

    public static <T> List<T> mutableList(T... args) {
        return Arrays.stream(args).collect(Collectors.toList());
    }

    public static String fromPlaceholderAPI(Player player, String str) {
        return PlaceholderHelper.setPlaceholders(player, str);
    }

    public static String asColor(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static int randomInt(Integer first, Integer last) {
        return SXItem.getRandom().nextInt(1 + last - first) + first;
    }

    public static double randomDouble(double min, double max) {
        double range = (max - min) * Math.random();
        return min + range;
    }



}
