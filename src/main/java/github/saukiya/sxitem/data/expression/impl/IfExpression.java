package github.saukiya.sxitem.data.expression.impl;

import github.saukiya.sxitem.data.expression.ExpressionHandler;
import github.saukiya.sxitem.data.expression.IExpression;

/**
 * {@code <if:条件?真值:假值>} 三元条件, 按条件返回不同文本
 * <pre>{@code
 *  "<if:true?A:B>" - 条件为真返回 A, 否则返回 B
 *  "<if:<eq:<l:品质>==史诗>?&c★史诗:&7普通>" - 结合 <eq:> 使用
 * }</pre>
 * 说明:
 *  1. 真值集合为 true/1/yes/ok, 其余一律视为假
 *  2. "真值" 不能含裸露的 ':' (首个 ':' 作为真假分隔), 如有需要请用嵌套表达式
 *  3. 缺少 '?' 或 ':' 分隔符时返回空串
 */
public class IfExpression implements IExpression {

    @Override
    public String replace(String key, ExpressionHandler handler) {
        int q = key.indexOf('?');
        if (q == -1) return "";
        String cond = key.substring(0, q).trim();
        String rest = key.substring(q + 1);
        int c = rest.indexOf(':');
        if (c == -1) return "";
        String trueVal = rest.substring(0, c);
        String falseVal = rest.substring(c + 1);
        switch (cond) {
            case "true":
            case "1":
            case "yes":
            case "ok":
                return trueVal;
            default:
                return falseVal;
        }
    }
}
