package github.saukiya.sxitem.data.expression.impl;

import github.saukiya.sxitem.data.expression.ExpressionHandler;
import github.saukiya.sxitem.data.expression.IExpression;

/**
 * {@code <eq:A==B>} 判断两值是否精确相等, 返回 true/false
 * <pre>{@code
 *  "<eq:AAA==AAA>" - 返回 true
 *  "<eq:AAA==BBB>" - 返回 false
 *  "<eq:<l:品质>==史诗>" - 结合其他表达式, 常作为 <if:> 的条件源
 * }</pre>
 * 说明: 缺少 "==" 分隔符时返回 false
 */
public class EqualExpression implements IExpression {

    @Override
    public String replace(String key, ExpressionHandler handler) {
        int index = key.indexOf("==");
        if (index == -1) return "false";
        String a = key.substring(0, index);
        String b = key.substring(index + 2);
        return String.valueOf(a.equals(b));
    }
}
