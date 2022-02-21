package github.saukiya.sxitem.event;

import github.saukiya.sxitem.data.item.IGenerator;
import github.saukiya.sxitem.helper.MythicMobsHelper;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * 物品生成事件
 *
 * @author Saukiya
 */
@Getter
@Setter
public class SXItemGiveToInventoryEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player player;

    private ActiveMob mob;

    public IGenerator item;

    public boolean cancelled = false;

    public SXItemGiveToInventoryEvent(Player player, ActiveMob mob, IGenerator itemGenerator) {
        this.player = player;
        this.mob = mob;
        this.item = itemGenerator;
    }

    public ItemStack getItemStack() {
        return MythicMobsHelper.getItem(item, player, mob);
    }


    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
