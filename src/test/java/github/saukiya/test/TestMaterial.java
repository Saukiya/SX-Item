package github.saukiya.test;

import github.saukiya.tools.base.Tuple;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static github.saukiya.test.Test.loadYmlTest;
import static github.saukiya.test.Test.testResourcePath;

public class TestMaterial {
    // LIGHT_BLUE
    static final List<String> NEW_COLOR = Arrays.asList("WHITE", "ORANGE", "MAGENTA", "LIGHT_BLUE", "YELLOW", "LIME", "PINK", "GRAY", "LIGHT_GRAY", "CYAN", "PURPLE", "BLUE", "BROWN", "GREEN", "RED", "BLACK");

    static final List<String> OLD_COLOR = Arrays.asList("WHITE", "ORANGE", "MAGENTA", "SILVER", "YELLOW", "LIME", "PINK", "GRAY", "LIGHT_GRAY", "CYAN", "PURPLE", "BLUE", "BROWN", "GREEN", "RED", "BLACK");

    static final List<String> COLOR_MATERIAL = Arrays.asList("WOOL", "BED", "STAINED_GLASS", "STAINED_GLASS_PANE", "CARPET", "CONCRETE", "CONCRETE_POWDER", "BANNER");

    public static void main(String[] args) {
//        readMaterialOld();
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

            // 清掉key的颜色
            for (String material : COLOR_MATERIAL) {
                if (!key.endsWith(material) || NEW_COLOR.stream().noneMatch(key::startsWith)) continue;
                key = material;
                break;
            }

            val finalKey = key;

            val list = map_enum.computeIfAbsent(newKey, k -> Tuple.of(finalKey, new ArrayList<>())).b();
            if (priority) {
                list.addFirst(value);
                map_enum.put(newKey, Tuple.of(finalKey, list));
            } else {
                list.addLast(value);
            }
            // 添加
            if (value.endsWith(":0")) {
                System.out.println("DEFAULT: " + newKey + " > " + key + " > " + value.substring(0, value.length() - 2));
                list.addFirst(value.substring(0, value.length() - 2));
            }

            value = yaml_input.contains(newKey) ? yaml_input.getString(newKey) + ',' + value : value;
            yaml_input.set(newKey, value);

        }
        yaml_input.save(new File(testResourcePath, "material-input.yml"));
        yaml_error.save(new File(testResourcePath, "material-error.yml"));
        val enumList = map_enum.entrySet().stream().map(entry -> "\t" + entry.getKey() + "(" + (entry.getKey().equals(entry.getValue().a()) ? null : "\"" + entry.getValue().a() + "\"") + ", \"" + String.join("\", \"", entry.getValue().b()) + "\"),").collect(Collectors.toList());
        FileUtils.writeLines(new File(testResourcePath, "material-enum.txt"), enumList);
    }

    @SneakyThrows
    public static void readMaterialNew() {
        // 1.21.1
        YamlConfiguration yaml_new = new YamlConfiguration();
        List<String> list = new ArrayList<>();
        for (Material value : Material.values()) {
            if (value.isLegacy()) {
                Material material = null;
//                material = org.bukkit.craftbukkit.v1_13_R2.util.CraftLegacy.fromLegacy(value); // 1.13.2
                material = org.bukkit.craftbukkit.v1_21_R1.legacy.CraftLegacy.fromLegacy(value); // 1.21.1

                yaml_new.set(value.name(), material.toString());
            } else {
                list.add(value.name());
            }
        }
        list.forEach(x -> yaml_new.set(x, ""));

        yaml_new.save(new File(testResourcePath, "material-new.yml"));
    }

    @SneakyThrows
    public static void readMaterialOld() {
        // 1.12.2
        YamlConfiguration yaml_old = new YamlConfiguration();// EnumColor 1.12 与 1.21 有一点不同, SILVER -> LIGHT_GRAY

        Map<String, String> colorMaterials = new HashMap<>();
        for (Material value : Material.values()) {
            if (!COLOR_MATERIAL.contains(value.name())) {
                yaml_old.set(value.name(), String.valueOf(value.getId()));
                continue;
            }
            for (int i = 0; i < NEW_COLOR.size(); i++) {
                val color = NEW_COLOR.get(i);
                colorMaterials.put(color + "_" + value.name(), value.getId() + ":" + i);
            }
        }
        colorMaterials.forEach(yaml_old::set);

        yaml_old.save(new File(testResourcePath, "material-old.yml"));
    }
}
