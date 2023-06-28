package github.saukiya.sxitem.data.random;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.helper.PlaceholderHelper;
import github.saukiya.sxitem.util.Config;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.script.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ScriptManager {

    private final JavaPlugin plugin;

    private final String[] defaultFile;

    private final ConcurrentHashMap<String, CompiledScript> compiledScripts = new ConcurrentHashMap<>();

    private Compilable compilableEngine;

    private Invocable invocable;

    public ScriptManager(JavaPlugin plugin, String... defaultFile) {
        this.plugin = plugin;
        this.defaultFile = defaultFile;
        reload();
    }

    /**
     * 重新加载
     */
    public void reload() {
        initEngine();
        File scriptFiles = new File(plugin.getDataFolder(), "Scripts");
        if (!scriptFiles.exists() || scriptFiles.listFiles().length == 0) {
            Arrays.stream(defaultFile).forEach(fileName -> plugin.saveResource(fileName, true));
        }
        try {
            loadScriptFile(scriptFiles);
        } catch (IOException | ScriptException e) {
            plugin.getLogger().warning("Load scripts error, Please see reported as follows");
            e.printStackTrace();
            compiledScripts.clear();
            return;
        }
        plugin.getLogger().info("Loaded " + compiledScripts.size() + " Scripts");
    }

    /**
     * 初始化引擎
     */
    @SneakyThrows
    private void initEngine() {
        // TODO 遍历可选引擎
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");

        // class路径在jdk8中不一致
        Class<?> clazz = Class.forName(System.getProperty("java.class.version").startsWith("52") ?
                "jdk.internal.dynalink.beans.StaticClass" :
                "jdk.dynalink.beans.StaticClass");
        Method method = clazz.getMethod("forClass", Class.class);
        engine.put("Bukkit", method.invoke(null, Bukkit.class));
        engine.put("SXItem", method.invoke(null, SXItem.class));
        engine.put("Arrays", method.invoke(null, Arrays.class));
        engine.put("Utils", method.invoke(null, Utils.class));
        compilableEngine = (Compilable) engine;
        invocable = (Invocable) engine;
        compiledScripts.clear();
        StringBuilder stringBuilder = new StringBuilder();
        ConfigurationSection scriptLib = Config.getConfig().getConfigurationSection("ScriptLib");
        if (scriptLib == null) return;
        for (String key : scriptLib.getKeys(false)) {
            stringBuilder.append("const ").append(key).append(" = ").append(scriptLib.getString(key)).append("\n");
        }
        try {
            compilableEngine.compile(stringBuilder.toString());
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 加载数据-遍历文件读取script
     *
     * @param files File
     */
    private void loadScriptFile(File files) throws IOException, ScriptException {
        for (File file : files.listFiles()) {
            if (file.getName().startsWith("NoLoad")) continue;
            if (file.isDirectory()) {
                loadScriptFile(file);
                // TODO 可选引擎后缀
            } else if (file.getName().endsWith(".js")) {
                InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
                CompiledScript compiled = compilableEngine.compile(inputStreamReader);
                compiledScripts.put(file.getName().replace(".js", ""), compiled);
                inputStreamReader.close();
            }
        }
    }

    /**
     * 执行script
     *
     * @param scriptName   脚本名
     * @param functionName 方法名
     * @param args         参数
     * @return script回参
     */
    public Object callFunction(String scriptName, String functionName, Object... args) throws Exception {
        CompiledScript compiled = compiledScripts.get(scriptName);
        if (compiled == null) {
            throw new Exception("Script not found: " + scriptName);
        }
        compiled.eval();
        return invocable.invokeFunction(functionName, args);
    }

    /**
     * script工具类
     */
    public static class Utils {

        public static <T> List<T> mutableList(T... args) {
            return Arrays.stream(args).collect(Collectors.toList());
        }

        public static String fromPlaceholderAPI(Player player, String str) {
            return PlaceholderHelper.setPlaceholders(player, str);
        }

        public static String asColor(String string) {
            return ChatColor.translateAlternateColorCodes('&', string);
        }

        public static int randomInt(Integer first, Integer last) {
            return SXItem.getRandom().nextInt(1 + last - first) + first;
        }

        public static double randomDouble(double min, double max) {
            double range = (max - min) * Math.random();
            return min + range;
        }
    }
}
