package github.saukiya.sxitem.data.expression.impl;

import github.saukiya.sxitem.data.expression.ExpressionHandler;
import github.saukiya.sxitem.data.expression.IExpression;

/**
 * {@code <min:1:10>} 获取最小值
 * <pre>{@code
 *  "<min:12.3:34.5:45.6>" - 从12.3/34.5/45.6中找出最小值: 12.3
 * }</pre>
 */
public class MinExpression implements IExpression {

    @Override
    public String replace(String key, ExpressionHandler handler) {
        String result = "";
        double min = Double.MAX_VALUE;

        for (String part : key.split(":")) {
            double num = Double.parseDouble(part);
            if (num < min) {
                min = num;
                result = part;
            }
        }
        return result;
    }
}