package github.saukiya.sxitem.data;

import github.saukiya.sxitem.util.Config;
import github.saukiya.util.helper.PlaceholderHelper;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.script.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ScriptManager {

    private static final Random random = new Random();

    private final ConcurrentHashMap<String, CompiledScript> compiledScripts = new ConcurrentHashMap<>();

    private final JavaPlugin plugin;

    private final File rootDirectory;

    private final File globalFile;

    private Compilable compilableEngine;

    private Invocable invocable;

    @Getter
    private boolean enabled;

    public ScriptManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.rootDirectory = new File(plugin.getDataFolder(), "Scripts");
        this.globalFile = new File(rootDirectory, "Global.js");
        reload();
    }

    /**
     * 重新加载
     */
    public void reload() {
        try {
            if (!rootDirectory.exists()) {
                plugin.getLogger().warning("Directory is not exists: " + rootDirectory.getName());
                return;
            }
            initEngine();
            enabled = true;
            loadScriptFile(rootDirectory);
        } catch (Exception e) {
            if (e instanceof NullPointerException) {
                plugin.getLogger().info("Scripts Disabled");
            } else {
                plugin.getLogger().warning("Load scripts error: " + e.getMessage());
            }
            compiledScripts.clear();
            enabled = false;
            return;
        }
        plugin.getLogger().info("Loaded " + compiledScripts.size() + " Scripts");
    }

    /**
     * 初始化引擎
     */
    private void initEngine() throws Exception {
        // TODO 遍历可选引擎
        String engineName = Config.getConfig().getString(Config.SCRIPT_ENGINE, "js");
        if (engineName.isEmpty()) throw null;
        System.setProperty("nashorn.args", "--language=es6");
        ScriptEngine engine = new ScriptEngineManager().getEngineByName(engineName);
        if (engine == null) throw new ScriptException("No Find ScriptEngine: " + engineName);
        // class路径在jdk8中不一致
        Class<?> clazz = Class.forName(System.getProperty("java.class.version").startsWith("52") ?
                "jdk.internal.dynalink.beans.StaticClass" :
                "jdk.dynalink.beans.StaticClass");
        Method method = clazz.getMethod("forClass", Class.class);
        // 如果要脱离组织架构的话需要清除SXItem.class 懒了
        engine.put("Bukkit", method.invoke(null, Bukkit.class));
        engine.put(plugin.getName().replaceAll("[^a-zA-Z]", ""), method.invoke(null, plugin.getClass()));
        engine.put("Arrays", method.invoke(null, Arrays.class));
        engine.put("Utils", method.invoke(null, Utils.class));
        compilableEngine = (Compilable) engine;
        invocable = (Invocable) engine;
        compiledScripts.clear();
        // 这玩意最好隔离出来单独搞个Global.js
        InputStreamReader globalReader = new InputStreamReader(Files.newInputStream(globalFile.toPath()), StandardCharsets.UTF_8);
        compilableEngine.compile(globalReader);
        globalReader.close();
    }

    /**
     * 加载数据-遍历文件读取script
     *
     * @param files File
     */
    private void loadScriptFile(File files) throws IOException, ScriptException {
        if (!enabled) return;
        for (File file : files.listFiles()) {
            if (file.getName().startsWith("NoLoad") || file.equals(globalFile)) continue;
            if (file.isDirectory()) {
                loadScriptFile(file);
                // TODO 可选引擎后缀
            } else if (file.getName().endsWith(".js")) {
                InputStreamReader inputStreamReader = new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8);
                CompiledScript compiled = compilableEngine.compile(inputStreamReader);
                compiledScripts.put(file.getName().replace(".js", ""), compiled);
                inputStreamReader.close();
            }
        }
    }

    /**
     * 获取已加载的脚本文件
     *
     * @return 脚本文件列表
     */
    public List<String> getFileNames() {
        return Collections.list(compiledScripts.keys());
    }

    /**
     * 判断是否存在该脚本
     *
     * @param scriptName 脚本名
     * @return boolean
     */
    public boolean containsFile(String scriptName) {
        return compiledScripts.containsKey(scriptName);
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
        if (!enabled) return null;
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
            return random.nextInt(1 + last - first) + first;
        }

        public static double randomDouble(double min, double max) {
            double range = (max - min) * Math.random();
            return min + range;
        }
    }
}
