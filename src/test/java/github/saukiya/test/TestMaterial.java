package github.saukiya.test;

import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.openjdk.jmh.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static github.saukiya.test.Test.loadYmlTest;
import static github.saukiya.test.Test.testResourcePath;

/**
 * 材质转换
 */
public class TestMaterial {
//    @Deprecated
//    static final List<String> COLOR_OLD = Arrays.asList("WHITE", "ORANGE", "MAGENTA", "SILVER", "YELLOW", "LIME", "PINK", "GRAY", "LIGHT_GRAY", "CYAN", "PURPLE", "BLUE", "BROWN", "GREEN", "RED", "BLACK");

    static final List<String> COLOR_NEW = Arrays.asList("WHITE", "ORANGE", "MAGENTA", "LIGHT_BLUE", "YELLOW", "LIME", "PINK", "GRAY", "LIGHT_GRAY", "CYAN", "PURPLE", "BLUE", "BROWN", "GREEN", "RED", "BLACK");

    static final List<String> COLOR_MATERIAL = Arrays.asList("WOOL", "BED", "STAINED_GLASS", "STAINED_GLASS_PANE", "CARPET", "CONCRETE", "CONCRETE_POWDER", "BANNER", "STAINED_HARDENED_CLAY");

    static final Map<String, List<String>> CONVERT_MAP = new HashMap<>();

    public static void main(String[] args) throws Exception {
//        readMaterialOld();
//        readMaterialNew();
        compareMaterial();
    }

    /**
     * 比较新旧版材质并自动生成材质信息
     * <pre>
     * material_enum.java - 转换后生成的枚举文件
     * material_info.md - 转换后生成的材质说明
     * material_input.yml - 转换成功后导出的信息
     * material_error.yml - 转换失败后导出的信息
     *
     * material_old.yml - 调用的旧版材质信息
     * material_new.yml - 调用的新版材质信息
     * material_zh_cn.yml - 调用本地化材质信息 (通过TestServer和zh_cn.lang生成)
     * </pre>
     */
    @SneakyThrows
    public static void compareMaterial() {
        YamlConfiguration yaml_old = loadYmlTest("material_old.yml");
        YamlConfiguration yaml_new = loadYmlTest("material_new.yml");
        YamlConfiguration yaml_trans = loadYmlTest("material_zh_cn.yml");
        YamlConfiguration yaml_input = new YamlConfiguration();
        YamlConfiguration yaml_error = new YamlConfiguration();
        Map<String, Triple<String, String, List<String>>> map_enum = new LinkedHashMap<>(); // new <old, translation, ids>
        Map<String, List<Triple<String, String, String>>> map_markdown = new LinkedHashMap<>(); // old <id, new, translation>
        for (Map.Entry<String, Object> entry : yaml_old.getValues(false).entrySet()) {
            val key = entry.getKey();
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
            }

            val translation = yaml_trans.getString(key, "NULL");

            // 还原material
            val finalKey = materialRevert(key);

            if (translation.equals("空气") && !newKey.equals("AIR")) {
                System.out.println("IGNORE: " + key + " >> " + newKey + " >> " + value);
            }
            if (newKey.equals("AIR") && !finalKey.equals("AIR")) {
                System.out.println("IGNORE: " + key + " >> " + newKey + " >> " + value);
                continue;
            }

            val enumlist = map_enum.computeIfAbsent(newKey, k -> Triple.of(finalKey, translation, new ArrayList<>())).getRight();
            val mardownList = map_markdown.computeIfAbsent(finalKey, k -> new ArrayList<>());
            if (priority) {
                enumlist.addFirst(value);
                map_enum.put(newKey, Triple.of(finalKey, translation, enumlist));
            } else {
                enumlist.addLast(value);
            }
            mardownList.add(Triple.of(value, newKey, translation));
            // 添加
            if (value.endsWith(":0")) {
                enumlist.addFirst(value.substring(0, value.length() - 2));
                mardownList.addFirst(Triple.of(value.substring(0, value.length() - 2), newKey, translation));
            }

            value = yaml_input.contains(newKey) ? yaml_input.getString(newKey) + ',' + value : value;
            yaml_input.set(newKey, value);

        }
        yaml_input.save(new File(testResourcePath, "material_input.yml"));
        yaml_error.save(new File(testResourcePath, "material_error.yml"));
        String enumList = map_enum.entrySet().stream().map(entry -> "/** " + entry.getValue().getMiddle() + " **/\n\t" + entry.getKey() + "(" + (entry.getKey().equals(entry.getValue().getLeft()) ? null : "\"" + entry.getValue().getLeft() + "\"") + ", \"" + String.join("\", \"", entry.getValue().getRight()) + "\")").collect(Collectors.joining(",\n\t"));
        enumList = "public enum ReMaterial {\n\t" + enumList + ";\n\n}";
        Files.write(Paths.get(testResourcePath.getPath(), "material_enum.java"), enumList.getBytes());
        val markdownList = map_markdown.entrySet().stream().flatMap(entry -> {
            String first = " - `" + entry.getValue().getFirst().getLeft() + "`  " + entry.getValue().getFirst().getMiddle() + (entry.getValue().getFirst().getMiddle().equals(entry.getKey()) ? "" : " `" + entry.getKey() + "`") + " - " + entry.getValue().getFirst().getRight();
            if (entry.getValue().size() == 1) return Stream.of(first);
            return Stream.concat(Stream.of(first), IntStream.range(1, entry.getValue().size()).mapToObj(i -> "   - `" + entry.getValue().get(i).getLeft() + "`  " + entry.getValue().get(i).getMiddle() + " - " + entry.getValue().get(i).getRight()));
        }).collect(Collectors.toList());
        markdownList.addFirst("## 兼容数字ID列表");
        FileUtils.writeLines(new File(testResourcePath, "material_info.md"), markdownList);

