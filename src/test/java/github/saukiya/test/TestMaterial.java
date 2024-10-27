package github.saukiya.test;

import github.saukiya.tools.base.Tuple;
import lombok.SneakyThrows;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.openjdk.jmh.util.FileUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static github.saukiya.test.Test.loadYmlTest;
import static github.saukiya.test.Test.testResourcePath;

public class TestMaterial {
    @Deprecated
    static final List<String> COLOR_OLD = Arrays.asList("WHITE", "ORANGE", "MAGENTA", "SILVER", "YELLOW", "LIME", "PINK", "GRAY", "LIGHT_GRAY", "CYAN", "PURPLE", "BLUE", "BROWN", "GREEN", "RED", "BLACK");

    static final List<String> COLOR_NEW = Arrays.asList("WHITE", "ORANGE", "MAGENTA", "LIGHT_BLUE", "YELLOW", "LIME", "PINK", "GRAY", "LIGHT_GRAY", "CYAN", "PURPLE", "BLUE", "BROWN", "GREEN", "RED", "BLACK");

    static final List<String> COLOR_MATERIAL = Arrays.asList("WOOL", "BED", "STAINED_GLASS", "STAINED_GLASS_PANE", "CARPET", "CONCRETE", "CONCRETE_POWDER", "BANNER", "STAINED_HARDENED_CLAY");

    static final Map<String, List<String>> CONVERT_MAP = new HashMap<>();

    public static List<String> materialConvert(String material) {
        return CONVERT_MAP.get(material);
    }

    public static String materialRevert(String material) {
        for (Map.Entry<String, List<String>> entry : CONVERT_MAP.entrySet()) {
            if (entry.getValue().contains(material)) {
                return entry.getKey();
            }
        }
        return material;
    }

    public static void main(String[] args) {
        readMaterialOld();
//        readMaterialNew();
        compareMaterial();
    }

    @SneakyThrows
    public static void compareMaterial() {
        YamlConfiguration yaml_old = loadYmlTest("material-old.yml");
        YamlConfiguration yaml_new = loadYmlTest("material-new.yml");
        YamlConfiguration yaml_input = new YamlConfiguration();
        YamlConfiguration yaml_error = new YamlConfiguration();
        Map<String, Tuple<String, List<String>>> map_enum = new LinkedHashMap<>(); // new old ids
        Map<String, List<Tuple<String, String>>> map_markdown = new LinkedHashMap<>(); // old <id, new>
        for (Map.Entry<String, Object> entry : yaml_old.getValues(false).entrySet()) {
            String key = entry.getKey();
            String newKey = key;
            String value = (String) entry.getValue();
            boolean priority = true;
            // 匹配新版材质
            // 如果两边材质名不同
            if (!yaml_new.contains(newKey)) {
                priority = false;
                // 低版本 从 高版本 的过时材质中遍历
                val legacyName = "LEGACY_" + newKey;
                if (!yaml_new.contains(legacyName)) {
                    System.out.println("ERROR:" + legacyName + " >> " + key + " >> " + value);
                    yaml_error.set(key + "-" + legacyName, value);
                    continue;
                }
                newKey = yaml_new.getString(legacyName);
                System.out.println("LEGACY: " + newKey + " >> " + key + " >> " + value);
            }

            // 还原material
            val finalKey = materialRevert(key);

            val enumlist = map_enum.computeIfAbsent(newKey, k -> Tuple.of(finalKey, new ArrayList<>())).b();
            val mardownList = map_markdown.computeIfAbsent(finalKey, k -> new ArrayList<>());
            if (priority) {
                enumlist.addFirst(value);
                map_enum.put(newKey, Tuple.of(finalKey, enumlist));
            } else {
                enumlist.addLast(value);
            }
            mardownList.add(Tuple.of(value, newKey));
            // 添加
            if (value.endsWith(":0")) {
                System.out.println("DEFAULT: " + newKey + " > " + key + " > " + value.substring(0, value.length() - 2));
                enumlist.addFirst(value.substring(0, value.length() - 2));
                mardownList.addFirst(Tuple.of(value.substring(0, value.length() - 2), newKey));
            }

            value = yaml_input.contains(newKey) ? yaml_input.getString(newKey) + ',' + value : value;
            yaml_input.set(newKey, value);

        }
        yaml_input.save(new File(testResourcePath, "material-input.yml"));
        yaml_error.save(new File(testResourcePath, "material-error.yml"));
        val enumList = map_enum.entrySet().stream().map(entry -> "\t" + entry.getKey() + "(" + (entry.getKey().equals(entry.getValue().a()) ? null : "\"" + entry.getValue().a() + "\"") + ", \"" + String.join("\", \"", entry.getValue().b()) + "\"),").collect(Collectors.toList());
        FileUtils.writeLines(new File(testResourcePath, "material-enum.txt"), enumList);
        val markdownList = map_markdown.entrySet().stream().flatMap(entry -> {
            val first = Stream.of(" - `" + entry.getValue().getFirst().a() + "`  " + entry.getValue().getFirst().b() + (entry.getValue().getFirst().b().equals(entry.getKey()) ? "" : " `" + entry.getKey() + "`"));
            if (entry.getValue().size() == 1) return first;
            return Stream.concat(first, IntStream.range(1, entry.getValue().size()).mapToObj(i -> "   - `" + entry.getValue().get(i).a() + "`  " + entry.getValue().get(i).b()));
        }).collect(Collectors.toList());
        markdownList.addFirst("兼容数字ID列表");
        FileUtils.writeLines(new File(testResourcePath, "material-enum.md"), markdownList);

        System.out.println("Material-Enum: " + map_enum.values().stream().mapToInt(tuple -> tuple.b().size()).sum());
    }

//    @SneakyThrows
//    public static void readMaterialNew() {
//        // 1.21.1
//        YamlConfiguration yaml_new = new YamlConfiguration();
//        List<String> list = new ArrayList<>();
//        for (Material value : Material.values()) {
//            if (value.isLegacy()) {
//                Material material = null;
////                material = org.bukkit.craftbukkit.v1_13_R2.util.CraftLegacy.fromLegacy(value); // 1.13.2
////                material = org.bukkit.craftbukkit.v1_21_R1.legacy.CraftLegacy.fromLegacy(value); // 1.21.1
//
//                yaml_new.set(value.name(), material.toString());
//            } else {
//                list.add(value.name());
//            }
//        }
//        list.forEach(x -> yaml_new.set(x, ""));
//
//        yaml_new.save(new File(testResourcePath, "material-new.yml"));
//    }

