package github.saukiya.sxitem.data.expression.impl;

import github.saukiya.sxitem.data.expression.ExpressionHandler;
import github.saukiya.sxitem.data.expression.IExpression;
import github.saukiya.sxitem.util.Util;

/**
 * {@code <i:1_10>} 得到一个随机整数
 * <pre>
 * 特性: 支持负数, 不分顺序
 * 别名: <code>r</code>
 * {@code
 *  "<i:10_100>" - 从10-100随机抽一个整数
 *  "<i:100_10>" - 从10-100随机抽一个整数(不分顺序)
 * }</pre>
 */
public class IntExpression implements IExpression {

    @Override
    public String replace(String key, ExpressionHandler handler) {
        int index = key.indexOf('_');
        if (index == -1) return key;
        int min = Integer.parseInt(key.substring(0, index));
        int max = Integer.parseInt(key.substring(index + 1));
        return String.valueOf(Util.random(min, max));
    }
}
