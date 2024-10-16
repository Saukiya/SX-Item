package github.saukiya.sxitem.data.random.randoms;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.IRandom;
import github.saukiya.sxitem.data.random.RandomDocker;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.script.Bindings;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptRandom implements IRandom {

    private static final Pattern pattern = Pattern.compile("(\\w+)\\.(\\w+)#(.+)");

    /**
     * 支持格式
     * <pre>
     *  &lt;j:File.Function&gt; - 执行无参数方法并返回值
     *  &lt;j:File.Function#AAA,BBB&gt; - 执行有参数方法并返回值
     * </pre>
     *
     * @param key    处理的key
     * @param docker 缓存
     * @return
     */
    @Override
    public String replace(String key, RandomDocker docker) {
        if (!SXItem.getScriptManager().isEnabled()) return null;
        Matcher matcher = pattern.matcher(key);
        if (matcher.matches()) {
            Object[] args = Arrays.stream(matcher.group(3).split(",")).map(arg -> {
                Player player = Bukkit.getPlayerExact(arg);
                if (player == null) return arg;
                return player;
            }).toArray();
            Object result;
            try {
                result = SXItem.getScriptManager().callFunction(matcher.group(1), matcher.group(2), docker, args);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            if (result instanceof Collection) return StringUtils.join((Collection) result, "\n");
            if (result instanceof Bindings) {
                // ScriptObjectMirror 本质是 Bindings
                return StringUtils.join(((Bindings) result).values(), "\n");
            }
            return result.toString();
        }
        return null;
    }
}
