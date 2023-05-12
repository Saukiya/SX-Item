package github.saukiya.sxitem.data.random.randoms.script;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptContext;
import java.io.File;
import java.io.FileReader;

public class JavaScriptEngine {
    private static JavaScriptEngine instance;
    private final ScriptEngine engine;
    private final ScriptContext context;

    private JavaScriptEngine() {
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("js");
        context = engine.getContext();
    }

    public static synchronized JavaScriptEngine getInstance() {
        if (instance == null) {
            instance = new JavaScriptEngine();
        }
        return instance;
    }


    public void loadScript(String filename) throws Exception {
        FileReader reader = new FileReader(filename);
        context.setReader(reader);
        engine.eval(reader);
    }

    public void loadScript(File filename) throws Exception {
        FileReader reader = new FileReader(filename);
        context.setReader(reader);
        engine.eval(reader);
    }

    public void reloadScriptEngine() {
        instance = new JavaScriptEngine();
    }

    public Object callFunction(String functionName, Object... args) throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append(functionName).append("(");
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(args[i]);
        }
        builder.append(")");
        return engine.eval(builder.toString());
    }
}