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
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ScriptManager {

    private static final Random random = new Random();

//    private final ConcurrentHashMap<String, CompiledScript> compiledScripts = new ConcurrentHashMap<>();

    private final Map<String, Bindings> bindingsMap = new HashMap<>();

    private final Function<File, String> getScriptName;

    private final JavaPlugin plugin;

    private final File rootDirectory;

    private final File globalFile;

    private ScriptEngine engine;

    private Compilable compilable;

    private Invocable invocable;

    private Bindings globalBindings;

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
//        compiledScripts.clear();
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
//            compiledScripts.clear();
            enabled = false;
            return;
        }
        engine.getBindings(ScriptContext.ENGINE_SCOPE).forEach((k1, v1) -> {
            plugin.getLogger().info(String.format("%-15s", "key-" + k1 + ":") + "\t" + v1.getClass().getCanonicalName());
        });
        bindingsMap.forEach((key, bindings) -> plugin.getLogger().info("\t" + key + ": " + String.join(",", bindings.keySet())));
        plugin.getLogger().info("Loaded " + bindingsMap.size() + " Scripts");
    }

    /**
     * 初始化引擎
     */
    private void initEngine() throws Exception {
        String engineName = Config.getConfig().getString(Config.SCRIPT_ENGINE, "js");
        if (engineName.isEmpty()) throw new NullPointerException("Scripts Disabled");
        System.setProperty("nashorn.args", "--language=es6 -Dnashorn.args=--no-deprecation-warning");
        val engineManager = new ScriptEngineManager();
        engine = engineManager.getEngineByName(engineName);
        if (engine == null) {
            val engineNames = engineManager.getEngineFactories().stream().map(ScriptEngineFactory::getEngineName).collect(Collectors.joining(", "));
            throw new ScriptException("No Find ScriptEngine: " + engineName + ", Can Use: " + engineNames);
        }
        globalBindings = engine.createBindings();
        // class路径在jdk8中不一致
        Class<?> clazz = Class.forName(System.getProperty("java.class.version").startsWith("52") ?
                "jdk.internal.dynalink.beans.StaticClass" :
                "jdk.dynalink.beans.StaticClass");
        Method method = clazz.getMethod("forClass", Class.class);
        globalBindings.put("Bukkit", method.invoke(null, Bukkit.class));
        globalBindings.put("Arrays", method.invoke(null, Arrays.class));
        globalBindings.put("Utils", method.invoke(null, Utils.class));
        globalBindings.put(plugin.getName().replaceAll("[^a-zA-Z]", ""), method.invoke(null, plugin.getClass()));
        compilable = (Compilable) engine;
        invocable = (Invocable) engine;
        try (val globalReader = new FileReader(globalFile)) {
            val globalScript = compilable.compile(globalReader);
            globalScript.eval(globalBindings);
            engine.setBindings(globalBindings, ScriptContext.GLOBAL_SCOPE);
            bindingsMap.put(getScriptName.apply(globalFile), globalBindings);

//            compiledScripts.put(getScriptName.apply(globalFile), globalScript);
        }
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
            } else {
                String scriptName = getScriptName.apply(file);
                try (val reader = new FileReader(file)) {
                    CompiledScript compiled = compilable.compile(reader);

//                    // 默认写法: 不需要eval

//                    // content解法
//                    val content = new SimpleScriptContext();
//                    content.getBindings(ScriptContext.ENGINE_SCOPE).putAll(bindings);
//                    compiled.eval(content);
//                    contentMap.put(scriptName, content);

                    // binding解法
                    val bindings = engine.createBindings();
                    compiled.eval(bindings);
                    bindingsMap.put(scriptName, bindings);
                    globalBindings.put(scriptName, bindings);

//                    compiledScripts.put(scriptName, compiled);
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
        return new ArrayList<>(getScriptNames());
    }

    public Set<String> getScriptNames() {
        return bindingsMap.keySet();
    }

    public Set<String> getScriptFunc(String scriptName) {
        val bindings = bindingsMap.get(scriptName);
        if (bindings == null) return Collections.emptySet();
        return bindings.keySet();
    }

    /**
     * 判断是否存在该脚本
     *
     * @param scriptName 脚本名
     * @return boolean
     */
    public boolean containsFile(String scriptName) {
        return bindingsMap.containsKey(scriptName);
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
        val bindings = bindingsMap.get(scriptName);
//        CompiledScript compiled = compiledScripts.get(scriptName);
        if (bindings == null) {
            throw new Exception("Script not found: " + scriptName);
        }
//        // 之前写法
//        compiled.eval();

//        // content 解法
//        engine.setContext(contentMap.get(scriptName));

        // bindings 解法
        engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        
        
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
