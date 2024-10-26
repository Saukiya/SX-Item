package github.saukiya.sxitem.data.expression.impl;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.expression.ExpressionHandler;
import github.saukiya.sxitem.data.expression.IExpression;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.script.Bindings;
import java.util.Arrays;
import java.util.Collection;

/**
 * {@code <j:File.function#args1,args2>} 调用脚本引擎方法
 * <pre>{@code
 *  "<j:File.function>" - 执行无参数方法 File.function(handler,null)
 *  "<j:File.function#AAA>" - 执行有参数方法 File.function(handler,[AAA])
 * }</pre>
 */
public class ScriptExpression implements IExpression {

    @Override
    public String replace(String key, ExpressionHandler handler) {
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
            result = SXItem.getScriptManager().callFunction(fileName, functionName, handler, args);
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
