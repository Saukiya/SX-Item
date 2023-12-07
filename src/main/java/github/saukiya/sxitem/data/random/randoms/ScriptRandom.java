package github.saukiya.sxitem.data.random.randoms;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.IRandom;
import github.saukiya.sxitem.data.random.RandomDocker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptRandom implements IRandom {

    private static final Pattern pattern = Pattern.compile("(\\w+)\\.(\\w+)#(.+)");

    @Override
    public String replace(String key, RandomDocker docker) {
        // <j:script.function#666,1,aaa>
        // key = script.function#666,1,aaa
        Matcher matcher = pattern.matcher(key);
        if (matcher.matches()) {
            Object[] args = matcher.group(3).split(",");
            for (int i = 0; i < args.length; i++) {
                Player player = Bukkit.getPlayerExact(args[i].toString());
                if (player != null) args[i] = player;
            }
            Object result;
            try {
                result = SXItem.getScriptManager().callFunction(matcher.group(1), matcher.group(2), docker, args);
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
            if (result instanceof List) return String.join("\n", (List<String>) result);
            // TODO 这玩意只支持class版本 55.0 , jdk8的版本是52.0 System.getProperty("java.class.version")
            if (result.getClass().getSimpleName().equals("ScriptObjectMirror")) {
                org.openjdk.nashorn.api.scripting.ScriptObjectMirror som = (org.openjdk.nashorn.api.scripting.ScriptObjectMirror) result;
                List<String> list = new ArrayList<>();
                int i = 0;
                while (som.get(String.valueOf(i)) != null) {
                    list.add(som.get(String.valueOf(i++)).toString());
                }
                return String.join("\n", list);
            }
            return result.toString();
        }
        return null;
    }
}
