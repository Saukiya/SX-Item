package github.saukiya.sxitem.helper;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.item.IGenerator;
import github.saukiya.sxitem.data.item.impl.GeneratorReMaterial;
import github.saukiya.sxitem.event.SXItemMythicMobsGiveToInventoryEvent;
import github.saukiya.sxitem.util.Config;
import github.saukiya.sxitem.util.Util;
import github.saukiya.tools.nms.NMS;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MythicMobsHelper {

    /**
     * MythicMobs 原生战利品类型名称。使用独立类型可以让 MM 负责概率、数量和战利品表流程，
     * 同时保留旧版 SX-Drop 配置的兼容性。
     */
    static final String DROP_NAME = "sxitem";

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
    private static DeathHandler deathHandler = (mobType, mobLocation, mobMap, player, drops, sxDropList) -> {
        for (String str : sxDropList) {
            String[] args = str.split(" ");
            IGenerator ig = SXItem.getItemManager().getGenerator(args[0]);
            // 概率
            if (ig == null || args.length > 2 && !args[2].isEmpty() && SXItem.getRandom().nextDouble() > Double.parseDouble(args[2]))
                continue;
            // 数量
            int amount = 1;
            if (args.length > 1 && !args[1].isEmpty()) {
                int index = args[1].indexOf('-');
                if (index != -1) {
                    int min = Integer.parseInt(args[1].substring(0, index));
                    int max = Integer.parseInt(args[1].substring(index + 1));
                    amount = Util.random(min, max);
                } else {
                    amount = Integer.parseInt(args[1]);
                }
            }
            // 给予
            Inventory inventory = player.getInventory();
            Object param;
            if (ig instanceof GeneratorReMaterial) {
                param = args[0];
            } else {
                val otherMap = getOtherMap(args, 3);
                otherMap.putAll(mobMap);
                param = otherMap;
            }
            for (int i = 0; i < amount; i++) {
                ItemStack itemStack = SXItem.getItemManager().getItem(ig, player, param);
                if (itemStack.getType() == Material.AIR) continue;
                if (Config.getConfig().getBoolean(Config.MOB_DROP_TO_PLAYER_INVENTORY)) {
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

            Object param;
            if (ig instanceof GeneratorReMaterial) {
                param = sap[0];
            } else {
                val otherMap = getOtherMap(args, 2);
                otherMap.putAll(mobMap);
                param = otherMap;
            }
            ItemStack itemStack = SXItem.getItemManager().getItem(ig, null, param);
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
        if (!Config.getConfig().getBoolean(Config.COMPATIBILITY_MYTHIC_MOBS, true)) return;
        if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
            if (NMS.hasClass("io.lumine.xikage.mythicmobs.mobs.MythicMob")) {
                Bukkit.getPluginManager().registerEvents(handler = new MythicMobsV4Helper(), SXItem.getInst());
                SXItem.getInst().getLogger().info("MythicMobsV4Helper Enabled");
            } else if (NMS.hasClass("io.lumine.mythic.api.mobs.MythicMob")) {
                Bukkit.getPluginManager().registerEvents(handler = new MythicMobsV5Helper(), SXItem.getInst());
                SXItem.getInst().getLogger().info("MythicMobsV5Helper Enabled");
            }
        } else {
            SXItem.getInst().getLogger().info("MythicMobsHelper Disable");
        }
    }

    public static Map<String, String> getOtherMap(String[] args, int index) {
        Map<String, String> otherMap = new HashMap<>();
        for (int i = index; i < args.length; i++) {
            String[] splits = args[i].split(":", 2);
            otherMap.put(splits[0], splits[1]);
        }
        return otherMap;
    }

    /**
     * 从原生掉落配置生成 SX 物品。参数值先经过 MythicMobs 当前掉落上下文解析，
     * 因而 `{owner=<trigger.name>}` 等占位符不会在加载配置时被错误地固定。
     */
    static ItemStack createNativeDrop(String itemId, Map<String, String> parameters,
                                              Player player, int amount) {
        IGenerator generator = SXItem.getItemManager().getGenerator(itemId);
        if (generator == null) return new ItemStack(Material.AIR);
        Object param = generator instanceof GeneratorReMaterial ? itemId : parameters;
        ItemStack item = SXItem.getItemManager().getItem(generator, player, param);
        if (item.getType() != Material.AIR) item.setAmount(Math.max(1, amount));
        return item;
    }

    static Map<String, String> mergeDropParameters(Map<String, String> parameters,
                                                            Map<String, String> mobMap) {
        Map<String, String> result = new HashMap<>(parameters);
        result.putAll(mobMap);
        return result;
    }

    static void handleDeath(String mobType, Location mobLocation, Map<String, String> mobMap,
                            Player player, List<ItemStack> drops, List<String> sxDropList) {
        deathHandler.death(mobType, mobLocation, mobMap, player, drops, sxDropList);
    }

    static void handleSpawn(String mobType, EntityEquipment mobEquipment,
                            Map<String, String> mobMap, List<String> sxEquipmentList) {
        spawnHandler.spawn(mobType, mobEquipment, mobMap, sxEquipmentList);
    }

    public interface DeathHandler {
        void death(String mobType, Location mobLocation, Map<String, String> mobMap, Player player, List<ItemStack> drops, List<String> sxDropList);
    }

    public interface SpawnHandler {
        void spawn(String mobType, EntityEquipment mobEquipment, Map<String, String> mobMap, List<String> sxEquipmentList);
    }

}
