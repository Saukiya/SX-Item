package github.saukiya.sxitem.data;

import github.saukiya.sxitem.util.Config;
import github.saukiya.util.helper.PlaceholderHelper;
import lombok.Getter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.script.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ScriptManager {

    private static final Random random = new Random();

    private final ConcurrentHashMap<String, CompiledScript> compiledScripts = new ConcurrentHashMap<>();

    private final Function<File, String> getScriptName;

    private final JavaPlugin plugin;

    private final File rootDirectory;

    private final File globalFile;

    private ScriptEngine engine;

    private Compilable compilable;

    private Invocable invocable;

    @Getter
    private boolean enabled;

    public ScriptManager(JavaPlugin plugin) {
        this(plugin, "Global.js");
    }

    /**
     * 初始化引擎模块 插件名会作为静态类调用
     *
     * @param plugin
     * @param globalFileName
     */
    public ScriptManager(JavaPlugin plugin, String globalFileName) {
        this.plugin = plugin;
        this.rootDirectory = new File(plugin.getDataFolder(), "Scripts");
        this.globalFile = new File(rootDirectory, globalFileName);
        val regex = Pattern.compile("\\..+$");
        getScriptName = file -> regex.matcher(file.getName()).replaceAll("");
        reload();
    }

    /**
     * 重新加载
     */
    public void reload() {
        compiledScripts.clear();
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
                plugin.getLogger().info(e.getMessage());
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
        String engineName = Config.getConfig().getString(Config.SCRIPT_ENGINE, "js");
        if (engineName.isEmpty()) throw new NullPointerException("Scripts Disabled");
        System.setProperty("nashorn.args", "--language=es6");
        val engineManager = new ScriptEngineManager();
        engine = engineManager.getEngineByName(engineName);
        if (engine == null) {
            val engineNames = engineManager.getEngineFactories().stream().map(ScriptEngineFactory::getEngineName).collect(Collectors.joining(", "));
            throw new ScriptException("No Find ScriptEngine: " + engineName + ", Can Use: " + engineNames);
        }
        // class路径在jdk8中不一致
        Class<?> clazz = Class.forName(System.getProperty("java.class.version").startsWith("52") ?
                "jdk.internal.dynalink.beans.StaticClass" :
                "jdk.dynalink.beans.StaticClass");
        Method method = clazz.getMethod("forClass", Class.class);
        engine.put("Bukkit", method.invoke(null, Bukkit.class));
        engine.put("Arrays", method.invoke(null, Arrays.class));
        engine.put("Utils", method.invoke(null, Utils.class));
        engine.put(plugin.getName().replaceAll("[^a-zA-Z]", ""), method.invoke(null, plugin.getClass()));
        compilable = (Compilable) engine;
        invocable = (Invocable) engine;
        try (val globalReader = new FileReader(globalFile)) {
            val global = compilable.compile(globalReader);
            global.eval();
            compiledScripts.put(getScriptName.apply(globalFile), global);
        }
    }

    /**
     * 加载数据-遍历文件读取script
     *
     * @param files File
     */
    private void loadScriptFile(File files) throws IOException, ScriptException {
        if (!enabled) return;
        val bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        for (File file : files.listFiles()) {
            if (file.getName().startsWith("NoLoad") || file.equals(globalFile)) continue;
            if (file.isDirectory()) {
                loadScriptFile(file);
            } else {
                String scriptName = getScriptName.apply(file);
                try (val reader = new FileReader(file)) {
                    CompiledScript compiled = compilable.compile(reader);
                    val simpleBindings = new SimpleBindings(new HashMap<>(bindings));
                    compiled.eval(simpleBindings);
//                    val property = simpleBindings.keySet().stream().filter(x -> !bindings.containsKey(x)).collect(Collectors.toList());
//                    plugin.getLogger().info(scriptName + ": " + String.join(", ", property));
                    compiledScripts.put(scriptName, compiled);
                }
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
