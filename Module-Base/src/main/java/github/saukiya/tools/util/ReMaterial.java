package github.saukiya.tools.util;

import github.saukiya.tools.nms.NMS;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 材质修复 - (兼容旧版数字ID/运行版本英文ID)
 * <pre>{@code
 *  Material m1 = ReMaterial.getMaterial("160:3"); // 获取材质
 *  ItemStack i1 = ReMaterial.getItem("160:3"); // 获取物品★
 *  ReMaterial.LIGHT_BLUE_STAINED_GLASS_PANE.material(); // 之间获取材质
 *  ReMaterial.LIGHT_BLUE_STAINED_GLASS_PANE.item(); // 直接获取物品★
 * }</pre>
 */
public enum ReMaterial {
    /** 空气 **/
    AIR(null, "0"),
    /** 石头 **/
    STONE(null, "1", "1:0"),
    /** 花岗岩 **/
    GRANITE("STONE", "1:1"),
    /** 磨制花岗岩 **/
    POLISHED_GRANITE("STONE", "1:2"),
    /** 闪长岩 **/
    DIORITE("STONE", "1:3"),
    /** 磨制闪长岩 **/
    POLISHED_DIORITE("STONE", "1:4"),
    /** 安山岩 **/
    ANDESITE("STONE", "1:5"),
    /** 磨制安山岩 **/
    POLISHED_ANDESITE("STONE", "1:6"),
    /** 草方块 **/
    GRASS_BLOCK("GRASS", "2"),
    /** 泥土 **/
    DIRT(null, "3", "3:0"),
    /** 砂土 **/
    COARSE_DIRT("DIRT", "3:1"),
    /** 灰化土 **/
    PODZOL("DIRT", "3:2"),
    /** 圆石 **/
    COBBLESTONE(null, "4"),
    /** 橡木木板 **/
    OAK_PLANKS("WOOD", "5", "5:0"),
    /** 云杉木板 **/
    SPRUCE_PLANKS("WOOD", "5:1"),
    /** 白桦木板 **/
    BIRCH_PLANKS("WOOD", "5:2"),
    /** 丛林木板 **/
    JUNGLE_PLANKS("WOOD", "5:3"),
    /** 金合欢木板 **/
    ACACIA_PLANKS("WOOD", "5:4"),
    /** 深色橡木木板 **/
    DARK_OAK_PLANKS("WOOD", "5:5"),
    /** 橡树树苗 **/
    OAK_SAPLING("SAPLING", "6", "6:0"),
    /** 云杉树苗 **/
    SPRUCE_SAPLING("SAPLING", "6:1"),
    /** 白桦树苗 **/
    BIRCH_SAPLING("SAPLING", "6:2"),
    /** 丛林树苗 **/
    JUNGLE_SAPLING("SAPLING", "6:3"),
    /** 金合欢树苗 **/
    ACACIA_SAPLING("SAPLING", "6:4"),
    /** 深色橡树树苗 **/
    DARK_OAK_SAPLING("SAPLING", "6:5"),
    /** 基岩 **/
    BEDROCK(null, "7"),
    /** 沙子 **/
    SAND(null, "12", "12:0"),
    /** 红沙 **/
    RED_SAND("SAND", "12:1"),
    /** 沙砾 **/
    GRAVEL(null, "13"),
    /** 金矿石 **/
    GOLD_ORE(null, "14"),
    /** 铁矿石 **/
    IRON_ORE(null, "15"),
    /** 煤矿石 **/
    COAL_ORE(null, "16"),
    /** 橡木 **/
    OAK_LOG("LOG", "17", "17:0"),
    /** 云杉木 **/
    SPRUCE_LOG("LOG", "17:1"),
    /** 白桦木 **/
    BIRCH_LOG("LOG", "17:2"),
    /** 丛林木 **/
    JUNGLE_LOG("LOG", "17:3"),
    /** 橡树树叶 **/
    OAK_LEAVES("LEAVES", "18", "18:0"),
    /** 云杉树叶 **/
    SPRUCE_LEAVES("LEAVES", "18:1"),
    /** 白桦树叶 **/
    BIRCH_LEAVES("LEAVES", "18:2"),
    /** 丛林树叶 **/
    JUNGLE_LEAVES("LEAVES", "18:3"),
    /** 海绵 **/
    SPONGE(null, "19", "19:0"),
    /** 湿海绵 **/
    WET_SPONGE("SPONGE", "19:1"),
    /** 玻璃 **/
    GLASS(null, "20"),
    /** 青金石矿石 **/
    LAPIS_ORE(null, "21"),
    /** 青金石块 **/
    LAPIS_BLOCK(null, "22"),
    /** 发射器 **/
    DISPENSER(null, "23"),
    /** 砂岩 **/
    SANDSTONE(null, "24", "24:0"),
    /** 錾制砂岩 **/
    CHISELED_SANDSTONE("SANDSTONE", "24:1"),
    /** 平滑砂岩 **/
    CUT_SANDSTONE("SANDSTONE", "24:2"),
    /** 音符盒 **/
    NOTE_BLOCK(null, "25"),
    /** 充能铁轨 **/
    POWERED_RAIL(null, "27"),
    /** 探测铁轨 **/
    DETECTOR_RAIL(null, "28"),
    /** 粘性活塞 **/
    STICKY_PISTON("PISTON_STICKY_BASE", "29"),
    /** 蜘蛛网 **/
    COBWEB("WEB", "30"),
    /** 灌木 **/
    SHORT_GRASS("LONG_GRASS", "31", "31:0"),
    /** 草 **/
    FERN("LONG_GRASS", "31:1"),
    /** 枯死的灌木 **/
    DEAD_BUSH(null, "32"),
    /** 活塞 **/
    PISTON("PISTON_BASE", "33"),
    /** 白色羊毛 **/
    WHITE_WOOL("WOOL", "35", "35:0"),
    /** 橙色羊毛 **/
    ORANGE_WOOL("WOOL", "35:1"),
    /** 品红色羊毛 **/
    MAGENTA_WOOL("WOOL", "35:2"),
    /** 淡蓝色羊毛 **/
    LIGHT_BLUE_WOOL("WOOL", "35:3"),
    /** 黄色羊毛 **/
    YELLOW_WOOL("WOOL", "35:4"),
    /** 黄绿色羊毛 **/
    LIME_WOOL("WOOL", "35:5"),
    /** 粉红色羊毛 **/
    PINK_WOOL("WOOL", "35:6"),
    /** 灰色羊毛 **/
    GRAY_WOOL("WOOL", "35:7"),
    /** 淡灰色羊毛 **/
    LIGHT_GRAY_WOOL("WOOL", "35:8"),
    /** 青色羊毛 **/
    CYAN_WOOL("WOOL", "35:9"),
    /** 紫色羊毛 **/
    PURPLE_WOOL("WOOL", "35:10"),
    /** 蓝色羊毛 **/
    BLUE_WOOL("WOOL", "35:11"),
    /** 棕色羊毛 **/
    BROWN_WOOL("WOOL", "35:12"),
    /** 绿色羊毛 **/
    GREEN_WOOL("WOOL", "35:13"),
    /** 红色羊毛 **/
    RED_WOOL("WOOL", "35:14"),
    /** 黑色羊毛 **/
    BLACK_WOOL("WOOL", "35:15"),
    /** 蒲公英 **/
    DANDELION("YELLOW_FLOWER", "37"),
    /** 虞美人 **/
    POPPY("RED_ROSE", "38", "38:0"),
    /** 兰花 **/
    BLUE_ORCHID("RED_ROSE", "38:1"),
    /** 绒球葱 **/
    ALLIUM("RED_ROSE", "38:2"),
    /** 茜草花 **/
    AZURE_BLUET("RED_ROSE", "38:3"),
    /** 红色郁金香 **/
    RED_TULIP("RED_ROSE", "38:4"),
    /** 橙色郁金香 **/
    ORANGE_TULIP("RED_ROSE", "38:5"),
    /** 白色郁金香 **/
    WHITE_TULIP("RED_ROSE", "38:6"),
    /** 粉红色郁金香 **/
    PINK_TULIP("RED_ROSE", "38:7"),
    /** 滨菊 **/
    OXEYE_DAISY("RED_ROSE", "38:8"),
    /** 蘑菇 **/
    BROWN_MUSHROOM(null, "39"),
    /** 蘑菇 **/
    RED_MUSHROOM(null, "40"),
    /** 金块 **/
    GOLD_BLOCK(null, "41"),
    /** 铁块 **/
    IRON_BLOCK(null, "42"),
    /** 石台阶 **/
    SMOOTH_STONE_SLAB("STEP", "44", "44:0"),
    /** 砂岩台阶 **/
    SANDSTONE_SLAB("STEP", "44:1"),
    /** 木台阶 **/
    COBBLESTONE_SLAB("STEP", "44:2"),
    /** 圆石台阶 **/
    BRICK_SLAB("STEP", "44:3"),
    /** 砖台阶 **/
    STONE_BRICK_SLAB("STEP", "44:4"),
    /** 石砖台阶 **/
    NETHER_BRICK_SLAB("STEP", "44:5"),
    /** 地狱砖台阶 **/
    QUARTZ_SLAB("STEP", "44:6"),
    /** 砖块 **/
    BRICK(null, "45", "336"),
    /** TNT **/
    TNT(null, "46"),
    /** 书架 **/
    BOOKSHELF(null, "47"),
    /** 苔石 **/
    MOSSY_COBBLESTONE(null, "48"),
    /** 黑曜石 **/
    OBSIDIAN(null, "49"),
    /** 火把 **/
    TORCH(null, "50"),
    /** 刷怪箱 **/
    SPAWNER("MOB_SPAWNER", "52"),
    /** 橡木楼梯 **/
    OAK_STAIRS("WOOD_STAIRS", "53"),
    /** 箱子 **/
    CHEST(null, "54"),
    /** 钻石矿石 **/
    DIAMOND_ORE(null, "56"),
    /** 钻石块 **/
    DIAMOND_BLOCK(null, "57"),
    /** 工作台 **/
    CRAFTING_TABLE("WORKBENCH", "58"),
    /** 耕地 **/
    FARMLAND("SOIL", "60"),
    /** 熔炉 **/
    FURNACE(null, "61"),
    /** 梯子 **/
    LADDER(null, "65"),
    /** 铁轨 **/
    RAIL("RAILS", "66"),
    /** 圆石楼梯 **/
    COBBLESTONE_STAIRS(null, "67"),
    /** 拉杆 **/
    LEVER(null, "69"),
    /** 石质压力板 **/
    STONE_PRESSURE_PLATE("STONE_PLATE", "70"),
    /** 木质压力板 **/
    OAK_PRESSURE_PLATE("WOOD_PLATE", "72"),
    /** 红石矿石 **/
    REDSTONE_ORE(null, "73"),
    /** 红石火把 **/
    REDSTONE_WALL_TORCH("REDSTONE_TORCH_ON", "76"),
    /** 按钮 **/
    STONE_BUTTON(null, "77"),
    /** 雪 **/
    SNOW(null, "78"),
    /** 冰 **/
    ICE(null, "79"),
    /** 雪 **/
    SNOW_BLOCK(null, "80"),
    /** 仙人掌 **/
    CACTUS(null, "81"),
    /** 粘土块 **/
    CLAY(null, "82"),
    /** 唱片机 **/
    JUKEBOX(null, "84"),
    /** 橡木栅栏 **/
    OAK_FENCE("FENCE", "85"),
    /** 南瓜 **/
    PUMPKIN(null, "86"),
    /** 地狱岩 **/
    NETHERRACK(null, "87"),
    /** 灵魂沙 **/
    SOUL_SAND(null, "88"),
    /** 荧石 **/
    GLOWSTONE(null, "89"),
    /** 南瓜灯 **/
    JACK_O_LANTERN(null, "91"),
    /** 白色染色玻璃 **/
    WHITE_STAINED_GLASS("STAINED_GLASS", "95", "95:0"),
    /** 橙色染色玻璃 **/
    ORANGE_STAINED_GLASS("STAINED_GLASS", "95:1"),
    /** 品红色染色玻璃 **/
    MAGENTA_STAINED_GLASS("STAINED_GLASS", "95:2"),
    /** 淡蓝色染色玻璃 **/
    LIGHT_BLUE_STAINED_GLASS("STAINED_GLASS", "95:3"),
    /** 黄色染色玻璃 **/
    YELLOW_STAINED_GLASS("STAINED_GLASS", "95:4"),
    /** 黄绿色染色玻璃 **/
    LIME_STAINED_GLASS("STAINED_GLASS", "95:5"),
    /** 粉红色染色玻璃 **/
    PINK_STAINED_GLASS("STAINED_GLASS", "95:6"),
    /** 灰色染色玻璃 **/
    GRAY_STAINED_GLASS("STAINED_GLASS", "95:7"),
    /** 淡灰色染色玻璃 **/
    LIGHT_GRAY_STAINED_GLASS("STAINED_GLASS", "95:8"),
    /** 青色染色玻璃 **/
    CYAN_STAINED_GLASS("STAINED_GLASS", "95:9"),
    /** 紫色染色玻璃 **/
    PURPLE_STAINED_GLASS("STAINED_GLASS", "95:10"),
    /** 蓝色染色玻璃 **/
    BLUE_STAINED_GLASS("STAINED_GLASS", "95:11"),
    /** 棕色染色玻璃 **/
    BROWN_STAINED_GLASS("STAINED_GLASS", "95:12"),
    /** 绿色染色玻璃 **/
    GREEN_STAINED_GLASS("STAINED_GLASS", "95:13"),
    /** 红色染色玻璃 **/
    RED_STAINED_GLASS("STAINED_GLASS", "95:14"),
    /** 黑色染色玻璃 **/
    BLACK_STAINED_GLASS("STAINED_GLASS", "95:15"),
    /** 活板门 **/
    OAK_TRAPDOOR("TRAP_DOOR", "96"),
    /** 生成 **/
    INFESTED_STONE("MONSTER_EGG", "383", "383:0", "97"),
    /** 石砖 **/
    STONE_BRICKS("SMOOTH_BRICK", "98"),
    /** 蘑菇 **/
    BROWN_MUSHROOM_BLOCK("HUGE_MUSHROOM_1", "99"),
    /** 蘑菇 **/
    RED_MUSHROOM_BLOCK("HUGE_MUSHROOM_2", "100"),
    /** 铁栏杆 **/
    IRON_BARS("IRON_FENCE", "101"),
    /** 玻璃板 **/
    GLASS_PANE("THIN_GLASS", "102"),
    /** 西瓜 **/
    MELON(null, "360", "103"),
    /** 藤蔓 **/
    VINE(null, "106"),
    /** 橡木栅栏门 **/
    OAK_FENCE_GATE("FENCE_GATE", "107"),
    /** 砖楼梯 **/
    BRICK_STAIRS(null, "108"),
    /** 石砖楼梯 **/
    STONE_BRICK_STAIRS("SMOOTH_STAIRS", "109"),
    /** 菌丝 **/
    MYCELIUM("MYCEL", "110"),
    /** 睡莲 **/
    LILY_PAD("WATER_LILY", "111"),
    /** 地狱砖 **/
    NETHER_BRICK(null, "112", "405"),
    /** 地狱砖栅栏 **/
    NETHER_BRICK_FENCE("NETHER_FENCE", "113"),
    /** 地狱砖楼梯 **/
    NETHER_BRICK_STAIRS(null, "114"),
    /** 附魔台 **/
    ENCHANTING_TABLE("ENCHANTMENT_TABLE", "116"),
    /** 末地传送门 **/
    END_PORTAL_FRAME("ENDER_PORTAL_FRAME", "120"),
    /** 末地石 **/
    END_STONE("ENDER_STONE", "121"),
    /** 龙蛋 **/
    DRAGON_EGG(null, "122"),
    /** 红石灯 **/
    REDSTONE_LAMP("REDSTONE_LAMP_OFF", "123"),
    /** 橡木台阶 **/
    OAK_SLAB("WOOD_STEP", "126", "126:0"),
    /** 云杉木台阶 **/
    SPRUCE_SLAB("WOOD_STEP", "126:1"),
    /** 桦木台阶 **/
    BIRCH_SLAB("WOOD_STEP", "126:2"),
    /** 丛林木台阶 **/
    JUNGLE_SLAB("WOOD_STEP", "126:3"),
    /** 金合欢木台阶 **/
    ACACIA_SLAB("WOOD_STEP", "126:4"),
    /** 深色橡木台阶 **/
    DARK_OAK_SLAB("WOOD_STEP", "126:5"),
    /** 砂岩楼梯 **/
    SANDSTONE_STAIRS(null, "128"),
    /** 绿宝石矿石 **/
    EMERALD_ORE(null, "129"),
    /** 末影箱 **/
    ENDER_CHEST(null, "130"),
    /** 绊线钩 **/
    TRIPWIRE_HOOK(null, "131"),
    /** 绿宝石块 **/
    EMERALD_BLOCK(null, "133"),
    /** 云杉木楼梯 **/
    SPRUCE_STAIRS("SPRUCE_WOOD_STAIRS", "134"),
    /** 桦木楼梯 **/
    BIRCH_STAIRS("BIRCH_WOOD_STAIRS", "135"),
    /** 丛林木楼梯 **/
    JUNGLE_STAIRS("JUNGLE_WOOD_STAIRS", "136"),
    /** 命令方块 **/
    COMMAND_BLOCK("COMMAND", "137"),
    /** 信标 **/
    BEACON(null, "138"),
    /** 圆石墙 **/
    COBBLESTONE_WALL("COBBLE_WALL", "139", "139:0"),
    /** 苔石墙 **/
    MOSSY_COBBLESTONE_WALL("COBBLE_WALL", "139:1"),
    /** 按钮 **/
    OAK_BUTTON("WOOD_BUTTON", "143"),
    /** 铁砧 **/
    ANVIL(null, "145", "145:0"),
    /** 轻微损坏的铁砧 **/
    CHIPPED_ANVIL("ANVIL", "145:1"),
    /** 严重损坏的铁砧 **/
    DAMAGED_ANVIL("ANVIL", "145:2"),
    /** 陷阱箱 **/
    TRAPPED_CHEST(null, "146"),
    /** 测重压力板（轻质） **/
    LIGHT_WEIGHTED_PRESSURE_PLATE("GOLD_PLATE", "147"),
    /** 测重压力板（重质） **/
    HEAVY_WEIGHTED_PRESSURE_PLATE("IRON_PLATE", "148"),
    /** 阳光传感器 **/
    DAYLIGHT_DETECTOR(null, "151"),
    /** 红石块 **/
    REDSTONE_BLOCK(null, "152"),
    /** 下界石英矿石 **/
    NETHER_QUARTZ_ORE("QUARTZ_ORE", "153"),
    /** 漏斗 **/
    HOPPER(null, "154"),
    /** 石英块 **/
    QUARTZ_BLOCK(null, "155", "155:0"),
    /** 錾制石英块 **/
    CHISELED_QUARTZ_BLOCK("QUARTZ_BLOCK", "155:1"),
    /** 竖纹石英块 **/
    QUARTZ_PILLAR("QUARTZ_BLOCK", "155:2"),
    /** 石英楼梯 **/
    QUARTZ_STAIRS(null, "156"),
    /** 激活铁轨 **/
    ACTIVATOR_RAIL(null, "157"),
    /** 投掷器 **/
    DROPPER(null, "158"),
    /** 白色陶瓦 **/
    WHITE_TERRACOTTA("STAINED_CLAY", "159"),
    /** 白色染色玻璃板 **/
    WHITE_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "160", "160:0"),
    /** 橙色染色玻璃板 **/
    ORANGE_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "160:1"),
    /** 品红色染色玻璃板 **/
    MAGENTA_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "160:2"),
    /** 淡蓝色染色玻璃板 **/
    LIGHT_BLUE_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "160:3"),
    /** 黄色染色玻璃板 **/
    YELLOW_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "160:4"),
    /** 黄绿色染色玻璃板 **/
    LIME_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "160:5"),
    /** 粉红色染色玻璃板 **/
    PINK_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "160:6"),
    /** 灰色染色玻璃板 **/
    GRAY_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "160:7"),
    /** 淡灰色染色玻璃板 **/
    LIGHT_GRAY_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "160:8"),
    /** 青色染色玻璃板 **/
    CYAN_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "160:9"),
    /** 紫色染色玻璃板 **/
    PURPLE_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "160:10"),
    /** 蓝色染色玻璃板 **/
    BLUE_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "160:11"),
    /** 棕色染色玻璃板 **/
    BROWN_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "160:12"),
    /** 绿色染色玻璃板 **/
    GREEN_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "160:13"),
    /** 红色染色玻璃板 **/
    RED_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "160:14"),
    /** 黑色染色玻璃板 **/
    BLACK_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "160:15"),
    /** 金合欢树叶 **/
    ACACIA_LEAVES("LEAVES_2", "161", "161:0"),
    /** 深色橡树树叶 **/
    DARK_OAK_LEAVES("LEAVES_2", "161:1"),
    /** 金合欢木 **/
    ACACIA_LOG("LOG_2", "162", "162:0"),
    /** 深色橡木 **/
    DARK_OAK_LOG("LOG_2", "162:1"),
    /** 金合欢木楼梯 **/
    ACACIA_STAIRS(null, "163"),
    /** 深色橡木楼梯  **/
    DARK_OAK_STAIRS(null, "164"),
    /** 粘液块 **/
    SLIME_BLOCK(null, "165"),
    /** 屏障 **/
    BARRIER(null, "166"),
    /** 铁活板门 **/
    IRON_TRAPDOOR(null, "167"),
    /** 海晶石 **/
    PRISMARINE(null, "168", "168:0"),
    /** 海晶石砖 **/
    PRISMARINE_BRICKS("PRISMARINE", "168:1"),
    /** 暗海晶石 **/
    DARK_PRISMARINE("PRISMARINE", "168:2"),
    /** 海晶灯 **/
    SEA_LANTERN(null, "169"),
    /** 干草块 **/
    HAY_BLOCK(null, "170"),
    /** 白色地毯 **/
    WHITE_CARPET("CARPET", "171", "171:0"),
    /** 橙色地毯 **/
    ORANGE_CARPET("CARPET", "171:1"),
    /** 品红色地毯 **/
    MAGENTA_CARPET("CARPET", "171:2"),
    /** 淡蓝色地毯 **/
    LIGHT_BLUE_CARPET("CARPET", "171:3"),
    /** 黄色地毯 **/
    YELLOW_CARPET("CARPET", "171:4"),
    /** 黄绿色地毯 **/
    LIME_CARPET("CARPET", "171:5"),
    /** 粉红色地毯 **/
    PINK_CARPET("CARPET", "171:6"),
    /** 灰色地毯 **/
    GRAY_CARPET("CARPET", "171:7"),
    /** 淡灰色地毯 **/
    LIGHT_GRAY_CARPET("CARPET", "171:8"),
    /** 青色地毯 **/
    CYAN_CARPET("CARPET", "171:9"),
    /** 紫色地毯 **/
    PURPLE_CARPET("CARPET", "171:10"),
    /** 蓝色地毯 **/
    BLUE_CARPET("CARPET", "171:11"),
    /** 棕色地毯 **/
    BROWN_CARPET("CARPET", "171:12"),
    /** 绿色地毯 **/
    GREEN_CARPET("CARPET", "171:13"),
    /** 红色地毯 **/
    RED_CARPET("CARPET", "171:14"),
    /** 黑色地毯 **/
    BLACK_CARPET("CARPET", "171:15"),
    /** 陶瓦 **/
    TERRACOTTA("HARD_CLAY", "172"),
    /** 煤炭块 **/
    COAL_BLOCK(null, "173"),
    /** 浮冰 **/
    PACKED_ICE(null, "174"),
    /** 向日葵 **/
    SUNFLOWER("DOUBLE_PLANT", "175", "175:0"),
    /** 丁香 **/
    LILAC("DOUBLE_PLANT", "175:1"),
    /** 高草丛 **/
    TALL_GRASS("DOUBLE_PLANT", "175:2"),
    /** 大型蕨 **/
    LARGE_FERN("DOUBLE_PLANT", "175:3"),
    /** 玫瑰丛 **/
    ROSE_BUSH("DOUBLE_PLANT", "175:4"),
    /** 牡丹 **/
    PEONY("DOUBLE_PLANT", "175:5"),
    /** 红砂岩 **/
    RED_SANDSTONE(null, "179", "179:0"),
    /** 錾制红砂岩 **/
    CHISELED_RED_SANDSTONE("RED_SANDSTONE", "179:1"),
    /** 平滑红砂岩 **/
    CUT_RED_SANDSTONE("RED_SANDSTONE", "179:2"),
    /** 红砂岩楼梯 **/
    RED_SANDSTONE_STAIRS(null, "180"),
    /** 红砂岩台阶 **/
    RED_SANDSTONE_SLAB("STONE_SLAB2", "182"),
    /** 云杉木栅栏门 **/
    SPRUCE_FENCE_GATE(null, "183"),
    /** 白桦木栅栏门 **/
    BIRCH_FENCE_GATE(null, "184"),
    /** 丛林木栅栏门 **/
    JUNGLE_FENCE_GATE(null, "185"),
    /** 深色橡木栅栏门 **/
    DARK_OAK_FENCE_GATE(null, "186"),
    /** 金合欢栅栏门 **/
    ACACIA_FENCE_GATE(null, "187"),
    /** 云杉木栅栏 **/
    SPRUCE_FENCE(null, "188"),
    /** 白桦木栅栏 **/
    BIRCH_FENCE(null, "189"),
    /** 丛林木栅栏 **/
    JUNGLE_FENCE(null, "190"),
    /** 深色橡木栅栏 **/
    DARK_OAK_FENCE(null, "191"),
    /** 金合欢栅栏 **/
    ACACIA_FENCE(null, "192"),
    /** 末地烛 **/
    END_ROD(null, "198"),
    /** 紫颂植物 **/
    CHORUS_PLANT(null, "199"),
    /** 紫颂花 **/
    CHORUS_FLOWER(null, "200"),
    /** 紫珀块 **/
    PURPUR_BLOCK(null, "201"),
    /** 竖纹紫珀块 **/
    PURPUR_PILLAR(null, "202"),
    /** 紫珀楼梯 **/
    PURPUR_STAIRS(null, "203"),
    /** 紫珀台阶 **/
    PURPUR_SLAB(null, "205"),
    /** 末地石砖 **/
    END_STONE_BRICKS("END_BRICKS", "206"),
    /** 草径 **/
    DIRT_PATH("GRASS_PATH", "208"),
    /** 循环型命令方块 **/
    REPEATING_COMMAND_BLOCK("COMMAND_REPEATING", "210"),
    /** 连锁型命令方块 **/
    CHAIN_COMMAND_BLOCK("COMMAND_CHAIN", "211"),
    /** 岩浆块 **/
    MAGMA_BLOCK("MAGMA", "213"),
    /** 地狱疣块 **/
    NETHER_WART_BLOCK(null, "214"),
    /** 红色地狱砖 **/
    RED_NETHER_BRICKS("RED_NETHER_BRICK", "215"),
    /** 骨块 **/
    BONE_BLOCK(null, "216"),
    /** 结构空位 **/
    STRUCTURE_VOID(null, "217"),
    /** 侦测器 **/
    OBSERVER(null, "218"),
    /** 白色潜影盒 **/
    WHITE_SHULKER_BOX(null, "219"),
    /** 橙色潜影盒 **/
    ORANGE_SHULKER_BOX(null, "220"),
    /** 品红色潜影盒 **/
    MAGENTA_SHULKER_BOX(null, "221"),
    /** 淡蓝色潜影盒 **/
    LIGHT_BLUE_SHULKER_BOX(null, "222"),
    /** 黄色潜影盒 **/
    YELLOW_SHULKER_BOX(null, "223"),
    /** 黄绿色潜影盒 **/
    LIME_SHULKER_BOX(null, "224"),
    /** 粉红色潜影盒 **/
    PINK_SHULKER_BOX(null, "225"),
    /** 灰色潜影盒 **/
    GRAY_SHULKER_BOX(null, "226"),
    /** 淡灰色潜影盒 **/
    LIGHT_GRAY_SHULKER_BOX("SILVER_SHULKER_BOX", "227"),
    /** 青色潜影盒 **/
    CYAN_SHULKER_BOX(null, "228"),
    /** 紫色潜影盒 **/
    PURPLE_SHULKER_BOX(null, "229"),
    /** 蓝色潜影盒 **/
    BLUE_SHULKER_BOX(null, "230"),
    /** 棕色潜影盒 **/
    BROWN_SHULKER_BOX(null, "231"),
    /** 绿色潜影盒 **/
    GREEN_SHULKER_BOX(null, "232"),
    /** 红色潜影盒 **/
    RED_SHULKER_BOX(null, "233"),
    /** 黑色潜影盒 **/
    BLACK_SHULKER_BOX(null, "234"),
    /** 白色带釉陶瓦 **/
    WHITE_GLAZED_TERRACOTTA(null, "235"),
    /** 橙色带釉陶瓦 **/
    ORANGE_GLAZED_TERRACOTTA(null, "236"),
    /** 品红色带釉陶瓦 **/
    MAGENTA_GLAZED_TERRACOTTA(null, "237"),
    /** 淡蓝色带釉陶瓦 **/
    LIGHT_BLUE_GLAZED_TERRACOTTA(null, "238"),
    /** 黄色带釉陶瓦 **/
    YELLOW_GLAZED_TERRACOTTA(null, "239"),
    /** 黄绿色带釉陶瓦 **/
    LIME_GLAZED_TERRACOTTA(null, "240"),
    /** 粉红色带釉陶瓦 **/
    PINK_GLAZED_TERRACOTTA(null, "241"),
    /** 灰色带釉陶瓦 **/
    GRAY_GLAZED_TERRACOTTA(null, "242"),
    /** 淡灰色带釉陶瓦 **/
    LIGHT_GRAY_GLAZED_TERRACOTTA("SILVER_GLAZED_TERRACOTTA", "243"),
    /** 青色带釉陶瓦 **/
    CYAN_GLAZED_TERRACOTTA(null, "244"),
    /** 紫色带釉陶瓦 **/
    PURPLE_GLAZED_TERRACOTTA(null, "245"),
    /** 蓝色带釉陶瓦 **/
    BLUE_GLAZED_TERRACOTTA(null, "246"),
    /** 棕色带釉陶瓦 **/
    BROWN_GLAZED_TERRACOTTA(null, "247"),
    /** 绿色带釉陶瓦 **/
    GREEN_GLAZED_TERRACOTTA(null, "248"),
    /** 红色带釉陶瓦 **/
    RED_GLAZED_TERRACOTTA(null, "249"),
    /** 黑色带釉陶瓦 **/
    BLACK_GLAZED_TERRACOTTA(null, "250"),
    /** 白色混凝土 **/
    WHITE_CONCRETE("CONCRETE", "251", "251:0"),
    /** 橙色混凝土 **/
    ORANGE_CONCRETE("CONCRETE", "251:1"),
    /** 品红色混凝土 **/
    MAGENTA_CONCRETE("CONCRETE", "251:2"),
    /** 淡蓝色混凝土 **/
    LIGHT_BLUE_CONCRETE("CONCRETE", "251:3"),
    /** 黄色混凝土 **/
    YELLOW_CONCRETE("CONCRETE", "251:4"),
    /** 黄绿色混凝土 **/
    LIME_CONCRETE("CONCRETE", "251:5"),
    /** 粉红色混凝土 **/
    PINK_CONCRETE("CONCRETE", "251:6"),
    /** 灰色混凝土 **/
    GRAY_CONCRETE("CONCRETE", "251:7"),
    /** 淡灰色混凝土 **/
    LIGHT_GRAY_CONCRETE("CONCRETE", "251:8"),
    /** 青色混凝土 **/
    CYAN_CONCRETE("CONCRETE", "251:9"),
    /** 紫色混凝土 **/
    PURPLE_CONCRETE("CONCRETE", "251:10"),
    /** 蓝色混凝土 **/
    BLUE_CONCRETE("CONCRETE", "251:11"),
    /** 棕色混凝土 **/
    BROWN_CONCRETE("CONCRETE", "251:12"),
    /** 绿色混凝土 **/
    GREEN_CONCRETE("CONCRETE", "251:13"),
    /** 红色混凝土 **/
    RED_CONCRETE("CONCRETE", "251:14"),
    /** 黑色混凝土 **/
    BLACK_CONCRETE("CONCRETE", "251:15"),
    /** 白色混凝土粉末 **/
    WHITE_CONCRETE_POWDER("CONCRETE_POWDER", "252", "252:0"),
    /** 橙色混凝土粉末 **/
    ORANGE_CONCRETE_POWDER("CONCRETE_POWDER", "252:1"),
    /** 品红色混凝土粉末 **/
    MAGENTA_CONCRETE_POWDER("CONCRETE_POWDER", "252:2"),
    /** 淡蓝色混凝土粉末 **/
    LIGHT_BLUE_CONCRETE_POWDER("CONCRETE_POWDER", "252:3"),
    /** 黄色混凝土粉末 **/
    YELLOW_CONCRETE_POWDER("CONCRETE_POWDER", "252:4"),
    /** 黄绿色混凝土粉末 **/
    LIME_CONCRETE_POWDER("CONCRETE_POWDER", "252:5"),
    /** 粉红色混凝土粉末 **/
    PINK_CONCRETE_POWDER("CONCRETE_POWDER", "252:6"),
    /** 灰色混凝土粉末 **/
    GRAY_CONCRETE_POWDER("CONCRETE_POWDER", "252:7"),
    /** 淡灰色混凝土粉末 **/
    LIGHT_GRAY_CONCRETE_POWDER("CONCRETE_POWDER", "252:8"),
    /** 青色混凝土粉末 **/
    CYAN_CONCRETE_POWDER("CONCRETE_POWDER", "252:9"),
    /** 紫色混凝土粉末 **/
    PURPLE_CONCRETE_POWDER("CONCRETE_POWDER", "252:10"),
    /** 蓝色混凝土粉末 **/
    BLUE_CONCRETE_POWDER("CONCRETE_POWDER", "252:11"),
    /** 棕色混凝土粉末 **/
    BROWN_CONCRETE_POWDER("CONCRETE_POWDER", "252:12"),
    /** 绿色混凝土粉末 **/
    GREEN_CONCRETE_POWDER("CONCRETE_POWDER", "252:13"),
    /** 红色混凝土粉末 **/
    RED_CONCRETE_POWDER("CONCRETE_POWDER", "252:14"),
    /** 黑色混凝土粉末 **/
    BLACK_CONCRETE_POWDER("CONCRETE_POWDER", "252:15"),
    /** 结构方块 **/
    STRUCTURE_BLOCK(null, "255"),
    /** 铁锹 **/
    IRON_SHOVEL("IRON_SPADE", "256"),
    /** 铁镐 **/
    IRON_PICKAXE(null, "257"),
    /** 铁斧 **/
    IRON_AXE(null, "258"),
    /** 打火石 **/
    FLINT_AND_STEEL(null, "259"),
    /** 苹果 **/
    APPLE(null, "260"),
    /** 弓 **/
    BOW(null, "261"),
    /** 箭 **/
    ARROW(null, "262"),
    /** 煤炭 **/
    COAL(null, "263", "263:0"),
    /** 木炭 **/
    CHARCOAL("COAL", "263:1"),
    /** 钻石 **/
    DIAMOND(null, "264"),
    /** 铁锭 **/
    IRON_INGOT(null, "265"),
    /** 金锭 **/
    GOLD_INGOT(null, "266"),
    /** 铁剑 **/
    IRON_SWORD(null, "267"),
    /** 木剑 **/
    WOODEN_SWORD("WOOD_SWORD", "268"),
    /** 木锹 **/
    WOODEN_SHOVEL("WOOD_SPADE", "269"),
    /** 木镐 **/
    WOODEN_PICKAXE("WOOD_PICKAXE", "270"),
    /** 木斧 **/
    WOODEN_AXE("WOOD_AXE", "271"),
    /** 石剑 **/
    STONE_SWORD(null, "272"),
    /** 石锹 **/
    STONE_SHOVEL("STONE_SPADE", "273"),
    /** 石镐 **/
    STONE_PICKAXE(null, "274"),
    /** 石斧 **/
    STONE_AXE(null, "275"),
    /** 钻石剑 **/
    DIAMOND_SWORD(null, "276"),
    /** 钻石锹 **/
    DIAMOND_SHOVEL("DIAMOND_SPADE", "277"),
    /** 钻石镐 **/
    DIAMOND_PICKAXE(null, "278"),
    /** 钻石斧 **/
    DIAMOND_AXE(null, "279"),
    /** 木棍 **/
    STICK(null, "280"),
    /** 碗 **/
    BOWL(null, "281"),
    /** 蘑菇煲 **/
    MUSHROOM_STEW("MUSHROOM_SOUP", "282"),
    /** 金剑 **/
    GOLDEN_SWORD("GOLD_SWORD", "283"),
    /** 金锹 **/
    GOLDEN_SHOVEL("GOLD_SPADE", "284"),
    /** 金镐 **/
    GOLDEN_PICKAXE("GOLD_PICKAXE", "285"),
    /** 金斧 **/
    GOLDEN_AXE("GOLD_AXE", "286"),
    /** 线 **/
    STRING(null, "287"),
    /** 羽毛 **/
    FEATHER(null, "288"),
    /** 火药 **/
    GUNPOWDER("SULPHUR", "289"),
    /** 木锄 **/
    WOODEN_HOE("WOOD_HOE", "290"),
    /** 石锄 **/
    STONE_HOE(null, "291"),
    /** 铁锄 **/
    IRON_HOE(null, "292"),
    /** 钻石锄 **/
    DIAMOND_HOE(null, "293"),
    /** 金锄 **/
    GOLDEN_HOE("GOLD_HOE", "294"),
    /** 小麦种子 **/
    WHEAT_SEEDS("SEEDS", "295"),
    /** 小麦 **/
    WHEAT(null, "296"),
    /** 面包 **/
    BREAD(null, "297"),
    /** 皮革帽子 **/
    LEATHER_HELMET(null, "298"),
    /** 皮革外套 **/
    LEATHER_CHESTPLATE(null, "299"),
    /** 皮革裤子 **/
    LEATHER_LEGGINGS(null, "300"),
    /** 皮革靴子 **/
    LEATHER_BOOTS(null, "301"),
    /** 锁链头盔 **/
    CHAINMAIL_HELMET(null, "302"),
    /** 锁链胸甲 **/
    CHAINMAIL_CHESTPLATE(null, "303"),
    /** 锁链护腿 **/
    CHAINMAIL_LEGGINGS(null, "304"),
    /** 锁链靴子 **/
    CHAINMAIL_BOOTS(null, "305"),
    /** 铁头盔 **/
    IRON_HELMET(null, "306"),
    /** 铁胸甲 **/
    IRON_CHESTPLATE(null, "307"),
    /** 铁护腿 **/
    IRON_LEGGINGS(null, "308"),
    /** 铁靴子 **/
    IRON_BOOTS(null, "309"),
    /** 钻石头盔 **/
    DIAMOND_HELMET(null, "310"),
    /** 钻石胸甲 **/
    DIAMOND_CHESTPLATE(null, "311"),
    /** 钻石护腿 **/
    DIAMOND_LEGGINGS(null, "312"),
    /** 钻石靴子 **/
    DIAMOND_BOOTS(null, "313"),
    /** 金头盔 **/
    GOLDEN_HELMET("GOLD_HELMET", "314"),
    /** 金胸甲 **/
    GOLDEN_CHESTPLATE("GOLD_CHESTPLATE", "315"),
    /** 金护腿 **/
    GOLDEN_LEGGINGS("GOLD_LEGGINGS", "316"),
    /** 金靴子 **/
    GOLDEN_BOOTS("GOLD_BOOTS", "317"),
    /** 燧石 **/
    FLINT(null, "318"),
    /** 生猪排 **/
    PORKCHOP("PORK", "319"),
    /** 熟猪排 **/
    COOKED_PORKCHOP("GRILLED_PORK", "320"),
    /** 画 **/
    PAINTING(null, "321"),
    /** 金苹果 **/
    GOLDEN_APPLE(null, "322", "322:0"),
    /** 金苹果 **/
    ENCHANTED_GOLDEN_APPLE("GOLDEN_APPLE", "322:1"),
    /** 告示牌 **/
    OAK_SIGN("SIGN", "323"),
    /** 橡木门 **/
    OAK_DOOR("WOOD_DOOR", "324"),
    /** 桶 **/
    BUCKET(null, "325"),
    /** 水桶 **/
    WATER_BUCKET(null, "326"),
    /** 熔岩桶 **/
    LAVA_BUCKET(null, "327"),
    /** 矿车 **/
    MINECART(null, "328"),
    /** 鞍 **/
    SADDLE(null, "329"),
    /** 铁门 **/
    IRON_DOOR(null, "330"),
    /** 红石 **/
    REDSTONE(null, "331"),
    /** 雪球 **/
    SNOWBALL("SNOW_BALL", "332"),
    /** 橡木船 **/
    OAK_BOAT("BOAT", "333"),
    /** 皮革 **/
    LEATHER(null, "334"),
    /** 牛奶 **/
    MILK_BUCKET(null, "335"),
    /** 粘土 **/
    CLAY_BALL(null, "337"),
    /** 甘蔗 **/
    SUGAR_CANE(null, "338"),
    /** 纸 **/
    PAPER(null, "339"),
    /** 书 **/
    BOOK(null, "340"),
    /** 粘液球 **/
    SLIME_BALL(null, "341"),
    /** 运输矿车 **/
    CHEST_MINECART("STORAGE_MINECART", "342"),
    /** 动力矿车 **/
    FURNACE_MINECART("POWERED_MINECART", "343"),
    /** 鸡蛋 **/
    EGG(null, "344"),
    /** 指南针 **/
    COMPASS(null, "345"),
    /** 钓鱼竿 **/
    FISHING_ROD(null, "346"),
    /** 钟 **/
    CLOCK("WATCH", "347"),
    /** 荧石粉 **/
    GLOWSTONE_DUST(null, "348"),
    /** 生鱼 **/
    COD("RAW_FISH", "349", "349:0"),
    /** 生鲑鱼 **/
    SALMON("RAW_FISH", "349:1"),
    /** 小丑鱼 **/
    TROPICAL_FISH("RAW_FISH", "349:2"),
    /** 河豚 **/
    PUFFERFISH("RAW_FISH", "349:3"),
    /** 熟鱼 **/
    COOKED_COD("COOKED_FISH", "350", "350:0"),
    /** 熟鲑鱼 **/
    COOKED_SALMON("COOKED_FISH", "350:1"),
    /** 墨囊 **/
    INK_SAC("INK_SACK", "351", "351:0"),
    /** 玫瑰红 **/
    RED_DYE("INK_SACK", "351:1"),
    /** 仙人掌绿 **/
    GREEN_DYE("INK_SACK", "351:2"),
    /** 可可豆 **/
    COCOA_BEANS("INK_SACK", "351:3"),
    /** 青金石 **/
    LAPIS_LAZULI("INK_SACK", "351:4"),
    /** 紫色染料 **/
    PURPLE_DYE("INK_SACK", "351:5"),
    /** 青色染料 **/
    CYAN_DYE("INK_SACK", "351:6"),
    /** 淡灰色染料 **/
    LIGHT_GRAY_DYE("INK_SACK", "351:7"),
    /** 灰色染料 **/
    GRAY_DYE("INK_SACK", "351:8"),
    /** 粉红色染料 **/
    PINK_DYE("INK_SACK", "351:9"),
    /** 黄绿色染料 **/
    LIME_DYE("INK_SACK", "351:10"),
    /** 蒲公英黄 **/
    YELLOW_DYE("INK_SACK", "351:11"),
    /** 淡蓝色染料 **/
    LIGHT_BLUE_DYE("INK_SACK", "351:12"),
    /** 品红色染料 **/
    MAGENTA_DYE("INK_SACK", "351:13"),
    /** 橙色染料 **/
    ORANGE_DYE("INK_SACK", "351:14"),
    /** 骨粉 **/
    BONE_MEAL("INK_SACK", "351:15"),
    /** 骨头 **/
    BONE(null, "352"),
    /** 糖 **/
    SUGAR(null, "353"),
    /** 蛋糕 **/
    CAKE(null, "354"),
    /** 白色床 **/
    WHITE_BED("BED", "355", "355:0"),
    /** 橙色床 **/
    ORANGE_BED("BED", "355:1"),
    /** 品红色床 **/
    MAGENTA_BED("BED", "355:2"),
    /** 淡蓝色床 **/
    LIGHT_BLUE_BED("BED", "355:3"),
    /** 黄色床 **/
    YELLOW_BED("BED", "355:4"),
    /** 黄绿色床 **/
    LIME_BED("BED", "355:5"),
    /** 粉红色床 **/
    PINK_BED("BED", "355:6"),
    /** 灰色床 **/
    GRAY_BED("BED", "355:7"),
    /** 淡灰色床 **/
    LIGHT_GRAY_BED("BED", "355:8"),
    /** 青色床 **/
    CYAN_BED("BED", "355:9"),
    /** 紫色床 **/
    PURPLE_BED("BED", "355:10"),
    /** 蓝色床 **/
    BLUE_BED("BED", "355:11"),
    /** 棕色床 **/
    BROWN_BED("BED", "355:12"),
    /** 绿色床 **/
    GREEN_BED("BED", "355:13"),
    /** 红色床 **/
    RED_BED("BED", "355:14"),
    /** 黑色床 **/
    BLACK_BED("BED", "355:15"),
    /** 红石中继器 **/
    REPEATER("DIODE", "356"),
    /** 曲奇 **/
    COOKIE(null, "357"),
    /** 地图 **/
    MAP(null, "358", "395"),
    /** 剪刀 **/
    SHEARS(null, "359"),
    /** 南瓜种子 **/
    PUMPKIN_SEEDS(null, "361"),
    /** 西瓜种子 **/
    MELON_SEEDS(null, "362"),
    /** 生牛肉 **/
    BEEF("RAW_BEEF", "363"),
    /** 牛排 **/
    COOKED_BEEF(null, "364"),
    /** 生鸡肉 **/
    CHICKEN("RAW_CHICKEN", "365"),
    /** 熟鸡肉 **/
    COOKED_CHICKEN(null, "366"),
    /** 腐肉 **/
    ROTTEN_FLESH(null, "367"),
    /** 末影珍珠 **/
    ENDER_PEARL(null, "368"),
    /** 烈焰棒 **/
    BLAZE_ROD(null, "369"),
    /** 恶魂之泪 **/
    GHAST_TEAR(null, "370"),
    /** 金粒 **/
    GOLD_NUGGET(null, "371"),
    /** 地狱疣 **/
    NETHER_WART("NETHER_STALK", "372"),
    /** 药水 **/
    POTION(null, "373"),
    /** 玻璃瓶 **/
    GLASS_BOTTLE(null, "374"),
    /** 蜘蛛眼 **/
    SPIDER_EYE(null, "375"),
    /** 发酵蛛眼 **/
    FERMENTED_SPIDER_EYE(null, "376"),
    /** 烈焰粉 **/
    BLAZE_POWDER(null, "377"),
    /** 岩浆膏 **/
    MAGMA_CREAM(null, "378"),
    /** 酿造台 **/
    BREWING_STAND("BREWING_STAND_ITEM", "379"),
    /** 炼药锅 **/
    CAULDRON("CAULDRON_ITEM", "380"),
    /** 末影之眼 **/
    ENDER_EYE("EYE_OF_ENDER", "381"),
    /** 闪烁的西瓜 **/
    GLISTERING_MELON_SLICE("SPECKLED_MELON", "382"),
    /** 生成 **/
    INFESTED_COBBLESTONE("MONSTER_EGG", "383:1"),
    /** 生成 **/
    INFESTED_STONE_BRICKS("MONSTER_EGG", "383:2"),
    /** 生成 **/
    INFESTED_MOSSY_STONE_BRICKS("MONSTER_EGG", "383:3"),
    /** 生成 **/
    INFESTED_CRACKED_STONE_BRICKS("MONSTER_EGG", "383:4"),
    /** 生成 **/
    INFESTED_CHISELED_STONE_BRICKS("MONSTER_EGG", "383:5"),
    /** 附魔之瓶 **/
    EXPERIENCE_BOTTLE("EXP_BOTTLE", "384"),
    /** 火焰弹 **/
    FIRE_CHARGE("FIREBALL", "385"),
    /** 书与笔 **/
    WRITABLE_BOOK("BOOK_AND_QUILL", "386"),
    /** 成书 **/
    WRITTEN_BOOK(null, "387"),
    /** 绿宝石 **/
    EMERALD(null, "388"),
    /** 物品展示框 **/
    ITEM_FRAME(null, "389"),
    /** 花盆 **/
    FLOWER_POT("FLOWER_POT_ITEM", "390"),
    /** 胡萝卜 **/
    CARROT("CARROT_ITEM", "391"),
    /** 马铃薯 **/
    POTATO("POTATO_ITEM", "392"),
    /** 烤马铃薯 **/
    BAKED_POTATO(null, "393"),
    /** 毒马铃薯 **/
    POISONOUS_POTATO(null, "394"),
    /** 金胡萝卜 **/
    GOLDEN_CARROT(null, "396"),
    /** 骷髅头颅 **/
    SKELETON_SKULL("SKULL_ITEM", "397", "397:0"),
    /** 凋灵骷髅头颅 **/
    WITHER_SKELETON_SKULL("SKULL_ITEM", "397:1"),
    /** 僵尸的头 **/
    ZOMBIE_HEAD("SKULL_ITEM", "397:2"),
    /** 头 **/
    PLAYER_HEAD("SKULL_ITEM", "397:3"),
    /** 爬行者的头 **/
    CREEPER_HEAD("SKULL_ITEM", "397:4"),
    /** 龙首 **/
    DRAGON_HEAD("SKULL_ITEM", "397:5"),
    /** 胡萝卜钓竿 **/
    CARROT_ON_A_STICK("CARROT_STICK", "398"),
    /** 下界之星 **/
    NETHER_STAR(null, "399"),
    /** 南瓜派 **/
    PUMPKIN_PIE(null, "400"),
    /** 烟花火箭 **/
    FIREWORK_ROCKET("FIREWORK", "401"),
    /** 烟火之星 **/
    FIREWORK_STAR("FIREWORK_CHARGE", "402"),
    /** 附魔书 **/
    ENCHANTED_BOOK(null, "403"),
    /** 红石比较器 **/
    COMPARATOR("REDSTONE_COMPARATOR", "404"),
    /** 下界石英 **/
    QUARTZ(null, "406"),
    /** TNT矿车 **/
    TNT_MINECART("EXPLOSIVE_MINECART", "407"),
    /** 漏斗矿车 **/
    HOPPER_MINECART(null, "408"),
    /** 海晶碎片 **/
    PRISMARINE_SHARD(null, "409"),
    /** 海晶砂粒 **/
    PRISMARINE_CRYSTALS(null, "410"),
    /** 生兔肉 **/
    RABBIT(null, "411"),
    /** 熟兔肉 **/
    COOKED_RABBIT(null, "412"),
    /** 兔肉煲 **/
    RABBIT_STEW(null, "413"),
    /** 兔子脚 **/
    RABBIT_FOOT(null, "414"),
    /** 兔子皮 **/
    RABBIT_HIDE(null, "415"),
    /** 盔甲架 **/
    ARMOR_STAND(null, "416"),
    /** 铁马铠 **/
    IRON_HORSE_ARMOR("IRON_BARDING", "417"),
    /** 金马铠 **/
    GOLDEN_HORSE_ARMOR("GOLD_BARDING", "418"),
    /** 钻石马铠 **/
    DIAMOND_HORSE_ARMOR("DIAMOND_BARDING", "419"),
    /** 拴绳 **/
    LEAD("LEASH", "420"),
    /** 命名牌 **/
    NAME_TAG(null, "421"),
    /** 命令方块矿车 **/
    COMMAND_BLOCK_MINECART("COMMAND_MINECART", "422"),
    /** 生羊肉 **/
    MUTTON(null, "423"),
    /** 熟羊肉 **/
    COOKED_MUTTON(null, "424"),
    /** 黑色旗帜 **/
    BLACK_BANNER("BANNER", "425", "425:0"),
    /** 红色旗帜 **/
    RED_BANNER("BANNER", "425:1"),
    /** 绿色旗帜 **/
    GREEN_BANNER("BANNER", "425:2"),
    /** 棕色旗帜 **/
    BROWN_BANNER("BANNER", "425:3"),
    /** 蓝色旗帜 **/
    BLUE_BANNER("BANNER", "425:4"),
    /** 紫色旗帜 **/
    PURPLE_BANNER("BANNER", "425:5"),
    /** 青色旗帜 **/
    CYAN_BANNER("BANNER", "425:6"),
    /** 淡灰色旗帜 **/
    LIGHT_GRAY_BANNER("BANNER", "425:7"),
    /** 灰色旗帜 **/
    GRAY_BANNER("BANNER", "425:8"),
    /** 粉红色旗帜 **/
    PINK_BANNER("BANNER", "425:9"),
    /** 黄绿色旗帜 **/
    LIME_BANNER("BANNER", "425:10"),
    /** 黄色旗帜 **/
    YELLOW_BANNER("BANNER", "425:11"),
    /** 淡蓝色旗帜 **/
    LIGHT_BLUE_BANNER("BANNER", "425:12"),
    /** 品红色旗帜 **/
    MAGENTA_BANNER("BANNER", "425:13"),
    /** 橙色旗帜 **/
    ORANGE_BANNER("BANNER", "425:14"),
    /** 白色旗帜 **/
    WHITE_BANNER("BANNER", "425:15"),
    /** 末影水晶 **/
    END_CRYSTAL(null, "426"),
    /** 云杉木门 **/
    SPRUCE_DOOR("SPRUCE_DOOR_ITEM", "427"),
    /** 白桦木门 **/
    BIRCH_DOOR("BIRCH_DOOR_ITEM", "428"),
    /** 丛林木门 **/
    JUNGLE_DOOR("JUNGLE_DOOR_ITEM", "429"),
    /** 金合欢木门 **/
    ACACIA_DOOR("ACACIA_DOOR_ITEM", "430"),
    /** 深色橡木门 **/
    DARK_OAK_DOOR("DARK_OAK_DOOR_ITEM", "431"),
    /** 紫颂果 **/
    CHORUS_FRUIT(null, "432"),
    /** 爆裂紫颂果 **/
    POPPED_CHORUS_FRUIT("CHORUS_FRUIT_POPPED", "433"),
    /** 甜菜根 **/
    BEETROOT(null, "434"),
    /** 甜菜种子 **/
    BEETROOT_SEEDS(null, "435"),
    /** 甜菜汤 **/
    BEETROOT_SOUP(null, "436"),
    /** 龙息 **/
    DRAGON_BREATH("DRAGONS_BREATH", "437"),
    /** 喷溅药水 **/
    SPLASH_POTION(null, "438"),
    /** 光灵箭 **/
    SPECTRAL_ARROW(null, "439"),
    /** 药箭 **/
    TIPPED_ARROW(null, "440"),
    /** 滞留药水 **/
    LINGERING_POTION(null, "441"),
    /** 盾牌 **/
    SHIELD(null, "442"),
    /** 鞘翅 **/
    ELYTRA(null, "443"),
    /** 云杉木船 **/
    SPRUCE_BOAT("BOAT_SPRUCE", "444"),
    /** 白桦木船 **/
    BIRCH_BOAT("BOAT_BIRCH", "445"),
    /** 丛林木船 **/
    JUNGLE_BOAT("BOAT_JUNGLE", "446"),
    /** 金合欢木船 **/
    ACACIA_BOAT("BOAT_ACACIA", "447"),
    /** 深色橡木船 **/
    DARK_OAK_BOAT("BOAT_DARK_OAK", "448"),
    /** 不死图腾 **/
    TOTEM_OF_UNDYING("TOTEM", "449"),
    /** 潜影壳 **/
    SHULKER_SHELL(null, "450"),
    /** 铁粒 **/
    IRON_NUGGET(null, "452"),
    /** 知识之书 **/
    KNOWLEDGE_BOOK(null, "453"),
    /** 音乐唱片 **/
    MUSIC_DISC_13("GOLD_RECORD", "2256"),
    /** 音乐唱片 **/
    MUSIC_DISC_CAT("GREEN_RECORD", "2257"),
    /** 音乐唱片 **/
    MUSIC_DISC_BLOCKS("RECORD_3", "2258"),
    /** 音乐唱片 **/
    MUSIC_DISC_CHIRP("RECORD_4", "2259"),
    /** 音乐唱片 **/
    MUSIC_DISC_FAR("RECORD_5", "2260"),
    /** 音乐唱片 **/
    MUSIC_DISC_MALL("RECORD_6", "2261"),
    /** 音乐唱片 **/
    MUSIC_DISC_MELLOHI("RECORD_7", "2262"),
    /** 音乐唱片 **/
    MUSIC_DISC_STAL("RECORD_8", "2263"),
    /** 音乐唱片 **/
    MUSIC_DISC_STRAD("RECORD_9", "2264"),
    /** 音乐唱片 **/
    MUSIC_DISC_WARD("RECORD_10", "2265"),
    /** 音乐唱片 **/
    MUSIC_DISC_11("RECORD_11", "2266"),
    /** 音乐唱片 **/
    MUSIC_DISC_WAIT("RECORD_12", "2267");

    private final String materialName;
    private short subId;
    private Material material;

    ReMaterial(String legacyName, String... ids) {
        this.materialName = (legacyName != null && Data.isLegacy) ? legacyName : name();
        for (String id : ids) {
            int index = id.indexOf(':');
            if (index != -1) {
                subId = Short.parseShort(id.substring(index + 1));
            }
            Data.BY_ID.put(id, this);
        }
    }

    /**
     * 获取材质
     *
     * @return
     */
    @Nonnull
    public Material material() {
        if (material != null) return material;
        return material = Material.getMaterial(materialName);
    }

    /**
     * 获取物品★
     *
     * @return
     */
    @Nonnull
    public ItemStack item() {
        return new ItemStack(material(), 1, subId);
    }

    /**
     * 根据数字id获取材质
     * <br> 如果存在':' 那么会尝试两次
     *
     * @param key 数字ID / 英文ID
     * @return 数字ID在
     */
    @Nullable
    public static Material getMaterial(String key) {
        int index = key.indexOf(':');
        if (index != -1) {
            ReMaterial fullValue = Data.BY_ID.get(key);
            if (fullValue != null) return fullValue.material();
            key = key.substring(0, index);
        }

        ReMaterial value = Data.BY_ID.get(key);
        if (value != null) return value.material();
        return Material.matchMaterial(key);
    }

    /**
     * 根据数字id获取物品★
     * <br> 如果存在':' 那么会尝试两次
     *
     * @param key 数字ID / 英文ID
     */
    @Nullable
    public static ItemStack getItem(String key) {
        ReMaterial value = Data.BY_ID.get(key);
        if (value != null) return value.item();

        int index = key.indexOf(':');
        if (index != -1) {
            key = key.substring(0, index);
            value = Data.BY_ID.get(key);
            if (value != null) return value.item();
        }

        Material material = Material.matchMaterial(key);
        if (material != null) return new ItemStack(material);
        return null;
    }

    /**
     * 判断是否存在
     *
     * @param key 数字ID / 英文ID
     */
    public static boolean has(String key) {
        return Data.BY_ID.containsKey(key) || Material.matchMaterial(key) != null;
    }

    /**
     * 通过材质查找兼容ID, 没有则返回empty
     *
     * @param material
     */
    public static String getKey(Material material) {
        for (Map.Entry<String, ReMaterial> value : Data.BY_ID.entrySet()) {
            if (value.getValue().materialName.equals(material.name())) {
                return value.getKey();
            }
        }
        return "";
    }

    /**
     * 返回所有兼容ID
     */
    public static Set<String> getKeys() {
        return Data.BY_ID.keySet();
    }

    static class Data {
        private static final boolean isLegacy = NMS.compareTo(1, 13, 2) < 0;
        private static final Map<String, ReMaterial> BY_ID = new HashMap<>(662);
    }
}
