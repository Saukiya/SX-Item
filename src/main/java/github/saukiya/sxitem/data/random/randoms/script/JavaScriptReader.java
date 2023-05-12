package github.saukiya.sxitem.data.random.randoms.script;

import github.saukiya.sxitem.SXItem;

import java.io.File;
import java.util.ArrayList;

public class JavaScriptReader {

    private static final ArrayList<File> jsFiles = new ArrayList<>();

    public static ArrayList<File> findJavaScriptFiles() {
        File dir = new File(SXItem.getInst().getDataFolder(), "scripts");
        addJavaScriptFiles(dir);
        return jsFiles;
    }

    private static void addJavaScriptFiles(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    addJavaScriptFiles(file);
                } else if (file.getName().endsWith(".js")) {
                    jsFiles.add(file);
                }
            }
        }
    }

    public static void readScripts() throws Exception {
        for (File jsFile : findJavaScriptFiles()) {
            JavaScriptEngine.getInstance().loadScript(jsFile);
        }
    }
}
