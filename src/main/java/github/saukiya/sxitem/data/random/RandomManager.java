package github.saukiya.sxitem.data.random;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.nodes.MultipleNode;
import github.saukiya.sxitem.data.random.nodes.SingletonNode;
import github.saukiya.sxitem.data.random.randoms.*;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Saukiya
 */
public class RandomManager {

    private final File file = new File(SXItem.getInst().getDataFolder(), "RandomString");

    @Getter
    Map<String, INode> map = new HashMap();

    //随机处理
    @Getter
    Map<Character, IRandom> randoms = new HashMap<>();

    public RandomManager() {
        loadData();
        randoms.put('c', new CalculatorRandom());
        randoms.put('l', new LockStringRandom());
        randoms.put('s', new StringRandom());
        randoms.put('t', new TimeRandom());
        randoms.put('d', new DoubleRandom());
        randoms.put('i', new IntRandom());
        randoms.put('r', randoms.get('i'));
    }

    /**
     * 加载数据
     */
    public void loadData() {
        map.clear();
        if (!file.exists() || file.listFiles().length == 0) {
            SXItem.getInst().saveResource("RandomString/NewRandom.yml", true);
            SXItem.getInst().saveResource("RandomString/DefaultRandom.yml", true);
            SXItem.getInst().saveResource("RandomString/10Level/Random.yml", true);
        }
        loadRandomFile(file);
        SXItem.getInst().getLogger().info("Loaded " + map.size() + " RandomString");
    }

    /**
     * 加载数据-遍历文件读取yaml
     *
     * @param files File
     */
    private void loadRandomFile(File files) {
        for (File file : files.listFiles()) {
            if (file.getName().startsWith("NoLoad")) continue;
            if (file.isDirectory()) {
                loadRandomFile(file);
            } else {
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                loadRandom(map, yml);
                /**
                 * TODO 旧版转化
                 *
                 * 流程:
                 * 1.将原来的file复制到 file.path/NoLoad_file.name 做备份
                 * 2.修改yml并且save
                 */
//                if (loadRandom(map, yml)) {
//                    void...
//                }
            }
        }
    }

    /**
     * 加载random数据到map中
     *
     * @param inputMap 需要导入的map
     * @param config   读取的config
     * @return boolean 当返回true代表有过时的写法
     */
    public boolean loadRandom(@Nonnull Map<String, INode> inputMap, @Nonnull ConfigurationSection config) {
        boolean deprecated = false;
        for (Map.Entry<String, Object> entry : config.getValues(false).entrySet()) {
            if (entry.getKey().startsWith("NoLoad")) continue;
            Object obj = entry.getValue();
            if (obj instanceof String) {
                inputMap.put(entry.getKey(), new SingletonNode(obj.toString()));
            } else if (obj instanceof List) {
                MultipleNode singletonNode = new MultipleNode();
                if (((List) obj).get(0) instanceof Map) {
                    for (Map value : (List<Map>) obj) {
                        value.forEach((k, v) -> singletonNode.add(Double.valueOf(k.toString()), loadDataString(v)));
                    }
                } else {
                    if (config instanceof YamlConfiguration) deprecated = true;
                    Map<Object, Integer> tempMap = new HashMap<>();
                    for (Object value : (List) obj) {
                        tempMap.put(value, tempMap.getOrDefault(value, 0) + 1);
                    }
                    tempMap.forEach((key1, value) -> singletonNode.add(value.doubleValue(), loadDataString(key1)));
                }
                if (!singletonNode.isEmpty()) inputMap.put(entry.getKey(), singletonNode);
            }
        }
        return deprecated;
    }

    public String random(String key) {
        return random(map, key);
    }

    public static String random(Map<String, INode> map, String key) {
        if (map == null) return null;
        INode node = map.get(key);
        if (node != null) return node.get();
        return null;
    }

    /**
     * 加载数据-读取V值
     *
     * @param value yaml参数
     * @return String
     */
    public static String loadDataString(Object value) {
        if (value == null) return null;
        if (value instanceof List) value = String.join("\n", (List) value);
        return value.toString().replace("/n", "\n").replace("\\n", "\n");
    }
}