package github.saukiya.sxitem.data.expression.impl;

import github.saukiya.sxitem.data.expression.ExpressionHandler;
import github.saukiya.sxitem.data.expression.IExpression;
import github.saukiya.sxitem.util.Util;
import github.saukiya.tools.util.CalculatorUtil;

/**
 * {@code <c:1+-(-10*100)/-1000>} - 计算表达式
 * <pre>{@code
 *  "<c:100 / 3>" - 计算 100 / 3 并返回小数(33.33)
 *  "<c:int -5 * <d:0.8_1.2>>" - 计算 5 * (0.8~1.2) 并返回int整数(4/5/6)
 *  "<c:<i:1_20> * <l:level#1.5:2.5:3.5>>" - 结合其他随机模式, 套公式返回小数
 * }</pre>
 */
public class CalculatorExpression implements IExpression {

    @Override
    public String replace(String key, ExpressionHandler handler) {
        if (key.startsWith("int")) {
            double result = CalculatorUtil.calculator(key.substring(3));
            return String.valueOf(Math.round(result));
        }
        double result = CalculatorUtil.calculator(key);
        return Util.format(result);
    }
}
