package github.saukiya.sxitem.data.random.randoms.script;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.RandomDocker;
import github.saukiya.sxitem.util.Config;
import jdk.dynalink.beans.StaticClass;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class JavaScriptEngine {
    private static JavaScriptEngine INSTANCE = new JavaScriptEngine();
    private final ScriptEngine engine;
    private final Compilable compilableEngine;
    private final ConcurrentHashMap<String, CompiledScript> compiledScripts;

    private JavaScriptEngine() {
        engine = new ScriptEngineManager().getEngineByName("js");
//        engine = new NashornScriptEngineFactory().getScriptEngine(SXItem.class.getClassLoader());
        engine.put("Bukkit", StaticClass.forClass(Bukkit.class));
        engine.put("SXItem", StaticClass.forClass(SXItem.class));
        engine.put("Arrays", StaticClass.forClass(Arrays.class));
        engine.put("Utils", StaticClass.forClass(JavaScriptUtils.class));
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

    public static JavaScriptEngine getInstance() {
        return INSTANCE;
    }

    public void loadScript(String filename) throws Exception {
        loadScript(new File(filename));
    }

    public void loadScript(File file) throws Exception {

        try{
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            CompiledScript compiled = compilableEngine.compile(inputStreamReader);
            compiledScripts.put(file.getName().replace(".js", ""), compiled);
            //System.out.println("Loaded script: " + file.getName());
            inputStreamReader.close();
        } catch (FileNotFoundException | ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    public void unloadScript(String scriptName) {
        compiledScripts.remove(scriptName);
    }

    public void reloadEngine() {
        INSTANCE = new JavaScriptEngine();
    }

    public Object callFunction(String scriptName, String functionName, RandomDocker docker, Object[] args) throws Exception {
        CompiledScript compiled = compiledScripts.get(scriptName);
        if (compiled == null) {
            throw new Exception("Script not found: " + scriptName);
        }
        compiled.eval();
        Invocable invocableEngine = (Invocable) engine;
        return invocableEngine.invokeFunction(functionName, docker, args);
    }
}