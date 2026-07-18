package github.saukiya.sxitem.listener;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.util.Config;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.lang.reflect.Method;

/**
 * 阻止 SXItem 进入会改变物品状态的原版工作台，并阻止其作为方块放置。
 *
 * <p>限制基于持久化物品键而不是显示内容，避免误伤同名或同 Lore 的普通物品。</p>
 */
public class ItemRestrictionListener implements Listener {

    /**
     * 拦截鼠标放入、Shift 快速移动及快捷栏/副手交换等单击操作。
     *
     * @param event 原版背包单击事件
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory topInventory = getTopInventory(event);
        if (!isRestricted(topInventory.getType())) return;

        int rawSlot = event.getRawSlot();
        if (rawSlot >= 0 && rawSlot < topInventory.getSize()) {
            if (isSXItem(event.getCursor()) || isSXItem(getHotbarSwapItem(event))) {
                event.setCancelled(true);
            }
            return;
        }

        // Shift 单击由服务端自动寻找目标槽位，必须在物品离开玩家背包前整体取消。
        if (event.isShiftClick() && event.getClickedInventory() != null && isSXItem(event.getCurrentItem())) {
            event.setCancelled(true);
        }
    }

    /**
     * 拖拽事件可一次覆盖多个槽位，因此只要目标包含受限工作台槽位就取消整次操作。
     *
     * @param event 原版背包拖拽事件
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory topInventory = getTopInventory(event);
        if (!isRestricted(topInventory.getType()) || !isSXItem(event.getOldCursor())) return;
        if (event.getRawSlots().stream().anyMatch(slot -> slot < topInventory.getSize())) {
            event.setCancelled(true);
        }
    }

    /**
     * 阻止具有方块材质的 SXItem 被放入世界，避免放置后永久丢失自定义物品数据。
     *
     * @param event 原版方块放置事件
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (Config.getConfig().getBoolean(Config.RESTRICTION_BLOCK_PLACE, true)
                && isSXItem(event.getItemInHand())) {
            event.setCancelled(true);
        }
    }

    private boolean isRestricted(InventoryType type) {
        if (type == InventoryType.WORKBENCH) {
            return Config.getConfig().getBoolean(Config.RESTRICTION_CRAFTING_TABLE, true);
        }
        if (type == InventoryType.ENCHANTING) {
            return Config.getConfig().getBoolean(Config.RESTRICTION_ENCHANTING_TABLE, true);
        }
        return false;
    }

    /**
     * 获取交互界面的顶部背包，同时避开 {@code InventoryView} 在不同 Bukkit 版本间的类/接口 ABI 变化。
     *
     * <p>{@link InventoryInteractEvent#getInventory()} 始终返回该事件的主背包，也就是原先从视图取得的
     * 顶部背包；直接走事件 API 可让同一份字节码同时运行在旧版类定义和新版接口定义上。</p>
     */
    private Inventory getTopInventory(InventoryInteractEvent event) {
        return event.getInventory();
    }

    private boolean isSXItem(ItemStack item) {
        return SXItem.getItemManager().isSXItem(item);
    }

    private ItemStack getHotbarSwapItem(InventoryClickEvent event) {
        HumanEntity player = event.getWhoClicked();
        if (!(player.getInventory() instanceof PlayerInventory)) return null;
        PlayerInventory inventory = (PlayerInventory) player.getInventory();
        if (event.getHotbarButton() >= 0) {
            return inventory.getItem(event.getHotbarButton());
        }
        if (!"SWAP_OFFHAND".equals(event.getClick().name())) return null;
        try {
            // 反射隔离 1.9 才新增的副手 API，保证插件仍可在无副手概念的旧版服务端加载。
            Method method = inventory.getClass().getMethod("getItemInOffHand");
            return (ItemStack) method.invoke(inventory);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }
}
