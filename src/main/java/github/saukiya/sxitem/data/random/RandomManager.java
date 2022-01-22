package github.saukiya.sxitem.data.random;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.nodes.MultipleNode;
import github.saukiya.sxitem.data.random.nodes.SingletonNode;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.*;

/**
 * @author Saukiya
 */
public class RandomManager {

    protected static final Map<Character, IRandom> RANDOMS = new HashMap<>();

    private final File file = new File(SXItem.getInst().getDataFolder(), "RandomString");

    @Getter
    Map<String, INode> map = new HashMap();

    public RandomManager() {
        loadData();
    }

    /**
     * 加载数据
     */
    public void loadData() {
        map.clear();
        if (!file.exists() || file.listFiles().length == 0) {
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
    @SneakyThrows
    private void loadRandomFile(File files) {
        for (File file : files.listFiles()) {
            if (file.getName().startsWith("NoLoad")) continue;
            if (file.isDirectory()) {
                loadRandomFile(file);
            } else {
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                if (loadRandom(map, yml)) {
                    if (file.renameTo(new File(file.getParentFile(), "NoLoad_" + file.getName()))) {
                        yml.save(file);
                        SXItem.getInst().getLogger().info("Random Convert: " + file.getName());
                    }
                }
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
        boolean convertParent = false;
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
                    Map<Object, Integer> tempMap = new HashMap<>();
                    boolean convert = false;
                    for (Object value : (List) obj) {
                        if (tempMap.put(value, tempMap.getOrDefault(value, 0) + 1) != null)
                            convertParent = convert = true;
                    }
                    tempMap.forEach((o, size) -> singletonNode.add(size.doubleValue(), loadDataString(o)));
                    if (convert) {
                        List<Map<Integer, Object>> list = new ArrayList<>();
                        tempMap.forEach((o, size) -> list.add(Collections.singletonMap(size, o)));
                        config.set(entry.getKey(), list);
                    }
                }
                if (!singletonNode.isEmpty()) inputMap.put(entry.getKey(), singletonNode);
            }
        }
        return convertParent;
    }

    public String random(String key) {
        return random(key, map);
    }

    public static String random(String key, Map<String, INode> map) {
        INode node;
        if (map == null || (node = map.get(key)) == null) return null;
        return node.get();
    }

    /**
     * 注册新的随机类型
     *
     * @param random 随机处理
     * @param types  类型
     */
    public static void register(IRandom random, char... types) {
        for (char type : types) {
            RANDOMS.put(type, random);
        }
    }

    /**
     * 获取随机类型
     *
     * @param type 类型
     * @return 随机处理
     */
    protected static IRandom getRandom(char type) {
        return RANDOMS.get(type);
    }

    /**
     * 加载数据-读取V值
     *
     * @param value yaml参数
     * @return String
     */
    protected static String loadDataString(Object value) {
        if (value == null) return null;
        if (value instanceof List) value = String.join("\n", (List) value);
        return value.toString().replace("/n", "\n").replace("\\n", "\n");
    }
}