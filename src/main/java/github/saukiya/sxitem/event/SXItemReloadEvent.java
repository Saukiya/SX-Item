package github.saukiya.sxitem.event;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 插件重载事件
 *
 * @author Saukiya
 */
public class SXItemReloadEvent extends Event {

    @Getter
    private static final SXItemReloadEvent inst = new SXItemReloadEvent();

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
