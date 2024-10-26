package github.saukiya.sxitem.data.random;

import github.saukiya.sxitem.data.expression.ExpressionManager;
import github.saukiya.sxitem.data.expression.IExpression;
import github.saukiya.sxitem.data.random.nodes.MultipleNode;
import github.saukiya.sxitem.data.random.nodes.SingletonNode;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.*;

/**
 * RandomManager
 */
public class RandomManager {

    private final JavaPlugin plugin;

    private final File rootDirectory;

    @Getter
    private final Map<String, INode> map = new HashMap<>();

    public RandomManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.rootDirectory = new File(plugin.getDataFolder(), "RandomString");
        loadData();
    }

    /**
     * 加载数据
     */
    public void loadData() {
        map.clear();
        if (!rootDirectory.exists()) {
            plugin.getLogger().warning("Directory is not exists: " + rootDirectory.getName());
            return;
        }
        loadRandomFile(rootDirectory);
        plugin.getLogger().info("Loaded " + map.size() + " RandomString");
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
                        plugin.getLogger().info("Random Convert: " + file.getName());
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
            if (obj instanceof List) {
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
                    tempMap.forEach((v, size) -> singletonNode.add(size.doubleValue(), loadDataString(v)));
                    if (convert) {
                        List<Map<Integer, Object>> list = new ArrayList<>();
                        tempMap.forEach((o, size) -> list.add(Collections.singletonMap(size, o)));
                        config.set(entry.getKey(), list);
                    }
                }
                if (!singletonNode.isEmpty()) inputMap.put(entry.getKey(), singletonNode);
            } else {
                inputMap.put(entry.getKey(), new SingletonNode(loadDataString(obj)));
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

    @Deprecated
    public static void register(IExpression random, char... types) {
        ExpressionManager.register(random, types);
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