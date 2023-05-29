package github.saukiya.sxitem.data.random.randoms.script;

import github.saukiya.sxitem.data.random.IRandom;
import github.saukiya.sxitem.data.random.RandomDocker;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaScriptRandom implements IRandom {

    private static final Pattern pattern = Pattern.compile("(\\w+)\\.(\\w+)#(.+)");

    @Override
    public String replace(String key, RandomDocker docker) {
        //<j:script.function#666,1,aaa>
        // key = script.function#666,1,aaa
        Matcher matcher = pattern.matcher(key);
        if (matcher.matches()) {
            String[] stringArgs = matcher.group(3).split(",");// 如果直接用string[] 那可能数字是字符串
//            Object[] args = new Object[stringArgs.length];// 除非转换成int或者double?反正放进js里都是数
            Object result;
            try {
                result = JavaScriptEngine.getInstance().callFunction(matcher.group(1), matcher.group(2), docker, stringArgs);
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
            if (result instanceof List) return String.join("\n", (List<String>) result);
            return result.toString();
        }
        return null;
    }
}
