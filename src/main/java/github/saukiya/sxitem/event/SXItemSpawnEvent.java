package github.saukiya.sxitem.event;

import github.saukiya.sxitem.data.item.IGenerator;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 物品生成事件
 */
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SXItemSpawnEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final JavaPlugin plugin;

    private final Player player;

    private final IGenerator ig;

    private ItemStack item;

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
