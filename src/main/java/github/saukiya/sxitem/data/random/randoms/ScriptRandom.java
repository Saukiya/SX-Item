package github.saukiya.sxitem.data.random.randoms;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.IRandom;
import github.saukiya.sxitem.data.random.RandomDocker;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.script.Bindings;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptRandom implements IRandom {

    private static final Pattern pattern = Pattern.compile("(\\w+)\\.(\\w+)#(.+)");

    @Override
    public String replace(String key, RandomDocker docker) {
        if (!SXItem.getScriptManager().isEnabled()) return null;
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
            if (result instanceof Bindings) {
                // ScriptObjectMirror 本质是 Bindings
                return StringUtils.join(((Bindings) result).values(), "\n");
            }
            return result.toString();
        }
        return null;
    }
}