        System.out.println("Material-Enum: " + map_enum.values().stream().mapToInt(tuple -> tuple.getRight().size()).sum());
    }

    /**
     * 生成新版材质信息 material_new.yml
     */
    public static void readMaterialNew() throws IOException {
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
//        yaml_new.save(new File(testResourcePath, "material_new.yml"));
    }

    /**
     * 生成旧版材质信息 material_old.yml
     */
    public static void readMaterialOld() throws IOException {
        // 1.12.2
        YamlConfiguration yaml_old = new YamlConfiguration();// EnumColor 1.12 与 1.21 有一点不同, SILVER -> LIGHT_GRAY

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

        yaml_old.save(new File(testResourcePath, "material_old.yml"));
    }

    /**
     * 转换旧版材质
     *
     * @param material
     * @return
     */
    public static List<String> materialConvert(String material) {
        return CONVERT_MAP.get(material);
    }

    /**
     * 还原旧版材质
     *
     * @param material
     * @return
     */
    public static String materialRevert(String material) {
        for (Map.Entry<String, List<String>> entry : CONVERT_MAP.entrySet()) {
            if (entry.getValue().contains(material)) {
                return entry.getKey();
            }
        }
        return material;
    }

    // 旧版转换参数
    static {
        CONVERT_MAP.put("STONE", Arrays.asList("STONE", "GRANITE", "POLISHED_GRANITE", "DIORITE", "POLISHED_DIORITE", "ANDESITE", "POLISHED_ANDESITE")); // 7
        // STONE_SLAB
        CONVERT_MAP.put("STEP", Arrays.asList("SMOOTH_STONE_SLAB", "SANDSTONE_SLAB", "COBBLESTONE_SLAB", "BRICK_SLAB", "STONE_BRICK_SLAB", "NETHER_BRICK_SLAB", "QUARTZ_SLAB"));
        CONVERT_MAP.put("STONE_BRICK", Arrays.asList("STONE_BRICKS", "MOSSY_STONE_BRICKS", "CRACKED_STONE_BRICKS", "CHISELED_STONE_BRICKS")); // 4
        CONVERT_MAP.put("SAND", Arrays.asList("SAND", "RED_SAND")); // 2
        CONVERT_MAP.put("SPONGE", Arrays.asList("SPONGE", "WET_SPONGE")); // 2

        CONVERT_MAP.put("SANDSTONE", Arrays.asList("SANDSTONE", "CHISELED_SANDSTONE", "CUT_SANDSTONE")); // 3
        CONVERT_MAP.put("DIRT", Arrays.asList("DIRT", "COARSE_DIRT", "PODZOL")); // 3
        CONVERT_MAP.put("QUARTZ_BLOCK", Arrays.asList("QUARTZ_BLOCK", "CHISELED_QUARTZ_BLOCK", "QUARTZ_PILLAR")); // 3
        CONVERT_MAP.put("PRISMARINE", Arrays.asList("PRISMARINE", "PRISMARINE_BRICKS", "DARK_PRISMARINE")); // 3
        CONVERT_MAP.put("RED_SANDSTONE", Arrays.asList("RED_SANDSTONE", "CHISELED_RED_SANDSTONE", "CUT_RED_SANDSTONE")); // 3

        // PLANKS
        CONVERT_MAP.put("WOOD", Arrays.asList("OAK_PLANKS", "SPRUCE_PLANKS", "BIRCH_PLANKS", "JUNGLE_PLANKS", "ACACIA_PLANKS", "DARK_OAK_PLANKS")); // 6
        // PLANKS_SLAB
        CONVERT_MAP.put("WOOD_STEP", Arrays.asList("OAK_SLAB", "SPRUCE_SLAB", "BIRCH_SLAB", "JUNGLE_SLAB", "ACACIA_SLAB", "DARK_OAK_SLAB")); // 6
        CONVERT_MAP.put("LOG", Arrays.asList("OAK_LOG", "SPRUCE_LOG", "BIRCH_LOG", "JUNGLE_LOG")); // 4
        CONVERT_MAP.put("LOG_2", Arrays.asList("ACACIA_LOG", "DARK_OAK_LOG")); // 2

        CONVERT_MAP.put("SAPLING", Arrays.asList("OAK_SAPLING", "SPRUCE_SAPLING", "BIRCH_SAPLING", "JUNGLE_SAPLING", "ACACIA_SAPLING", "DARK_OAK_SAPLING")); // 6
        CONVERT_MAP.put("LEAVES", Arrays.asList("OAK_LEAVES", "SPRUCE_LEAVES", "BIRCH_LEAVES", "JUNGLE_LEAVES")); // 4
        CONVERT_MAP.put("LEAVES_2", Arrays.asList("ACACIA_LEAVES", "DARK_OAK_LEAVES")); // 2
        CONVERT_MAP.put("COAL", Arrays.asList("COAL", "CHARCOAL")); // 2

        // TALLGRASS
        CONVERT_MAP.put("LONG_GRASS", Arrays.asList("SHORT_GRASS", "FERN")); // 2
        CONVERT_MAP.put("MONSTER_EGG", Arrays.asList("INFESTED_STONE", "INFESTED_COBBLESTONE", "INFESTED_STONE_BRICKS", "INFESTED_MOSSY_STONE_BRICKS", "INFESTED_CRACKED_STONE_BRICKS", "INFESTED_CHISELED_STONE_BRICKS")); // 6
        // RED_FLOWER
        CONVERT_MAP.put("RED_ROSE", Arrays.asList("POPPY", "BLUE_ORCHID", "ALLIUM", "AZURE_BLUET", "RED_TULIP", "ORANGE_TULIP", "WHITE_TULIP", "PINK_TULIP", "OXEYE_DAISY")); // 9
        CONVERT_MAP.put("DOUBLE_PLANT", Arrays.asList("SUNFLOWER", "LILAC", "TALL_GRASS", "LARGE_FERN", "ROSE_BUSH", "PEONY")); // 6

        CONVERT_MAP.put("SKULL_ITEM", Arrays.asList("SKELETON_SKULL", "WITHER_SKELETON_SKULL", "ZOMBIE_HEAD", "PLAYER_HEAD", "CREEPER_HEAD", "DRAGON_HEAD")); // 6
        // COBBLESTONE_WALL
        CONVERT_MAP.put("COBBLE_WALL", Arrays.asList("COBBLESTONE_WALL", "MOSSY_COBBLESTONE_WALL")); // 2
        CONVERT_MAP.put("ANVIL", Arrays.asList("ANVIL", "CHIPPED_ANVIL", "DAMAGED_ANVIL")); // 3

        // DYE
        CONVERT_MAP.put("INK_SACK", Arrays.asList("INK_SAC", "RED_DYE", "GREEN_DYE", "COCOA_BEANS", "LAPIS_LAZULI", "PURPLE_DYE", "CYAN_DYE", "LIGHT_GRAY_DYE", "GRAY_DYE", "PINK_DYE", "LIME_DYE", "YELLOW_DYE", "LIGHT_BLUE_DYE", "MAGENTA_DYE", "ORANGE_DYE", "BONE_MEAL")); // 16
//        CONVERT_MAP.put("SPAWN_EGG", Arrays.asList("", "")); // ??? LONG 120 / 

        CONVERT_MAP.put("GOLDEN_APPLE", Arrays.asList("GOLDEN_APPLE", "ENCHANTED_GOLDEN_APPLE")); // 2
        // FISH
        CONVERT_MAP.put("RAW_FISH", Arrays.asList("COD", "SALMON", "TROPICAL_FISH", "PUFFERFISH")); // 4
        CONVERT_MAP.put("COOKED_FISH", Arrays.asList("COOKED_COD", "COOKED_SALMON")); // 2

        for (String material : COLOR_MATERIAL) {
            CONVERT_MAP.put(material, COLOR_NEW.stream().map(color -> color + "_" + material).collect(Collectors.toList()));
        }
        Collections.reverse(CONVERT_MAP.get("BANNER"));
    }
}
