package github.saukiya.sxitem.util;

import github.saukiya.sxitem.SXItem;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

/**
 * @Author 格洛
 */

public class Placeholders extends PlaceholderExpansion {


    public static void setup() {
        new Placeholders().register();
    }

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
        if (str.startsWith("random")) {
            //TODO 添加随机字符的占位符
        }
        return "Error: %si_" + str + "%";
    }
}
