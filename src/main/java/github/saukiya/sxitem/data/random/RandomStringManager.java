package github.saukiya.sxitem.data.random;

import github.saukiya.sxitem.SXItem;
import net.minecraft.util.Tuple;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

/**
 * @author Saukiya
 */
public class RandomStringManager {

    private final File file = new File(SXItem.getInst().getDataFolder(), "RandomString");

    Map<String, List<Tuple<Double, String>>> randomMap = new HashMap();

    //TODO 多数随机由ItemManager完成，RandomManager只提供相应Key的返回值,并且不会进行自身迭代（如果迭代，则大概率会冲突？）

    public RandomStringManager() {
        loadData();
    }

    public static String loadDataString(Object obj) {
//        if (obj == null) return "";
        if (obj instanceof String) {
            return obj.toString().replace("\n", "/n");
        }
        if (obj instanceof List) {
            return String.join("/n", (List) obj);
        }
        return "无法识别";
    }

    public void loadData() {
        randomMap.clear();
        if (!file.exists() || Objects.requireNonNull(file.listFiles()).length == 0) {
            SXItem.getInst().saveResource("RandomString/DefaultRandom.yml", true);
            SXItem.getInst().saveResource("RandomString/10Level/Random.yml", true);
        }
        loadRandom(file);
        SXItem.getInst().getLogger().info("Loaded " + randomMap.size() + " RandomString");
    }

    /**
     * 遍历读取随机字符串数据
     *
     * @param files File
     */
    private void loadRandom(File files) {
        for (File file : Objects.requireNonNull(files.listFiles())) {
            if (file.isDirectory()) {
                loadRandom(file);
            } else {
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                for (String key : yml.getKeys(false)) {
                    if (randomMap.containsKey(key)) {
                        SXItem.getInst().getLogger().info("字符组名重复: " + file.getName().replace("plugins" + File.separator + SXItem.getInst().getName() + File.separator, "") + File.separator + key + " !");
                        continue;
                    }
                    Object obj = yml.get(key);
                    //单行 key - v
                    if (obj instanceof String) {
                        randomMap.put(key, Collections.singletonList(new Tuple<>(1D, obj.toString())));
                    }
                    // 多行 key - vList
                    else if (obj instanceof List) {
                        List<Tuple<Double, String>> list = new ArrayList<>();
                        Object unitObj = ((List<?>) obj).get(0);
                        if (unitObj instanceof Map) {
                            List<Map> listMap = (List<Map>) obj;
                            for (Map map : listMap) {
                                list.add(new Tuple<>(Double.valueOf(map.get("rate").toString()), loadDataString(map.get("string"))));
                            }
                        } else {// Deprecated
                            // TODO 标记可转化文本并备份
                            Map<String, Integer> tempMap = new HashMap<>();
                            String value;
                            for (Object objs : (List) obj) {
                                value = loadDataString(objs);
                                tempMap.put(value, tempMap.getOrDefault(value, 0) + 1);
                            }
                            for (Map.Entry<String, Integer> entry : tempMap.entrySet()) {
                                list.add(new Tuple<>(Double.valueOf(entry.getValue()), entry.getKey()));
                            }
                        }

                        list.sort(Comparator.comparing(Tuple::a));
                        randomMap.put(key, list);
                    }
                }
            }
        }
    }


    private void random(ItemStack item) {

    }

    public class RandomData extends HashMap<String, Object> {

        public <V> V get(Class<V> c, String str) {
            return (V) super.get(str);
        }
    }

//    /**
//     * 注册物品生成器
//     *
//     * @param generator ItemGenerator
//     */
//    public static void registerGenerator(IGenerator generator) {
//        if (generator.getType() == null || generators.stream().anyMatch((ig) -> ig.getType().equals(generator.getType()))) {
//            SXItem.getInst().getLogger().warning("ItemGenerator >>  [" + generator.getClass().getSimpleName() + "] Type Error!");
//            return;
//        }
//        generators.add(generator);
//        SXItem.getInst().getLogger().info("ItemGenerator >> Register [" + generator.getClass().getSimpleName() + "] To Type " + generator.getType() + " !");
//    }
}