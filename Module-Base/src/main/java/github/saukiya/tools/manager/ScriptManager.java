package github.saukiya.tools.manager;

import github.saukiya.tools.helper.PlaceholderHelper;
import github.saukiya.tools.nms.NMS;
import lombok.Getter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import javax.script.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 脚本管理工具
 */
public class ScriptManager implements Listener {

//    private final ConcurrentHashMap<String, CompiledScript> compiledScripts = new ConcurrentHashMap<>();

    private final Map<String, Bindings> bindingsMap = new HashMap<>();

    private final JavaPlugin plugin;

    private final String engineName;

    private final File rootDirectory;

    private final File globalDirectory;

    private ScriptEngine engine;

    private Compilable compilable;

    private Invocable invocable;

    private Bindings globalBindings;

    @Getter
    private boolean enabled;

    /**
     * 初始化引擎模块 默认引擎为js
     * <pre>
     * 插件名会作为静态类引用
     * 读取插件下Scripts文件夹的脚本文件
     * 并将Scripts/Global文件夹内的脚本作为全局变量
     * </pre>
     * @param plugin
     */
    public ScriptManager(JavaPlugin plugin) {
        this(plugin, "js");
    }

    /**
     * 初始化引擎模块
     * <pre>
     * 插件名会作为静态类引用
     * 读取插件下Scripts文件夹的脚本文件
     * 并将Scripts/Global文件夹内的脚本作为全局变量
     * </pre>
     *
     * @param plugin
     */
    public ScriptManager(JavaPlugin plugin, String engineName) {
        this.plugin = plugin;
        this.engineName = engineName;
        this.rootDirectory = new File(plugin.getDataFolder(), "Scripts");
        this.globalDirectory = new File(rootDirectory, "Global");
        reload();
    }

    /**
     * 重新加载
     */
    public void reload() {
        HandlerList.unregisterAll(this);
//        compiledScripts.clear();
        bindingsMap.clear();
        try {
            if (!rootDirectory.exists()) {
                plugin.getLogger().warning("Directory is not exists: " + rootDirectory.getName());
                return;
            }
            initEngine();
            loadScriptFile(rootDirectory);
        } catch (Exception e) {
            if (e instanceof NullPointerException) {
                plugin.getLogger().info(e.getMessage());
            } else {
                plugin.getLogger().warning("Load scripts error: " + e.getMessage());
            }
            enabled = false;
            return;
        }
        enabled = true;
        plugin.getLogger().info("Loaded " + bindingsMap.size() + " Scripts");
    }

    /**
     * 初始化引擎
     */
    private void initEngine() throws Exception {
        if (engineName.isEmpty()) throw new NullPointerException("Scripts Disabled");
        val engineManager = new ScriptEngineManager();
        if (engineName.equals("js")) {
            // nashorn 参数
            System.setProperty("nashorn.args", "--language=es6");
        }
        engine = engineManager.getEngineByName(engineName);
        if (engine == null) {
            if (engineName.equals("js") && NMS.hasClass("org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory")) {
                engine = new org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory().getScriptEngine();
            } else {
                val engineNames = engineManager.getEngineFactories().stream().map(ScriptEngineFactory::getEngineName).collect(Collectors.joining(", "));
                throw new ScriptException("No Find ScriptEngine: " + engineName + (engineNames.isEmpty() ? "" : "Can Use: " + engineNames));
            }
        }

        globalBindings = engine.createBindings();
        // class路径在jdk8中不一致
        Class<?> clazz = Class.forName(System.getProperty("java.class.version").startsWith("52") ?
                "jdk.internal.dynalink.beans.StaticClass" :
                "jdk.dynalink.beans.StaticClass");
        Method method = clazz.getMethod("forClass", Class.class);
        globalBindings.put("listener", this);
        globalBindings.put("Bukkit", method.invoke(null, Bukkit.class));
        globalBindings.put("Arrays", method.invoke(null, Arrays.class));
        globalBindings.put("Utils", method.invoke(null, Utils.class));
        globalBindings.put(plugin.getName().replaceAll("[^a-zA-Z]", ""), method.invoke(null, plugin.getClass()));
        engine.setBindings(globalBindings, ScriptContext.GLOBAL_SCOPE);
        compilable = (Compilable) engine;
        invocable = (Invocable) engine;
        loadGlobalFile(globalDirectory);
        bindingsMap.put("Global", globalBindings);
    }

    private void loadGlobalFile(File files) throws IOException, ScriptException {
        for (File file : files.listFiles()) {
            if (file.getName().startsWith("NoLoad")) continue;
            if (file.isDirectory()) {
                loadGlobalFile(file);
            } else {
                try (val globalReader = new FileReader(file)) {
                    val globalScript = compilable.compile(globalReader);
                    globalScript.eval(globalBindings);
                }
            }
        }

    }

    /**
     * 加载数据-遍历文件读取script
     *
     * @param files File
     */
    private void loadScriptFile(File files) throws IOException, ScriptException {
        for (File file : files.listFiles()) {
            if (file.getName().startsWith("NoLoad") || file.equals(globalDirectory)) continue;
            if (file.isDirectory()) {
                loadScriptFile(file);
            } else {
                int indexOf = file.getName().indexOf('.');
                String scriptName = indexOf != -1 ? file.getName().substring(0, indexOf) : file.getName();
                try (val reader = new FileReader(file)) {
                    CompiledScript compiled = compilable.compile(reader);

                    // binding解法
                    val bindings = engine.createBindings();
                    compiled.eval(bindings);
                    bindingsMap.put(scriptName, bindings);
                    globalBindings.put(scriptName, bindings);

//                    // 之前写法 把compiled存起来, 执行的时候再eval
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
    @Deprecated
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
     * @return 基础类型 或 Bindings {{@link Map}}
     */
    public Object callFunction(String scriptName, String functionName, Object... args) throws Exception {
        if (!enabled) return null;
        val bindings = bindingsMap.get(scriptName);
//        CompiledScript compiled = compiledScripts.get(scriptName);
        if (bindings == null) {
            throw new Exception("Script not found: " + scriptName);
        }

        // bindings 解法+ 跳过setBindings处理
        return callFunction(bindings, functionName, args);

//        // 之前写法 eval 后 invokeFunction
//        compiled.eval();
//        return invocable.invokeFunction(functionName, args);
    }

    public Object callFunction(Bindings bindings, String functionName, Object... args) throws Exception {
        if (!enabled) return null;
        return invocable.invokeMethod(bindings, functionName, args);
    }

    /**
     * script工具类
     */
    public static class Utils {

        public static final Random random = new Random();

        public static <T> List<T> mutableList(T... args) {
            return Arrays.stream(args).collect(Collectors.toList());
        }

        public static String fromPlaceholderAPI(Player player, String str) {
            return PlaceholderHelper.setPlaceholders(player, str);
        }

        public static String asColor(String string) {
            return ChatColor.translateAlternateColorCodes('&', string);
        }

        public static int randomInt(int v1, int v2) {
            return random.nextInt(1 + Math.abs(v2 - v1)) + Math.min(v1, v2);
        }

        public static double randomDouble(double v1, double v2) {
            return random.nextDouble() * (v2 - v1) + v1;
        }
    }
}
