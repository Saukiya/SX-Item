package github.saukiya.sxitem.helper;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.item.IGenerator;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class MythicMobsHelper {

    /**
     * SX-Equipment:
     * - 物品ID:位置 概率
     */
    @Setter
    private static Consumer<MythicMobSpawnEvent> spawnConsumer = event -> {
        if (event.getEntity() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) event.getEntity();
            EntityEquipment eq = entity.getEquipment();
            MythicMob mm = event.getMobType();
            for (String str : mm.getConfig().getStringList("SX-Equipment")) {
                String[] args = str.split(":");
                IGenerator ig = SXItem.getItemManager().getGenerator(args[0]);
                String[] sap = args[1].split(" ");
                if (ig == null || sap.length > 1 && SXItem.getRandom().nextDouble() > Double.parseDouble(sap[1]))
                    continue;

                ItemStack item = ig.getItem(null);
                switch (Integer.parseInt(sap[0])) {
                    case -1:
                        eq.setItemInOffHand(item);
                        break;
                    case 0:
                        eq.setItemInHand(item);
                        break;
                    case 1:
                        eq.setBoots(item);
                        break;
                    case 2:
                        eq.setLeggings(item);
                        break;
                    case 3:
                        eq.setChestplate(item);
                        break;
                    case 4:
                        eq.setHelmet(item);
                        break;
                    default:
                        SXItem.getInst().getLogger().severe("MythicMobs - Equipment Error: " + mm.getDisplayName() + " - " + str);
                }
            }
        }
    };

    /**
     * SX-Drop:
     * - 物品ID 数量 概率
     */
    @Setter
    private static Consumer<MythicMobDeathEvent> deathConsumer = event -> {
        if (event.getKiller() instanceof Player) {
            MythicMob mm = event.getMobType();
            List<ItemStack> drops = event.getDrops();
            for (String str : mm.getConfig().getStringList("SX-Drop")) {
                String[] args = str.split(" ");
                IGenerator ig = SXItem.getItemManager().getGenerator(args[0]);
                // 概率
                if (ig == null || args.length > 2 && args[2].length() > 0 && SXItem.getRandom().nextDouble() > Double.parseDouble(args[2])) {
                    continue;
                }
                // 数量
                int amount = 1;
                if (args.length > 1 && args[1].length() > 0) {
                    if (args[1].contains("-")) {
                        int[] ints = Arrays.stream(args[1].split("-")).mapToInt(Integer::parseInt).sorted().toArray();
                        amount = SXItem.getRandom().nextInt(1 + ints[1] - ints[0]) + ints[0];
                    } else {
                        amount = Integer.parseInt(args[1]);
                    }
                }
                // 给予
                for (int i = 0; i < amount; i++) {
                    drops.add(ig.getItem((Player) event.getKiller()));
                }
            }
            event.setDrops(drops);
        }
    };

    public static void setup() {
        if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
            Bukkit.getPluginManager().registerEvents(new Listener() {

                @EventHandler
                void on(MythicMobSpawnEvent event) {
                    spawnConsumer.accept(event);
                }

                @EventHandler
                void on(MythicMobDeathEvent event) {
                    deathConsumer.accept(event);
                }
            }, SXItem.getInst());
        }
    }
}
