package github.saukiya.sxitem.event;

import github.saukiya.sxitem.data.item.IGenerator;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 开启mm掉落进入玩家背包后给予时触发
 *
 * @author Ray_Hughes
 */
@Getter
@Setter
public class SXItemUpdateEvent extends SXItemSpawnEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final ItemStack oldItem;

    private boolean cancelled;

    public SXItemUpdateEvent(JavaPlugin plugin, Player player, IGenerator ig, ItemStack item, ItemStack oldItem) {
        super(plugin, player, ig, item);
        this.oldItem = oldItem;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
