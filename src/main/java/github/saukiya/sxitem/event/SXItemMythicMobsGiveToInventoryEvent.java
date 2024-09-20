package github.saukiya.sxitem.event;

import github.saukiya.sxitem.data.item.IGenerator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * 物品放入背包事件
 *
 * @author Ray_Hughes
 */
@Getter
@Setter
@RequiredArgsConstructor
public class SXItemMythicMobsGiveToInventoryEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final IGenerator item;

    private final Player player;

    private final String mobType;

    @Getter
    private final ItemStack itemStack;

    private boolean cancelled;

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
