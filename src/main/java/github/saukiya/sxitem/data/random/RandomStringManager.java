package github.saukiya.sxitem.data.random;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.sub.*;
import github.saukiya.sxitem.util.Tuple;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

/**
 * @author Saukiya
 */
public class RandomStringManager {

    private final File file = new File(SXItem.getInst().getDataFolder(), "RandomString");

    //公共变量
    Map<String, List<Tuple<Double, String>>> globalMap = new HashMap();

    //局部变量
    Map<String, Map<String, List<Tuple<Double, String>>>> localMap = new HashMap<>();

    //随机处理
    Map<Character, IRandom> randoms = new HashMap<>();

    //TODO 自动转化
    boolean deprecated = false;

    public RandomStringManager() {
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
        globalMap.clear();
        localMap.clear();
        if (!file.exists() || file.listFiles().length == 0) {
            SXItem.getInst().saveResource("RandomString/NewRandom.yml", true);
            SXItem.getInst().saveResource("RandomString/DefaultRandom.yml", true);
            SXItem.getInst().saveResource("RandomString/10Level/Random.yml", true);
        }
        loadRandomFile(file);
        SXItem.getInst().getLogger().info("Loaded " + globalMap.size() + " RandomString");
    }

    /**
     * 加载数据-遍历文件读取yaml
     *
     * @param files File
     */
    private void loadRandomFile(File files) {
        for (File file : files.listFiles()) {
            if (file.isDirectory()) {
                loadRandomFile(file);
            } else {
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                loadRandom(globalMap, yml);
            }
        }
    }

    /**
     * 加载局部数据
     *
     * @param key    关联key值
     * @param config config
     */
    public void loadRandomLocal(String key, ConfigurationSection config) {
        loadRandom(localMap.computeIfAbsent(key, k -> new HashMap<>()), config);
    }

    private void loadRandom(Map<String, List<Tuple<Double, String>>> inputMap, ConfigurationSection config) {
        for (String key : config.getKeys(false)) {
            if (inputMap.containsKey(key)) {
                SXItem.getInst().getLogger().info("Random Key Repeat: " + key + " !");
                continue;
            }
            Object obj = config.get(key);
            //单行 key - v
            if (obj instanceof String) {
                inputMap.put(key, Collections.singletonList(new Tuple<>(1D, obj.toString())));
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
                    deprecated = true;
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

                double value = 1;
                double temp;
                double sum = list.stream().mapToDouble(Tuple::a).sum();
                for (int i = list.size() - 1; i >= 0; i--) {
                    Tuple<Double, String> tuple = list.get(i);
                    temp = tuple.a() / sum;
                    tuple.a(value);
                    value -= temp;
                }
                inputMap.put(key, list);
            }
        }
    }

    /**
     * 加载数据-读取V值
     *
     * @param value
     * @return
     */
    private String loadDataString(Object value) {
        if (value instanceof String) {
            return value.toString().replace("\n", "/n");
        }
        if (value instanceof List) {
            return String.join("/n", (List) value);
        }
        return "N/A";
    }
}