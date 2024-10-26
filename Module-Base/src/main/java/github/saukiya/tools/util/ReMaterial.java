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
    AIR(null, "0", "144"),
    STONE(null, "1"),
    GRASS_BLOCK("GRASS", "2"),
    DIRT(null, "3"),
    COBBLESTONE(null, "4"),
    OAK_PLANKS("WOOD", "5"),
    OAK_SAPLING("SAPLING", "6"),
    BEDROCK(null, "7"),
    WATER(null, "8", "9"),
    LAVA(null, "10", "11"),
    SAND(null, "12"),
    GRAVEL(null, "13"),
    GOLD_ORE(null, "14"),
    IRON_ORE(null, "15"),
    COAL_ORE(null, "16"),
    OAK_LOG("LOG", "17"),
    OAK_LEAVES("LEAVES", "18"),
    SPONGE(null, "19"),
    GLASS(null, "20"),
    LAPIS_ORE(null, "21"),
    LAPIS_BLOCK(null, "22"),
    DISPENSER(null, "23"),
    SANDSTONE(null, "24"),
    NOTE_BLOCK(null, "25"),
    RED_BED("BED", "355:14", "26"),
    POWERED_RAIL(null, "27"),
    DETECTOR_RAIL(null, "28"),
    STICKY_PISTON("PISTON_STICKY_BASE", "29"),
    COBWEB("WEB", "30"),
    DEAD_BUSH(null, "32", "31"),
    PISTON("PISTON_BASE", "33"),
    PISTON_HEAD("PISTON_EXTENSION", "34"),
    MOVING_PISTON("PISTON_MOVING_PIECE", "36"),
    DANDELION("YELLOW_FLOWER", "37"),
    POPPY("RED_ROSE", "38"),
    BROWN_MUSHROOM(null, "39"),
    RED_MUSHROOM(null, "40"),
    GOLD_BLOCK(null, "41"),
    IRON_BLOCK(null, "42"),
    SMOOTH_STONE_SLAB("DOUBLE_STEP", "43", "44"),
    BRICK(null, "45", "336"),
    TNT(null, "46"),
    BOOKSHELF(null, "47"),
    MOSSY_COBBLESTONE(null, "48"),
    OBSIDIAN(null, "49"),
    TORCH(null, "50"),
    FIRE(null, "51"),
    SPAWNER("MOB_SPAWNER", "52"),
    OAK_STAIRS("WOOD_STAIRS", "53"),
    CHEST(null, "54"),
    REDSTONE_WIRE(null, "55"),
    DIAMOND_ORE(null, "56"),
    DIAMOND_BLOCK(null, "57"),
    CRAFTING_TABLE("WORKBENCH", "58"),
    WHEAT(null, "296", "59"),
    FARMLAND("SOIL", "60"),
    FURNACE(null, "61", "62"),
    OAK_SIGN("SIGN_POST", "63", "323"),
    OAK_DOOR("WOODEN_DOOR", "64", "324"),
    LADDER(null, "65"),
    RAIL("RAILS", "66"),
    COBBLESTONE_STAIRS(null, "67"),
    OAK_WALL_SIGN("WALL_SIGN", "68"),
    LEVER(null, "69"),
    STONE_PRESSURE_PLATE("STONE_PLATE", "70"),
    IRON_DOOR(null, "330", "71"),
    OAK_PRESSURE_PLATE("WOOD_PLATE", "72"),
    REDSTONE_ORE(null, "73", "74"),
    REDSTONE_WALL_TORCH("REDSTONE_TORCH_OFF", "75", "76"),
    STONE_BUTTON(null, "77"),
    SNOW(null, "78"),
    ICE(null, "79"),
    SNOW_BLOCK(null, "80"),
    CACTUS(null, "81"),
    CLAY(null, "82"),
    SUGAR_CANE(null, "338", "83"),
    JUKEBOX(null, "84"),
    OAK_FENCE("FENCE", "85"),
    PUMPKIN(null, "86"),
    NETHERRACK(null, "87"),
    SOUL_SAND(null, "88"),
    GLOWSTONE(null, "89"),
    NETHER_PORTAL("PORTAL", "90"),
    JACK_O_LANTERN(null, "91"),
    CAKE(null, "354", "92"),
    REPEATER("DIODE_BLOCK_OFF", "93", "94", "356"),
    OAK_TRAPDOOR("TRAP_DOOR", "96"),
    INFESTED_STONE("MONSTER_EGGS", "97"),
    STONE_BRICKS("SMOOTH_BRICK", "98"),
    BROWN_MUSHROOM_BLOCK("HUGE_MUSHROOM_1", "99"),
    RED_MUSHROOM_BLOCK("HUGE_MUSHROOM_2", "100"),
    IRON_BARS("IRON_FENCE", "101"),
    GLASS_PANE("THIN_GLASS", "102"),
    MELON(null, "360", "103"),
    PUMPKIN_STEM(null, "104"),
    MELON_STEM(null, "105"),
    VINE(null, "106"),
    OAK_FENCE_GATE("FENCE_GATE", "107"),
    BRICK_STAIRS(null, "108"),
    STONE_BRICK_STAIRS("SMOOTH_STAIRS", "109"),
    MYCELIUM("MYCEL", "110"),
    LILY_PAD("WATER_LILY", "111"),
    NETHER_BRICK(null, "112", "405"),
    NETHER_BRICK_FENCE("NETHER_FENCE", "113"),
    NETHER_BRICK_STAIRS(null, "114"),
    NETHER_WART("NETHER_WARTS", "115", "372"),
    ENCHANTING_TABLE("ENCHANTMENT_TABLE", "116"),
    BREWING_STAND(null, "117", "379"),
    CAULDRON(null, "118", "380"),
    END_PORTAL("ENDER_PORTAL", "119"),
    END_PORTAL_FRAME("ENDER_PORTAL_FRAME", "120"),
    END_STONE("ENDER_STONE", "121"),
    DRAGON_EGG(null, "122"),
    REDSTONE_LAMP("REDSTONE_LAMP_OFF", "123", "124"),
    OAK_SLAB("WOOD_DOUBLE_STEP", "125", "126"),
    COCOA(null, "127"),
    SANDSTONE_STAIRS(null, "128"),
    EMERALD_ORE(null, "129"),
    ENDER_CHEST(null, "130"),
    TRIPWIRE_HOOK(null, "131"),
    TRIPWIRE(null, "132"),
    EMERALD_BLOCK(null, "133"),
    SPRUCE_STAIRS("SPRUCE_WOOD_STAIRS", "134"),
    BIRCH_STAIRS("BIRCH_WOOD_STAIRS", "135"),
    JUNGLE_STAIRS("JUNGLE_WOOD_STAIRS", "136"),
    COMMAND_BLOCK("COMMAND", "137"),
    BEACON(null, "138"),
    COBBLESTONE_WALL("COBBLE_WALL", "139"),
    FLOWER_POT(null, "140", "390"),
    CARROT(null, "141", "391"),
    POTATO(null, "142", "392"),
    OAK_BUTTON("WOOD_BUTTON", "143"),
    ANVIL(null, "145"),
    TRAPPED_CHEST(null, "146"),
    LIGHT_WEIGHTED_PRESSURE_PLATE("GOLD_PLATE", "147"),
    HEAVY_WEIGHTED_PRESSURE_PLATE("IRON_PLATE", "148"),
    COMPARATOR("REDSTONE_COMPARATOR_OFF", "149", "150", "404"),
    DAYLIGHT_DETECTOR(null, "151", "178"),
    REDSTONE_BLOCK(null, "152"),
    NETHER_QUARTZ_ORE("QUARTZ_ORE", "153"),
    HOPPER(null, "154"),
    QUARTZ_BLOCK(null, "155"),
    QUARTZ_STAIRS(null, "156"),
    ACTIVATOR_RAIL(null, "157"),
    DROPPER(null, "158"),
    WHITE_TERRACOTTA("STAINED_CLAY", "159"),
    ACACIA_LEAVES("LEAVES_2", "161"),
    ACACIA_LOG("LOG_2", "162"),
    ACACIA_STAIRS(null, "163"),
    DARK_OAK_STAIRS(null, "164"),
    SLIME_BLOCK(null, "165"),
    BARRIER(null, "166"),
    IRON_TRAPDOOR(null, "167"),
    PRISMARINE(null, "168"),
    SEA_LANTERN(null, "169"),
    HAY_BLOCK(null, "170"),
    TERRACOTTA("HARD_CLAY", "172"),
    COAL_BLOCK(null, "173"),
    PACKED_ICE(null, "174"),
    SUNFLOWER("DOUBLE_PLANT", "175"),
    WHITE_BANNER("BANNER", "425", "425:0", "176"),
    WHITE_WALL_BANNER("WALL_BANNER", "177"),
    RED_SANDSTONE(null, "179"),
    RED_SANDSTONE_STAIRS(null, "180"),
    RED_SANDSTONE_SLAB("DOUBLE_STONE_SLAB2", "181", "182"),
    SPRUCE_FENCE_GATE(null, "183"),
    BIRCH_FENCE_GATE(null, "184"),
    JUNGLE_FENCE_GATE(null, "185"),
    DARK_OAK_FENCE_GATE(null, "186"),
    ACACIA_FENCE_GATE(null, "187"),
    SPRUCE_FENCE(null, "188"),
    BIRCH_FENCE(null, "189"),
    JUNGLE_FENCE(null, "190"),
    DARK_OAK_FENCE(null, "191"),
    ACACIA_FENCE(null, "192"),
    SPRUCE_DOOR(null, "193", "427"),
    BIRCH_DOOR(null, "194", "428"),
    JUNGLE_DOOR(null, "195", "429"),
    ACACIA_DOOR(null, "196", "430"),
    DARK_OAK_DOOR(null, "197", "431"),
    END_ROD(null, "198"),
    CHORUS_PLANT(null, "199"),
    CHORUS_FLOWER(null, "200"),
    PURPUR_BLOCK(null, "201"),
    PURPUR_PILLAR(null, "202"),
    PURPUR_STAIRS(null, "203"),
    PURPUR_SLAB(null, "205", "204"),
    END_STONE_BRICKS("END_BRICKS", "206"),
    BEETROOTS("BEETROOT_BLOCK", "207"),
    DIRT_PATH("GRASS_PATH", "208"),
    END_GATEWAY(null, "209"),
    REPEATING_COMMAND_BLOCK("COMMAND_REPEATING", "210"),
    CHAIN_COMMAND_BLOCK("COMMAND_CHAIN", "211"),
    FROSTED_ICE(null, "212"),
    MAGMA_BLOCK("MAGMA", "213"),
    NETHER_WART_BLOCK(null, "214"),
    RED_NETHER_BRICKS("RED_NETHER_BRICK", "215"),
    BONE_BLOCK(null, "216"),
    STRUCTURE_VOID(null, "217"),
    OBSERVER(null, "218"),
    WHITE_SHULKER_BOX(null, "219"),
    ORANGE_SHULKER_BOX(null, "220"),
    MAGENTA_SHULKER_BOX(null, "221"),
    LIGHT_BLUE_SHULKER_BOX(null, "222"),
    YELLOW_SHULKER_BOX(null, "223"),
    LIME_SHULKER_BOX(null, "224"),
    PINK_SHULKER_BOX(null, "225"),
    GRAY_SHULKER_BOX(null, "226"),
    LIGHT_GRAY_SHULKER_BOX("SILVER_SHULKER_BOX", "227"),
    CYAN_SHULKER_BOX(null, "228"),
    PURPLE_SHULKER_BOX(null, "229"),
    BLUE_SHULKER_BOX(null, "230"),
    BROWN_SHULKER_BOX(null, "231"),
    GREEN_SHULKER_BOX(null, "232"),
    RED_SHULKER_BOX(null, "233"),
    BLACK_SHULKER_BOX(null, "234"),
    WHITE_GLAZED_TERRACOTTA(null, "235"),
    ORANGE_GLAZED_TERRACOTTA(null, "236"),
    MAGENTA_GLAZED_TERRACOTTA(null, "237"),
    LIGHT_BLUE_GLAZED_TERRACOTTA(null, "238"),
    YELLOW_GLAZED_TERRACOTTA(null, "239"),
    LIME_GLAZED_TERRACOTTA(null, "240"),
    PINK_GLAZED_TERRACOTTA(null, "241"),
    GRAY_GLAZED_TERRACOTTA(null, "242"),
    LIGHT_GRAY_GLAZED_TERRACOTTA("SILVER_GLAZED_TERRACOTTA", "243"),
    CYAN_GLAZED_TERRACOTTA(null, "244"),
    PURPLE_GLAZED_TERRACOTTA(null, "245"),
    BLUE_GLAZED_TERRACOTTA(null, "246"),
    BROWN_GLAZED_TERRACOTTA(null, "247"),
    GREEN_GLAZED_TERRACOTTA(null, "248"),
    RED_GLAZED_TERRACOTTA(null, "249"),
    BLACK_GLAZED_TERRACOTTA(null, "250"),
    STRUCTURE_BLOCK(null, "255"),
    IRON_SHOVEL("IRON_SPADE", "256"),
    IRON_PICKAXE(null, "257"),
    IRON_AXE(null, "258"),
    FLINT_AND_STEEL(null, "259"),
    APPLE(null, "260"),
    BOW(null, "261"),
    ARROW(null, "262"),
    COAL(null, "263"),
    DIAMOND(null, "264"),
    IRON_INGOT(null, "265"),
    GOLD_INGOT(null, "266"),
    IRON_SWORD(null, "267"),
    WOODEN_SWORD("WOOD_SWORD", "268"),
    WOODEN_SHOVEL("WOOD_SPADE", "269"),
    WOODEN_PICKAXE("WOOD_PICKAXE", "270"),
    WOODEN_AXE("WOOD_AXE", "271"),
    STONE_SWORD(null, "272"),
    STONE_SHOVEL("STONE_SPADE", "273"),
    STONE_PICKAXE(null, "274"),
    STONE_AXE(null, "275"),
    DIAMOND_SWORD(null, "276"),
    DIAMOND_SHOVEL("DIAMOND_SPADE", "277"),
    DIAMOND_PICKAXE(null, "278"),
    DIAMOND_AXE(null, "279"),
    STICK(null, "280"),
    BOWL(null, "281"),
    MUSHROOM_STEW("MUSHROOM_SOUP", "282"),
    GOLDEN_SWORD("GOLD_SWORD", "283"),
    GOLDEN_SHOVEL("GOLD_SPADE", "284"),
    GOLDEN_PICKAXE("GOLD_PICKAXE", "285"),
    GOLDEN_AXE("GOLD_AXE", "286"),
    STRING(null, "287"),
    FEATHER(null, "288"),
    GUNPOWDER("SULPHUR", "289"),
    WOODEN_HOE("WOOD_HOE", "290"),
    STONE_HOE(null, "291"),
    IRON_HOE(null, "292"),
    DIAMOND_HOE(null, "293"),
    GOLDEN_HOE("GOLD_HOE", "294"),
    WHEAT_SEEDS("SEEDS", "295"),
    BREAD(null, "297"),
    LEATHER_HELMET(null, "298"),
    LEATHER_CHESTPLATE(null, "299"),
    LEATHER_LEGGINGS(null, "300"),
    LEATHER_BOOTS(null, "301"),
    CHAINMAIL_HELMET(null, "302"),
    CHAINMAIL_CHESTPLATE(null, "303"),
    CHAINMAIL_LEGGINGS(null, "304"),
    CHAINMAIL_BOOTS(null, "305"),
    IRON_HELMET(null, "306"),
    IRON_CHESTPLATE(null, "307"),
    IRON_LEGGINGS(null, "308"),
    IRON_BOOTS(null, "309"),
    DIAMOND_HELMET(null, "310"),
    DIAMOND_CHESTPLATE(null, "311"),
    DIAMOND_LEGGINGS(null, "312"),
    DIAMOND_BOOTS(null, "313"),
    GOLDEN_HELMET("GOLD_HELMET", "314"),
    GOLDEN_CHESTPLATE("GOLD_CHESTPLATE", "315"),
    GOLDEN_LEGGINGS("GOLD_LEGGINGS", "316"),
    GOLDEN_BOOTS("GOLD_BOOTS", "317"),
    FLINT(null, "318"),
    PORKCHOP("PORK", "319"),
    COOKED_PORKCHOP("GRILLED_PORK", "320"),
    PAINTING(null, "321"),
    GOLDEN_APPLE(null, "322"),
    BUCKET(null, "325"),
    WATER_BUCKET(null, "326"),
    LAVA_BUCKET(null, "327"),
    MINECART(null, "328"),
    SADDLE(null, "329"),
    REDSTONE(null, "331"),
    SNOWBALL("SNOW_BALL", "332"),
    OAK_BOAT("BOAT", "333"),
    LEATHER(null, "334"),
    MILK_BUCKET(null, "335"),
    CLAY_BALL(null, "337"),
    PAPER(null, "339"),
    BOOK(null, "340"),
    SLIME_BALL(null, "341"),
    CHEST_MINECART("STORAGE_MINECART", "342"),
    FURNACE_MINECART("POWERED_MINECART", "343"),
    EGG(null, "344"),
    COMPASS(null, "345"),
    FISHING_ROD(null, "346"),
    CLOCK("WATCH", "347"),
    GLOWSTONE_DUST(null, "348"),
    COD("RAW_FISH", "349"),
    COOKED_COD("COOKED_FISH", "350"),
    INK_SAC("INK_SACK", "351"),
    BONE(null, "352"),
    SUGAR(null, "353"),
    COOKIE(null, "357"),
    MAP(null, "358", "395"),
    SHEARS(null, "359"),
    PUMPKIN_SEEDS(null, "361"),
    MELON_SEEDS(null, "362"),
    BEEF("RAW_BEEF", "363"),
    COOKED_BEEF(null, "364"),
    CHICKEN("RAW_CHICKEN", "365"),
    COOKED_CHICKEN(null, "366"),
    ROTTEN_FLESH(null, "367"),
    ENDER_PEARL(null, "368"),
    BLAZE_ROD(null, "369"),
    GHAST_TEAR(null, "370"),
    GOLD_NUGGET(null, "371"),
    POTION(null, "373"),
    GLASS_BOTTLE(null, "374"),
    SPIDER_EYE(null, "375"),
    FERMENTED_SPIDER_EYE(null, "376"),
    BLAZE_POWDER(null, "377"),
    MAGMA_CREAM(null, "378"),
    ENDER_EYE("EYE_OF_ENDER", "381"),
    GLISTERING_MELON_SLICE("SPECKLED_MELON", "382"),
    PIG_SPAWN_EGG("MONSTER_EGG", "383"),
    EXPERIENCE_BOTTLE("EXP_BOTTLE", "384"),
    FIRE_CHARGE("FIREBALL", "385"),
    WRITABLE_BOOK("BOOK_AND_QUILL", "386"),
    WRITTEN_BOOK(null, "387"),
    EMERALD(null, "388"),
    ITEM_FRAME(null, "389"),
    BAKED_POTATO(null, "393"),
    POISONOUS_POTATO(null, "394"),
    GOLDEN_CARROT(null, "396"),
    SKELETON_SKULL("SKULL_ITEM", "397"),
    CARROT_ON_A_STICK("CARROT_STICK", "398"),
    NETHER_STAR(null, "399"),
    PUMPKIN_PIE(null, "400"),
    FIREWORK_ROCKET("FIREWORK", "401"),
    FIREWORK_STAR("FIREWORK_CHARGE", "402"),
    ENCHANTED_BOOK(null, "403"),
    QUARTZ(null, "406"),
    TNT_MINECART("EXPLOSIVE_MINECART", "407"),
    HOPPER_MINECART(null, "408"),
    PRISMARINE_SHARD(null, "409"),
    PRISMARINE_CRYSTALS(null, "410"),
    RABBIT(null, "411"),
    COOKED_RABBIT(null, "412"),
    RABBIT_STEW(null, "413"),
    RABBIT_FOOT(null, "414"),
    RABBIT_HIDE(null, "415"),
    ARMOR_STAND(null, "416"),
    IRON_HORSE_ARMOR("IRON_BARDING", "417"),
    GOLDEN_HORSE_ARMOR("GOLD_BARDING", "418"),
    DIAMOND_HORSE_ARMOR("DIAMOND_BARDING", "419"),
    LEAD("LEASH", "420"),
    NAME_TAG(null, "421"),
    COMMAND_BLOCK_MINECART("COMMAND_MINECART", "422"),
    MUTTON(null, "423"),
    COOKED_MUTTON(null, "424"),
    END_CRYSTAL(null, "426"),
    CHORUS_FRUIT(null, "432"),
    POPPED_CHORUS_FRUIT("CHORUS_FRUIT_POPPED", "433"),
    BEETROOT(null, "434"),
    BEETROOT_SEEDS(null, "435"),
    BEETROOT_SOUP(null, "436"),
    DRAGON_BREATH("DRAGONS_BREATH", "437"),
    SPLASH_POTION(null, "438"),
    SPECTRAL_ARROW(null, "439"),
    TIPPED_ARROW(null, "440"),
    LINGERING_POTION(null, "441"),
    SHIELD(null, "442"),
    ELYTRA(null, "443"),
    SPRUCE_BOAT("BOAT_SPRUCE", "444"),
    BIRCH_BOAT("BOAT_BIRCH", "445"),
    JUNGLE_BOAT("BOAT_JUNGLE", "446"),
    ACACIA_BOAT("BOAT_ACACIA", "447"),
    DARK_OAK_BOAT("BOAT_DARK_OAK", "448"),
    TOTEM_OF_UNDYING("TOTEM", "449"),
    SHULKER_SHELL(null, "450"),
    IRON_NUGGET(null, "452"),
    KNOWLEDGE_BOOK(null, "453"),
    MUSIC_DISC_13("GOLD_RECORD", "2256"),
    MUSIC_DISC_CAT("GREEN_RECORD", "2257"),
    MUSIC_DISC_BLOCKS("RECORD_3", "2258"),
    MUSIC_DISC_CHIRP("RECORD_4", "2259"),
    MUSIC_DISC_FAR("RECORD_5", "2260"),
    MUSIC_DISC_MALL("RECORD_6", "2261"),
    MUSIC_DISC_MELLOHI("RECORD_7", "2262"),
    MUSIC_DISC_STAL("RECORD_8", "2263"),
    MUSIC_DISC_STRAD("RECORD_9", "2264"),
    MUSIC_DISC_WARD("RECORD_10", "2265"),
    MUSIC_DISC_11("RECORD_11", "2266"),
    MUSIC_DISC_WAIT("RECORD_12", "2267"),
    BROWN_CARPET("CARPET", "171:12"),
    GREEN_CONCRETE_POWDER("CONCRETE_POWDER", "252:13"),
    BROWN_BED("BED", "355:12"),
    BROWN_STAINED_GLASS("STAINED_GLASS", "95:12"),
    PURPLE_CARPET("CARPET", "171:10"),
    ORANGE_CARPET("CARPET", "171:1"),
    PINK_CARPET("CARPET", "171:6"),
    MAGENTA_BANNER("BANNER", "425:2"),
    LIME_CONCRETE_POWDER("CONCRETE_POWDER", "252:5"),
    PINK_BANNER("BANNER", "425:6"),
    GREEN_WOOL("WOOL", "35:13"),
    MAGENTA_CONCRETE_POWDER("CONCRETE_POWDER", "252:2"),
    BLACK_WOOL("WOOL", "35:15"),
    LIGHT_GRAY_CARPET("CARPET", "171:8"),
    PURPLE_CONCRETE("CONCRETE", "251:10"),
    RED_CARPET("CARPET", "171:14"),
    CYAN_BED("BED", "355:9"),
    MAGENTA_CARPET("CARPET", "171:2"),
    YELLOW_STAINED_GLASS("STAINED_GLASS", "95:4"),
    GRAY_STAINED_GLASS("STAINED_GLASS", "95:7"),
    BROWN_BANNER("BANNER", "425:12"),
    LIME_BED("BED", "355:5"),
    RED_BANNER("BANNER", "425:14"),
    BROWN_WOOL("WOOL", "35:12"),
    LIGHT_GRAY_BANNER("BANNER", "425:8"),
    GREEN_BANNER("BANNER", "425:13"),
    BLUE_STAINED_GLASS("STAINED_GLASS", "95:11"),
    BLUE_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "160:11"),
    PINK_CONCRETE("CONCRETE", "251:6"),
    LIME_BANNER("BANNER", "425:5"),
    ORANGE_CONCRETE_POWDER("CONCRETE_POWDER", "252:1"),
    ORANGE_WOOL("WOOL", "35:1"),
    LIME_WOOL("WOOL", "35:5"),
    PURPLE_BED("BED", "355:10"),
    BLACK_CONCRETE("CONCRETE", "251:15"),
    WHITE_WOOL("WOOL", "35", "35:0"),
    GRAY_CARPET("CARPET", "171:7"),
    LIGHT_BLUE_STAINED_GLASS("STAINED_GLASS", "95:3"),
    BLACK_STAINED_GLASS("STAINED_GLASS", "95:15"),
    WHITE_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "160", "160:0"),
    LIME_STAINED_GLASS("STAINED_GLASS", "95:5"),
    BLUE_BANNER("BANNER", "425:11"),
    GRAY_CONCRETE_POWDER("CONCRETE_POWDER", "252:7"),
    ORANGE_STAINED_GLASS("STAINED_GLASS", "95:1"),
    CYAN_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "160:9"),
    LIGHT_BLUE_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "160:3"),
    BLUE_CONCRETE_POWDER("CONCRETE_POWDER", "252:11"),
    RED_WOOL("WOOL", "35:14"),
    MAGENTA_STAINED_GLASS("STAINED_GLASS", "95:2"),
    BLUE_CONCRETE("CONCRETE", "251:11"),
    GREEN_BED("BED", "355:13"),
    LIGHT_BLUE_CARPET("CARPET", "171:3"),
    RED_STAINED_GLASS("STAINED_GLASS", "95:14"),
    LIME_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "160:5"),
    ORANGE_BANNER("BANNER", "425:1"),
    GREEN_STAINED_GLASS("STAINED_GLASS", "95:13"),
    GRAY_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "160:7"),
    PINK_STAINED_GLASS("STAINED_GLASS", "95:6"),
    BLUE_WOOL("WOOL", "35:11"),
    MAGENTA_WOOL("WOOL", "35:2"),
    LIGHT_BLUE_CONCRETE("CONCRETE", "251:3"),
    LIGHT_GRAY_CONCRETE_POWDER("CONCRETE_POWDER", "252:8"),
    PINK_BED("BED", "355:6"),
    CYAN_WOOL("WOOL", "35:9"),
    LIGHT_GRAY_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "160:8"),
    GREEN_CARPET("CARPET", "171:13"),
    WHITE_STAINED_GLASS("STAINED_GLASS", "95", "95:0"),
    YELLOW_CONCRETE("CONCRETE", "251:4"),
    LIGHT_BLUE_WOOL("WOOL", "35:3"),
    WHITE_CARPET("CARPET", "171", "171:0"),
    BLACK_BANNER("BANNER", "425:15"),
    LIGHT_BLUE_BED("BED", "355:3"),
    BLACK_CONCRETE_POWDER("CONCRETE_POWDER", "252:15"),
    PINK_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "160:6"),
    BLACK_BED("BED", "355:15"),
    BLUE_BED("BED", "355:11"),
    YELLOW_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "160:4"),
    PINK_CONCRETE_POWDER("CONCRETE_POWDER", "252:6"),
    BLACK_CARPET("CARPET", "171:15"),
    LIGHT_GRAY_CONCRETE("CONCRETE", "251:8"),
    LIGHT_BLUE_CONCRETE_POWDER("CONCRETE_POWDER", "252:3"),
    WHITE_BED("BED", "355", "355:0"),
    GRAY_BED("BED", "355:7"),
    ORANGE_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "160:1"),
    YELLOW_CARPET("CARPET", "171:4"),
    WHITE_CONCRETE_POWDER("CONCRETE_POWDER", "252", "252:0"),
    MAGENTA_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "160:2"),
    CYAN_CARPET("CARPET", "171:9"),
    BLACK_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "160:15"),
    YELLOW_CONCRETE_POWDER("CONCRETE_POWDER", "252:4"),
    LIGHT_GRAY_STAINED_GLASS("STAINED_GLASS", "95:8"),
    PINK_WOOL("WOOL", "35:6"),
    GRAY_WOOL("WOOL", "35:7"),
    LIGHT_GRAY_WOOL("WOOL", "35:8"),
    PURPLE_WOOL("WOOL", "35:10"),
    PURPLE_STAINED_GLASS("STAINED_GLASS", "95:10"),
    GRAY_CONCRETE("CONCRETE", "251:7"),
    LIGHT_BLUE_BANNER("BANNER", "425:3"),
    BROWN_CONCRETE_POWDER("CONCRETE_POWDER", "252:12"),
    PURPLE_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "160:10"),
    RED_CONCRETE("CONCRETE", "251:14"),
    GREEN_CONCRETE("CONCRETE", "251:13"),
    CYAN_STAINED_GLASS("STAINED_GLASS", "95:9"),
    LIGHT_GRAY_BED("BED", "355:8"),
    GREEN_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "160:13"),
    ORANGE_BED("BED", "355:1"),
    ORANGE_CONCRETE("CONCRETE", "251:1"),
    YELLOW_BANNER("BANNER", "425:4"),
    LIME_CONCRETE("CONCRETE", "251:5"),
    BROWN_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "160:12"),
    PURPLE_CONCRETE_POWDER("CONCRETE_POWDER", "252:10"),
    PURPLE_BANNER("BANNER", "425:10"),
    CYAN_CONCRETE_POWDER("CONCRETE_POWDER", "252:9"),
    YELLOW_BED("BED", "355:4"),
    BROWN_CONCRETE("CONCRETE", "251:12"),
    GRAY_BANNER("BANNER", "425:7"),
    LIME_CARPET("CARPET", "171:5"),
    CYAN_CONCRETE("CONCRETE", "251:9"),
    CYAN_BANNER("BANNER", "425:9"),
    RED_CONCRETE_POWDER("CONCRETE_POWDER", "252:14"),
    MAGENTA_CONCRETE("CONCRETE", "251:2"),
    WHITE_CONCRETE("CONCRETE", "251", "251:0"),
    MAGENTA_BED("BED", "355:2"),
    RED_STAINED_GLASS_PANE("STAINED_GLASS_PANE", "160:14"),
    BLUE_CARPET("CARPET", "171:11"),
    YELLOW_WOOL("WOOL", "35:4");

    private final String materialName;

    private short subId;

    Material material;

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
        int index = key.indexOf(':');
        if (index != -1) {
            ReMaterial fullValue = Data.BY_ID.get(key);
            if (fullValue != null) return fullValue.item();
            key = key.substring(0, index);
        }

        ReMaterial value = Data.BY_ID.get(key);
        if (value != null) return value.item();
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
        private static final Map<String, ReMaterial> BY_ID = new HashMap<>(591); // material-old.yml -> length
    }
}
