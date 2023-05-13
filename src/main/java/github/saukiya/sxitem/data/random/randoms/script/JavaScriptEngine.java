package github.saukiya.sxitem.data.random.randoms.script;

import javax.script.*;
import java.io.File;
import java.io.FileReader;
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
    }

    public static JavaScriptEngine getInstance() {
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

    public Object callFunction(String scriptName, String functionName, Object... args) throws Exception {
        CompiledScript compiled = compiledScripts.get(scriptName);
        if (compiled == null) {
            throw new Exception("Script not found: " + scriptName);
        }

        Bindings bindings = engine.createBindings();
        for (int i = 0; i < args.length; i++) {
            String paramName = "arg" + i;
            bindings.put(paramName, args[i]);
        }

        Object result = compiled.eval(bindings);
        Invocable invocableEngine = (Invocable) engine;
        return invocableEngine.invokeFunction(functionName, bindings);
    }
}