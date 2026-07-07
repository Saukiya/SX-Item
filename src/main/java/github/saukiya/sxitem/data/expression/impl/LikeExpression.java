package github.saukiya.sxitem.data.expression.impl;

import github.saukiya.sxitem.data.expression.ExpressionHandler;
import github.saukiya.sxitem.data.expression.IExpression;

/**
 * {@code <like:A==B>} 模糊包含判断, A 含 B 或 B 含 A 则返回 true
 * <pre>{@code
 *  "<like:钢铁长剑==长剑>" - 返回 true (前者包含后者)
 *  "<like:长剑==钢铁长剑>" - 返回 true (后者包含前者)
 *  "<like:斧==剑>" - 返回 false
 * }</pre>
 * 说明: 缺少 "==" 分隔符时返回 false
 */
public class LikeExpression implements IExpression {

    @Override
    public String replace(String key, ExpressionHandler handler) {
        int index = key.indexOf("==");
        if (index == -1) return "false";
        String a = key.substring(0, index);
        String b = key.substring(index + 2);
        return String.valueOf(a.contains(b) || b.contains(a));
    }
}
