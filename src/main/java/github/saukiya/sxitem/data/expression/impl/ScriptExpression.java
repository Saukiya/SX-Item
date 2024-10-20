package github.saukiya.sxitem.data.expression.impl;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.expression.ExpressionSpace;
import github.saukiya.sxitem.data.expression.IExpression;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.script.Bindings;
import java.util.Arrays;
import java.util.Collection;

/**
 * &lt;j:&gt; 调用脚本引擎方法
 */
public class ScriptExpression implements IExpression {

    /**
     * 支持格式
     * <pre>
     *  &lt;j:File.function&gt; - 执行无参数方法 File.function(space,null)
     *  &lt;j:File.function#AAA&gt; - 执行有参数方法 File.function(space,[AAA])
     * </pre>
     *
     * @param key    处理的key
     * @param space 缓存
     * @return
     */
    @Override
    public String replace(String key, ExpressionSpace space) {
        if (!SXItem.getScriptManager().isEnabled()) return null;
        int index1 = key.indexOf('.');
        Validate.isTrue(index1 != -1, key);
        String fileName = key.substring(0, index1++);

        int index2 = key.indexOf('#', index1);
        index2 = index2 != -1 ? index2 : key.length();
        String functionName = key.substring(index1, index2++);

        Object[] args;
        if (index2 > key.length()) {
            args = null;
        } else {
            args = Arrays.stream(key.substring(index2).split(",")).map(arg -> {
                Player player = Bukkit.getPlayerExact(arg);
                if (player == null) return arg;
                return player;
            }).toArray();
        }

        Object result;
        try {
            result = SXItem.getScriptManager().callFunction(fileName, functionName, space, args);
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
}
