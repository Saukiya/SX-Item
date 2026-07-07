package github.saukiya.sxitem.data.expression.impl;

import github.saukiya.sxitem.data.expression.ExpressionHandler;
import github.saukiya.sxitem.data.expression.IExpression;

/**
 * {@code <null:...>} 空占位, 恒返回空串
 * <pre>{@code
 *  "<null:任意内容>" - 返回空串, 用于占位或临时注释一段变量
 * }</pre>
 */
public class NullExpression implements IExpression {

    @Override
    public String replace(String key, ExpressionHandler handler) {
        return "";
    }
}
