package github.saukiya.sxitem.data.random.randoms.script;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.RandomDocker;
import github.saukiya.sxitem.util.Config;
import jdk.dynalink.beans.StaticClass;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import javax.script.*;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class JavaScriptEngine {
    private static final JavaScriptEngine INSTANCE = new JavaScriptEngine();
    private final ScriptEngine engine;
    private final Compilable compilableEngine;
    private final ConcurrentHashMap<String, CompiledScript> compiledScripts;

    private JavaScriptEngine() {
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("js");
        engine.put("Bukkit", StaticClass.forClass(Bukkit.class));
        engine.put("SXItem", StaticClass.forClass(SXItem.class));
        engine.put("Arrays", StaticClass.forClass(Arrays.class));
        compilableEngine = (Compilable) engine;
        compiledScripts = new ConcurrentHashMap<>();
        init();
    }

    private void init() {
        StringBuilder stringBuilder = new StringBuilder();
        ConfigurationSection scriptLib = Config.getConfig().getConfigurationSection("ScriptLib");
        if (scriptLib == null) {
            return;
        }
        for (String key : scriptLib.getKeys(false)) {
            stringBuilder.append("const ").append(key).append(" = ").append(scriptLib.getString(key)).append("\n");
        }
        try {
            compilableEngine.compile(stringBuilder.toString());
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized JavaScriptEngine getInstance() {
        return INSTANCE;
    }

    public void loadScript(String filename) throws Exception {
        loadScript(new File(filename));
    }

    public void loadScript(File file) throws Exception {
        try (FileReader reader = new FileReader(file)) {
            CompiledScript compiled = compilableEngine.compile(reader);
            compiledScripts.put(file.getName(), compiled);
        }
    }

    public void unloadScript(String scriptName) {
        compiledScripts.remove(scriptName);
    }

    public Object callFunction(String scriptName, String functionName, RandomDocker docker, Object[] args) throws Exception {
        CompiledScript compiled = compiledScripts.get(scriptName);
        if (compiled == null) {
            throw new Exception("Script not found: " + scriptName);
        }

        Invocable invocableEngine = (Invocable) engine;
        return invocableEngine.invokeFunction(functionName, docker, args);
    }
}