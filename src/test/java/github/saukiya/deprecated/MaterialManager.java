package github.saukiya.deprecated;

import lombok.Getter;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 旧版材质管理器
 */
public class MaterialManager {
    @Getter
    private static final Map<String, Material> materialMap = new HashMap<>();

    /**
     * 读取Material数据
     */
    public static void loadMaterialData() {
//        materialMap.clear();
//        File file = new File(SXItem.getInst().getDataFolder(), "deprecated/Material.yml");
//        if (!file.exists()) {
//            SXItem.getInst().saveResource("deprecated/Material.yml", true);
//        }
//        boolean change = false;
//        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
//        boolean methodUse = NMS.compareTo(1, 13, 1) >= 0;
//        for (Map.Entry<String, Object> entry : yaml.getValues(false).entrySet()) {
//            Material material = Material.getMaterial(entry.getKey());
//            if (material == null) {
//                try {
//                    if (methodUse) material = Material.getMaterial(entry.getKey(), true);
//                    if (material == null) {
//                        SXItem.getInst().getLogger().warning("Material.yml No Material - " + entry.getKey());
//                        continue;
//                    }
//                    change = true;
//                    SXItem.getInst().getLogger().config("Material.yml Change MaterialName - " + entry.getKey() + " To " + material.name());
//                    if (yaml.contains(material.name())) {
//                        yaml.set(material.name(), yaml.getString(material.name()) + "," + entry.getValue());
//                    } else {
//                        yaml.set(material.name(), entry.getValue());
//                    }
//                    yaml.set(entry.getKey(), null);
//                } catch (Exception ignored) {
//                    SXItem.getInst().getLogger().warning("Material.yml No Material - " + entry.getKey());
//                    continue;
//                }
//            }
//            for (String key : entry.getValue().toString().split(",")) {
//                if (!key.isEmpty()) {
//                    Object ret = materialMap.put(key, material);
//                    if (ret != null) {
//                        SXItem.getInst().getLogger().warning("Material.yml Repeat Key - " + key + " (" + ret + "/" + material + ")");
//                        materialMap.remove(key);
//                    }
//                }
//            }
//        }
//        if (change) yaml.save(file);
//        SXItem.getInst().getLogger().info("Loaded " + materialMap.size() + " Materials");
    }

    /**
     * 获取物品材质
     * <pre>
     * 可能参数: 267,
     * </pre>
     *
     * @param key 索引
     * @return 材质
     */
    public static Material getMaterial(String key) {
        Material material = materialMap.get(key);
        return material != null ? material : Material.getMaterial(key.replace(' ', '_').toUpperCase(Locale.ROOT));
    }

    public static Set<String> getMaterialString(Material value) {
        return materialMap.entrySet().stream().filter(e -> e.getValue().equals(value)).map(Map.Entry::getKey).collect(Collectors.toSet());
    }

}
