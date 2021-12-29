package github.saukiya.sxitem.event;

import github.saukiya.sxitem.data.item.IGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * 物品生成事件
 *
 * @author Saukiya
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
public class SXItemSpawnEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

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
