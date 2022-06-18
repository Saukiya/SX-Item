package github.saukiya.sxitem.helper;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.item.IGenerator;
import github.saukiya.sxitem.data.item.sub.GeneratorDefault;
import github.saukiya.sxitem.event.SXItemMythicMobsGiveToInventoryEvent;
import github.saukiya.sxitem.util.Config;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicReloadedEvent;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MythicMobsHelper {

    public static final Map<MythicMob, Map<String, String>> mobPlaceholders = new HashMap<>();

    @Setter
    private static Consumer<MythicMobSpawnEvent> spawnConsumer = MythicMobsHelper::spawnFunc;

    @Setter
    private static Consumer<MythicMobDeathEvent> deathConsumer = MythicMobsHelper::deathFunc;

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

                @EventHandler
                void on(MythicReloadedEvent event) {
                    mobPlaceholders.clear();
                }
            }, SXItem.getInst());
        }
    }

    /**
     * SX-Drop:
     * - 物品ID 数量 概率
     * - 物品ID 数量 概率 局部变量
     * - 物品ID 数量 概率 Key1:Value1 Key2:Value2
     *
     * @param event MythicMobDeathEvent
     */
    private static void deathFunc(MythicMobDeathEvent event) {
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
                Map<String, String> otherMap = getOtherMap(args, 3);
                // 给予
                if (Config.getConfig().getBoolean(Config.MOB_DROP_TO_PLAYER_INVENTORY)) {
                    Inventory inventory = ((Player) event.getKiller()).getInventory();
                    for (int i = 0; i < amount; i++) {
                        SXItemMythicMobsGiveToInventoryEvent eventI = new SXItemMythicMobsGiveToInventoryEvent(ig, (Player) event.getKiller(), event.getMob(), getItem(ig, (Player) event.getKiller(), event.getMob(), otherMap));
                        Bukkit.getPluginManager().callEvent(eventI);
                        if (!eventI.isCancelled()) {
                            inventory.addItem(eventI.getItemStack());
                        }
                    }
                } else {
                    for (int i = 0; i < amount; i++) {
                        drops.add(getItem(ig, (Player) event.getKiller(), event.getMob(), otherMap));
                    }
                }
            }
            event.setDrops(drops);
        }
    }

    /**
     * SX-Equipment:
     * - 物品ID:位置 概率
     * - 物品ID:位置 概率 局部变量
     * - 物品ID:位置 概率 Key1:Value1 Key2:Value2
     *
     * @param event MythicMobSpawnEvent
     */
    private static void spawnFunc(MythicMobSpawnEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) event.getEntity();
            EntityEquipment eq = entity.getEquipment();
            MythicMob mm = event.getMobType();
            for (String str : mm.getConfig().getStringList("SX-Equipment")) {
                String[] args = str.split(" ");
                String[] sap = args[0].split(":");

                IGenerator ig = SXItem.getItemManager().getGenerator(sap[0]);
                if (ig == null || args.length > 1 && SXItem.getRandom().nextDouble() > Double.parseDouble(args[1]))
                    continue;

                ItemStack item = getItem(ig, null, event.getMob(), getOtherMap(args, 2));
                switch (Integer.parseInt(sap[1])) {
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
    }

    /**
     * 依据 ActiveMob 提供变量 生成 Item
     *
     * @param ig
     * @param player
     * @param mob
     * @return
     */
    public static ItemStack getItem(IGenerator ig, @Nullable Player player, ActiveMob mob) {
        return getItem(ig, player, mob, new HashMap<>());
    }

    public static ItemStack getItem(IGenerator ig, @Nullable Player player, ActiveMob mob, Map<String, String> otherMap) {
        if (ig instanceof GeneratorDefault) {
            otherMap.put("mob_level", Double.toString(mob.getLevel()));
            otherMap.put("mob_name_display", mob.getDisplayName());
            otherMap.put("mob_name_internal", mob.getType().getInternalName());
            otherMap.put("mob_uuid", mob.getUniqueId().toString());
            return SXItem.getItemManager().getItem(ig, player, otherMap);
        } else {
            return SXItem.getItemManager().getItem(ig, player);
        }
    }

    public static Map<String, String> getOtherMap(String[] args) {
        return getOtherMap(args, 0);
    }

    public static Map<String, String> getOtherMap(String[] args, int index) {
        Map<String, String> otherMap = new HashMap<>();
        for (int i = index; i < args.length; i++) {
            String[] splits = args[i].split(":");
            otherMap.put(splits[0], splits[1]);
        }
        return otherMap;
    }
}
