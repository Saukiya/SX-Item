package github.saukiya.sxitem.helper;

import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.RandomDocker;
import github.saukiya.sxitem.util.NMS;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author 格洛
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemsAdderHelper implements Listener {

    public static boolean enabled = false;

    public static void setup() {
        if (Bukkit.getPluginManager().isPluginEnabled("ItemsAdder")) {
            enabled = true;
        }
    }

    public static void setItem(ItemStack item, ItemMeta meta, String form) {
        if (ItemsAdderHelper.enabled) {
            ItemStack itemsAdder = ItemsAdderHelper.getItem(form);
            if (itemsAdder != null) {
                ItemMeta temp = itemsAdder.getItemMeta();
                if (temp != null) {
                    meta.setCustomModelData(temp.getCustomModelData());
                    item.setType(itemsAdder.getType());
                }
            }
        }
    }

    public static ItemStack getItem(String id) {
        CustomStack stack = CustomStack.getInstance(id);
        return stack.getItemStack();
    }

}
