package github.saukiya.sxitem.data.random.randoms.script;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.util.Config;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import javax.script.*;
import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JavaScriptEngine {
    private static final JavaScriptEngine INSTANCE = new JavaScriptEngine();
    private final ScriptEngine engine;
    private final Compilable compilableEngine;
    private final ConcurrentHashMap<String, CompiledScript> compiledScripts;

    private JavaScriptEngine() {
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("js");
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
            stringBuilder.append("const ").append(key).append(" = ").append(scriptLib.getString(key)).append(";\n");
        }
        stringBuilder.append("const sxitem = ").append(SXItem.getInst().getClass()).append("; \n");
        stringBuilder.append("const server = ").append(Bukkit.getServer().getClass()).append("; \n");
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

    public Object callFunction(Player sender, String scriptName, String functionName, Map<String, Object> args) throws Exception {
        CompiledScript compiled = compiledScripts.get(scriptName);
        if (compiled == null) {
            throw new Exception("Script not found: " + scriptName);
        }

        Bindings bindings = engine.createBindings();
        bindings.put("sender", sender);

        bindings.putAll(args);

        Object result = compiled.eval(bindings);
        Invocable invocableEngine = (Invocable) engine;
        return invocableEngine.invokeFunction(functionName, bindings);
    }
}