    @SneakyThrows
    public static void readMaterialOld() {
        // 1.12.2
        YamlConfiguration yaml_old = new YamlConfiguration();// EnumColor 1.12 与 1.21 有一点不同, SILVER -> LIGHT_GRAY

        Map<String, String> colorMaterials = new HashMap<>();
        for (Material value : Material.values()) {
            List<String> convert = materialConvert(value.name());
            if (convert == null) {
                yaml_old.set(value.name(), String.valueOf(value.getId()));
                continue;
            }
            for (int i = 0, length = convert.size(); i < length; i++) {
                yaml_old.set(convert.get(i), value.getId() + ":" + i);
            }
        }

        yaml_old.save(new File(testResourcePath, "material-old.yml"));
    }

    static {
        CONVERT_MAP.put("STONE", Arrays.asList("STONE", "GRANITE", "POLISHED_GRANITE", "DIORITE", "POLISHED_DIORITE", "ANDESITE", "POLISHED_ANDESITE")); // 7
        CONVERT_MAP.put("STONE_SLAB", Arrays.asList("SMOOTH_STONE_SLAB", "SANDSTONE_SLAB", "COBBLESTONE_SLAB", "BRICK_SLAB", "STONE_BRICK_SLAB", "NETHER_BRICK_SLAB", "QUARTZ_SLAB"));
        CONVERT_MAP.put("STONE_BRICK", Arrays.asList("STONE_BRICKS", "MOSSY_STONE_BRICKS", "CRACKED_STONE_BRICKS", "CHISELED_STONE_BRICKS")); // 4
        CONVERT_MAP.put("SAND", Arrays.asList("SAND", "RED_SAND")); // 2
        CONVERT_MAP.put("SPONGE", Arrays.asList("SPONGE", "WET_SPONGE")); // 2

        CONVERT_MAP.put("SANDSTONE", Arrays.asList("SANDSTONE", "CHISELED_SANDSTONE", "CUT_SANDSTONE")); // 3
        CONVERT_MAP.put("DIRT", Arrays.asList("DIRT", "COARSE_DIRT", "PODZOL")); // 3
        CONVERT_MAP.put("QUARTZ_BLOCK", Arrays.asList("QUARTZ_BLOCK", "CHISELED_QUARTZ_BLOCK", "QUARTZ_PILLAR")); // 3
        CONVERT_MAP.put("PRISMARINE", Arrays.asList("PRISMARINE", "PRISMARINE_BRICKS", "DARK_PRISMARINE")); // 3
        CONVERT_MAP.put("RED_SANDSTONE", Arrays.asList("RED_SANDSTONE", "CHISELED_RED_SANDSTONE", "CUT_RED_SANDSTONE")); // 3

        CONVERT_MAP.put("PLANKS", Arrays.asList("OAK_PLANKS", "SPRUCE_PLANKS", "BIRCH_PLANKS", "JUNGLE_PLANKS", "ACACIA_PLANKS", "DARK_OAK_PLANKS")); // 6
        CONVERT_MAP.put("PLANKS_SLAB", Arrays.asList("OAK_SLAB", "SPRUCE_SLAB", "BIRCH_SLAB", "JUNGLE_SLAB", "ACACIA_SLAB", "DARK_OAK_SLAB")); // 6
        CONVERT_MAP.put("LOG", Arrays.asList("OAK_LOG", "SPRUCE_LOG", "BIRCH_LOG", "JUNGLE_LOG")); // 4
        CONVERT_MAP.put("LOG_2", Arrays.asList("ACACIA_LOG", "DARK_OAK_LOG")); // 2

        CONVERT_MAP.put("SAPLING", Arrays.asList("OAK_SAPLING", "SPRUCE_SAPLING", "BIRCH_SAPLING", "JUNGLE_SAPLING", "ACACIA_SAPLING", "DARK_OAK_SAPLING")); // 6
        CONVERT_MAP.put("LEAVES", Arrays.asList("OAK_LEAVES", "SPRUCE_LEAVES", "BIRCH_LEAVES", "JUNGLE_LEAVES")); // 4
        CONVERT_MAP.put("LEAVES_2", Arrays.asList("ACACIA_LEAVES", "DARK_OAK_LEAVES")); // 2
        CONVERT_MAP.put("COAL", Arrays.asList("COAL", "CHARCOAL")); // 2

        CONVERT_MAP.put("TALLGRASS", Arrays.asList("SHORT_GRASS", "FERN")); // 2
        CONVERT_MAP.put("MONSTER_EGG", Arrays.asList("INFESTED_STONE", "INFESTED_COBBLESTONE", "INFESTED_STONE_BRICKS", "INFESTED_MOSSY_STONE_BRICKS", "INFESTED_CRACKED_STONE_BRICKS", "INFESTED_CHISELED_STONE_BRICKS")); // 6
        CONVERT_MAP.put("RED_FLOWER", Arrays.asList("POPPY", "BLUE_ORCHID", "ALLIUM", "AZURE_BLUET", "RED_TULIP", "ORANGE_TULIP", "WHITE_TULIP", "PINK_TULIP", "OXEYE_DAISY")); // 9
        CONVERT_MAP.put("DOUBLE_PLANT", Arrays.asList("SUNFLOWER", "LILAC", "TALL_GRASS", "LARGE_FERN", "ROSE_BUSH", "PEONY")); // 6

        CONVERT_MAP.put("SKULL", Arrays.asList("SKELETON_SKULL", "WITHER_SKELETON_SKULL", "ZOMBIE_HEAD", "PLAYER_HEAD", "CREEPER_HEAD", "DRAGON_HEAD")); // 6
        CONVERT_MAP.put("COBBLESTONE_WALL", Arrays.asList("COBBLESTONE_WALL", "MOSSY_COBBLESTONE_WALL")); // 2
        CONVERT_MAP.put("ANVIL", Arrays.asList("ANVIL", "CHIPPED_ANVIL", "DAMAGED_ANVIL")); // 3

        CONVERT_MAP.put("DYE", Arrays.asList("INK_SAC", "RED_DYE", "GREEN_DYE", "COCOA_BEANS", "LAPIS_LAZULI", "PURPLE_DYE", "CYAN_DYE", "LIGHT_GRAY_DYE", "GRAY_DYE", "PINK_DYE", "LIME_DYE", "YELLOW_DYE", "LIGHT_BLUE_DYE", "MAGENTA_DYE", "ORANGE_DYE", "BONE_MEAL")); // 16
//        CONVERT_MAP.put("SPAWN_EGG", Arrays.asList("", "")); // ??? LONG 120 / 

        CONVERT_MAP.put("GOLDEN_APPLE", Arrays.asList("GOLDEN_APPLE", "ENCHANTED_GOLDEN_APPLE")); // 2
        CONVERT_MAP.put("FISH", Arrays.asList("COD", "SALMON", "TROPICAL_FISH", "PUFFERFISH")); // 4
        CONVERT_MAP.put("COOKED_FISH", Arrays.asList("COOKED_COD", "COOKED_SALMON")); // 2

        for (String material : COLOR_MATERIAL) {
            CONVERT_MAP.put(material, COLOR_NEW.stream().map(color -> color + "_" + material).collect(Collectors.toList()));
        }
    }
}
