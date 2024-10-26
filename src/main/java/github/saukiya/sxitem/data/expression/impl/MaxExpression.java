package github.saukiya.sxitem.data.expression.impl;

import github.saukiya.sxitem.data.expression.ExpressionHandler;
import github.saukiya.sxitem.data.expression.IExpression;

/**
 * {@code <max:1:10>} 获取最大值
 * <pre>{@code
 *  "<max:12.3:34.5:45.6>" - 从12.3/34.5/45.6中找出最大值: 45.6
 * }</pre>
 */
public class MaxExpression implements IExpression {

    @Override
    public String replace(String key, ExpressionHandler handler) {
        String result = "";
        double max = Double.MIN_VALUE;

        for (String part : key.split(":")) {
            double num = Double.parseDouble(part);
            if (num > max) {
                max = num;
                result = part;
            }
        }
        return result;
    }
}