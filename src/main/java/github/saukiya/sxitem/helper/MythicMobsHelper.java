package github.saukiya.sxitem.helper;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.item.IGenerator;
import github.saukiya.sxitem.event.SXItemMythicMobsGiveToInventoryEvent;
import github.saukiya.sxitem.util.Config;
import github.saukiya.sxitem.util.NMS;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MythicMobsHelper {

    @Getter
    private static Listener handler;

    /**
     * SX-Drop:
     * - 物品ID 数量 概率
     * - 物品ID 数量 概率 局部变量
     * - 物品ID 数量 概率 Key1:Value1 Key2:Value2
     *
     * @param event io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent
     */
    @Setter
    private static DeathHandler deathHandler = (mobType, mobLocation, mobMap, killer, drops, sxDropList) -> {
        for (String str : sxDropList) {
            String[] args = str.split(" ");
            IGenerator ig = SXItem.getItemManager().getGenerator(args[0]);
            // 概率
            if (ig == null || args.length > 2 && args[2].length() > 0 && SXItem.getRandom().nextDouble() > Double.parseDouble(args[2]))
                continue;
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
            Map<String, String> otherMap = getOtherMap(args, 3);
            otherMap.putAll(mobMap);
            //BUGFIX 被伤害来源非玩家击杀(宠物等)不会正常掉落物品
            Player player = null;
            Inventory inventory = null;
            if (killer instanceof Player) {
                player = (Player) killer;
                inventory = player.getInventory();
            }
            for (int i = 0; i < amount; i++) {
                ItemStack itemStack = SXItem.getItemManager().getItem(ig, player, otherMap);
                if (player != null  && Config.getConfig().getBoolean(Config.MOB_DROP_TO_PLAYER_INVENTORY)) {
                    SXItemMythicMobsGiveToInventoryEvent eventI = new SXItemMythicMobsGiveToInventoryEvent(ig, player, mobType, itemStack);
                    Bukkit.getPluginManager().callEvent(eventI);
                    if (!eventI.isCancelled()) {
                        if (inventory.firstEmpty() != -1) {
                            inventory.addItem(eventI.getItemStack());
                        } else {
                            Item item = player.getWorld().dropItem(mobLocation, eventI.getItemStack());
                            item.setMetadata("SX-Item|DropData", new FixedMetadataValue(SXItem.getInst(), player.getName()));
                            item.setPickupDelay(40);
                        }
                    }
                } else {
                    if (player == null && !Config.getConfig().getBoolean(Config.MOB_DROP_IF_NON_PLAYER_KILLED)) {
                        continue;
                    }
                    drops.add(itemStack);
                }
            }
        }
    };

    /**
     * SX-Equipment:
     * - 物品ID:位置 概率
     * - 物品ID:位置 概率 局部变量
     * - 物品ID:位置 概率 Key1:Value1 Key2:Value2
     *
     * @param event io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent
     */
    @Setter
    private static SpawnHandler spawnHandler = (mobType, mobEquipment, mobMap, sxEquipmentList) -> {
        for (String str : sxEquipmentList) {
            String[] args = str.split(" ");
            String[] sap = args[0].split(":");

            IGenerator ig = SXItem.getItemManager().getGenerator(sap[0]);
            if (ig == null || args.length > 1 && SXItem.getRandom().nextDouble() > Double.parseDouble(args[1]))
                continue;
            Map<String, String> otherMap = getOtherMap(args, 2);
            otherMap.putAll(mobMap);
            ItemStack itemStack = SXItem.getItemManager().getItem(ig, null, otherMap);
            switch (sap[1]) {
                case "-1":
                case "OFFHAND":
                    mobEquipment.setItemInOffHand(itemStack);
                    break;
                case "0":
                case "HAND":
                    mobEquipment.setItemInHand(itemStack);
                    break;
                case "1":
                case "FEET":
                    mobEquipment.setBoots(itemStack);
                    break;
                case "2":
                case "LEGS":
                    mobEquipment.setLeggings(itemStack);
                    break;
                case "3":
                case "CHEST":
                    mobEquipment.setChestplate(itemStack);
                    break;
                case "4":
                case "HEAD":
                    mobEquipment.setHelmet(itemStack);
                    break;
                default:
                    SXItem.getInst().getLogger().severe("MythicMobs - Equipment Error: " + mobType + " - " + str);
            }
        }
    };

    public static void setup() {
        if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
            if (NMS.hasClass("io.lumine.xikage.mythicmobs.mobs.MythicMob")) {
                Bukkit.getPluginManager().registerEvents(handler = new V4Listener(), SXItem.getInst());
                SXItem.getInst().getLogger().info("MythicMobsV4Helper Enabled");
            } else if (NMS.hasClass("io.lumine.mythic.api.mobs.MythicMob")) {
                Bukkit.getPluginManager().registerEvents(handler = new V5Listener(), SXItem.getInst());
                SXItem.getInst().getLogger().info("MythicMobsV5Helper Enabled");
            }
        } else {
            SXItem.getInst().getLogger().info("MythicMobsHelper Disable");
        }
    }

    public static Map<String, String> getOtherMap(String[] args, int index) {
        Map<String, String> otherMap = new HashMap<>();
        for (int i = index; i < args.length; i++) {
            String[] splits = args[i].split(":");
            otherMap.put(splits[0], splits[1]);
        }
        return otherMap;
    }

    public interface DeathHandler {
        void death(String mobType, Location mobLocation, Map<String, String> mobMap, LivingEntity killer, List<ItemStack> drops, List<String> sxDropList);
    }

    public interface SpawnHandler {
        void spawn(String mobType, EntityEquipment mobEquipment, Map<String, String> mobMap, List<String> sxEquipmentList);
    }

    public static class V4Listener implements Listener {

        @EventHandler
        void on(io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent event) {
            if (event.getEntity() instanceof LivingEntity) {
                String mobType = event.getMobType().getInternalName();
                EntityEquipment mobEquipment = ((LivingEntity) event.getEntity()).getEquipment();
                Map<String, String> mobMap = getMobMap(event.getMob());
                List<String> sxEquipmentList = event.getMobType().getConfig().getStringList("SX-Equipment");
                spawnHandler.spawn(mobType, mobEquipment, mobMap, sxEquipmentList);
            }
        }

        @EventHandler
        void on(io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent event) {
            String mobType = event.getMobType().getInternalName();
            Location mobLocation = event.getEntity().getLocation();
            Map<String, String> mobMap = getMobMap(event.getMob());
            LivingEntity killer = event.getKiller();
            List<ItemStack> drops = event.getDrops();
            List<String> sxDropList = event.getMobType().getConfig().getStringList("SX-Drop");
            sxDropList.addAll(event.getMobType().getConfig().getStringList("SX-Drops"));
            deathHandler.death(mobType, mobLocation, mobMap, killer, drops, sxDropList);
            event.setDrops(drops);
        }

        /**
         * 依据 io.lumine.xikage.mythicmobs.mobs.ActiveMob 提供变量
         *
         * @param mob
         */
        public static Map<String, String> getMobMap(io.lumine.xikage.mythicmobs.mobs.ActiveMob mob) {
            Map<String, String> map = new HashMap<>();
            map.put("mob_level", Double.toString(mob.getLevel()));
            map.put("mob_name_display", mob.getDisplayName());
            map.put("mob_name_internal", mob.getType().getInternalName());
            map.put("mob_uuid", mob.getUniqueId().toString());
            return map;
        }
    }

    public static class V5Listener implements Listener {

        @EventHandler
        void on(io.lumine.mythic.bukkit.events.MythicMobSpawnEvent event) {
            if (event.getEntity() instanceof LivingEntity) {
                String mobType = event.getMobType().getInternalName();
                EntityEquipment mobEquipment = ((LivingEntity) event.getEntity()).getEquipment();
                Map<String, String> mobMap = getMobMap(event.getMob());
                List<String> sxEquipmentList = event.getMobType().getConfig().getStringList("SX-Equipment");
                spawnHandler.spawn(mobType, mobEquipment, mobMap, sxEquipmentList);
            }
        }

        @EventHandler
        void on(io.lumine.mythic.bukkit.events.MythicMobDeathEvent event) {
            String mobType = event.getMobType().getInternalName();
            Location mobLocation = event.getEntity().getLocation();
            Map<String, String> mobMap = getMobMap(event.getMob());
            LivingEntity killer = event.getKiller();
            List<ItemStack> drops = event.getDrops();
            List<String> sxDropList = event.getMobType().getConfig().getStringList("SX-Drop");
            sxDropList.addAll(event.getMobType().getConfig().getStringList("SX-Drops"));
            deathHandler.death(mobType, mobLocation, mobMap, killer, drops, sxDropList);
            event.setDrops(drops);
        }

        /**
         * 依据 io.lumine.mythic.core.mobs.ActiveMob 提供变量
         *
         * @param mob
         */
        public static Map<String, String> getMobMap(io.lumine.mythic.core.mobs.ActiveMob mob) {
            Map<String, String> map = new HashMap<>();
            map.put("mob_level", Double.toString(mob.getLevel()));
            map.put("mob_name_display", mob.getDisplayName());
            map.put("mob_name_internal", mob.getType().getInternalName());
            map.put("mob_uuid", mob.getUniqueId().toString());
            return map;
        }
    }
}
