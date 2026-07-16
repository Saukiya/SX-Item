package github.saukiya.sxitem.helper;

import github.saukiya.tools.base.EmptyMap;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.lumine.xikage.mythicmobs.adapters.AbstractItemStack;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitItemStack;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicDropLoadEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent;
import io.lumine.xikage.mythicmobs.drops.Drop;
import io.lumine.xikage.mythicmobs.drops.DropMetadata;
import io.lumine.xikage.mythicmobs.drops.IItemDrop;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;

/** MythicMobs 4 的独立适配层，避免 MM4 API 与 MM5 API 在同一监听器中交叉加载。 */
public class MythicMobsV4Helper implements Listener {

    private boolean isVersionGreaterThan490;

    public MythicMobsV4Helper() {
        try {
            MythicMobSpawnEvent.class.getMethod("getMob");
            isVersionGreaterThan490 = true;
        } catch (NoSuchMethodException ignored) {
            isVersionGreaterThan490 = false;
        }
    }

    @EventHandler
    void on(MythicMobSpawnEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) return;
        EntityEquipment equipment = ((LivingEntity) event.getEntity()).getEquipment();
        Map<String, String> mobMap = isVersionGreaterThan490 ? getMobMap(event.getMob()) : EmptyMap.emptyMap();
        MythicMobsHelper.handleSpawn(event.getMobType().getInternalName(), equipment, mobMap,
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
                isVersionGreaterThan490 ? getMobMap(event.getMob()) : EmptyMap.emptyMap(),
                (Player) event.getKiller(), items, drops);
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

    /** MM4 在 getDrop 中使用已经由 MythicMobs 计算完成的 Drop 数量。 */
    private static class NativeDrop extends Drop implements IItemDrop {
        private final MythicLineConfig config;

        NativeDrop(MythicLineConfig config) {
            super(config.getLine(), config);
            this.config = config;
        }

        @Override
        public AbstractItemStack getDrop(DropMetadata metadata) {
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
                    Math.max(1, (int) getAmount()));
            return new BukkitItemStack(item);
        }

        private boolean isReserved(String key) {
            return key.equalsIgnoreCase("item") || key.equalsIgnoreCase("id")
                    || key.equalsIgnoreCase("amount") || key.equalsIgnoreCase("a");
        }
    }
}
