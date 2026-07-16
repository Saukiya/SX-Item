package github.saukiya.sxitem.helper;

import github.saukiya.tools.base.EmptyMap;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.IItemDrop;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.bukkit.events.MythicDropLoadEvent;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent;
import io.lumine.mythic.core.drops.Drop;
import io.lumine.mythic.core.mobs.ActiveMob;

/** MythicMobs 5 的独立适配层，使用 MM5 的 api/core 包而不污染 MM4 代码。 */
public class MythicMobsV5Helper implements Listener {

    @EventHandler
    void on(MythicMobSpawnEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) return;
        EntityEquipment equipment = ((LivingEntity) event.getEntity()).getEquipment();
        MythicMobsHelper.handleSpawn(event.getMobType().getInternalName(), equipment, getMobMap(event.getMob()),
                event.getMobType().getConfig().getStringList("SX-Equipment"));
    }

    @EventHandler
    void on(MythicDropLoadEvent event) {
        if (MythicMobsHelper.DROP_NAME.equalsIgnoreCase(event.getDropName())) {
            event.register(new NativeDrop(event.getConfig()));
        }
    }

    @EventHandler
    void on(MythicMobDeathEvent event) {
        if (!(event.getKiller() instanceof Player)) return;
        List<String> drops = event.getMobType().getConfig().getStringList("SX-Drop");
        drops.addAll(event.getMobType().getConfig().getStringList("SX-Drops"));
        List<ItemStack> items = event.getDrops();
        MythicMobsHelper.handleDeath(event.getMobType().getInternalName(), event.getEntity().getLocation(),
                getMobMap(event.getMob()), (Player) event.getKiller(), items, drops);
        event.setDrops(items);
    }

    static Map<String, String> getMobMap(ActiveMob mob) {
        Map<String, String> map = new HashMap<>();
        map.put("mob_level", Double.toString(mob.getLevel()));
        map.put("mob_name_display", mob.getDisplayName());
        map.put("mob_name_internal", mob.getType().getInternalName());
        map.put("mob_uuid", mob.getUniqueId().toString());
        return map;
    }

    /** MM5 在 getDrop 中传入 MythicMobs 计算后的数量倍率。 */
    private static class NativeDrop extends Drop implements IItemDrop {
        private final MythicLineConfig config;

        NativeDrop(MythicLineConfig config) {
            super(config.getLine(), config);
            this.config = config;
        }

        @Override
        public AbstractItemStack getDrop(DropMetadata metadata, double amount) {
            Player player = metadata.getCause().map(cause -> cause.getBukkitEntity())
                    .filter(entity -> entity instanceof Player).map(entity -> (Player) entity).orElse(null);
            Map<String, String> mobMap = metadata.getDropper().filter(mob -> mob instanceof ActiveMob)
                    .map(mob -> getMobMap((ActiveMob) mob)).orElse(EmptyMap.emptyMap());
            Map<String, String> parameters = new HashMap<>();
            String itemId = config.getString("item", config.getString("id", ""));
            itemId = config.getPlaceholderString("item", itemId).get(metadata);
            for (Map.Entry<String, String> entry : config.entrySet()) {
                if (!isReserved(entry.getKey())) {
                    parameters.put(entry.getKey(), config.getPlaceholderString(entry.getKey(), entry.getValue()).get(metadata));
                }
            }
            ItemStack item = MythicMobsHelper.createNativeDrop(itemId,
                    MythicMobsHelper.mergeDropParameters(parameters, mobMap), player,
                    Math.max(1, (int) amount));
            return new BukkitItemStack(item);
        }

        private boolean isReserved(String key) {
            return key.equalsIgnoreCase("item") || key.equalsIgnoreCase("id")
                    || key.equalsIgnoreCase("amount") || key.equalsIgnoreCase("a");
        }
    }
}
