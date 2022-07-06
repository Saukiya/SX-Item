package github.saukiya.sxitem.helper;

import github.saukiya.sxitem.SXItem;
import org.bukkit.Bukkit;

public class ItemsAdderHelper {

    static boolean enabled;

    public static void setup() {
        enabled = Bukkit.getPluginManager().getPlugin("ItemsAdder") != null;
    }

    public static Boolean isEnable(){
        return enabled;
    }

}
