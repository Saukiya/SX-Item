package github.saukiya.sxitem.data.random.randoms.script;

import github.saukiya.sxitem.SXItem;

import java.io.File;
import java.util.ArrayList;

public class JavaScriptReader {

    private static final ArrayList<File> jsFiles = new ArrayList<>();

    public static void initScript() {
        try {
            JavaScriptEngine.getInstance();
            readScripts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<File> findJavaScriptFiles() {
        File dir = new File(SXItem.getInst().getDataFolder(), "script");
        if (!dir.exists()) {
            dir.mkdir();
        }
        addJavaScriptFiles(dir);
        System.out.println("Found " + jsFiles.size() + " scripts.");
        System.out.println(dir.getPath());
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
        findJavaScriptFiles();
        for (File jsFile : jsFiles) {
            System.out.println("Loading... script: " + jsFile.getName());
            JavaScriptEngine.getInstance().loadScript(jsFile);
        }
    }
}
