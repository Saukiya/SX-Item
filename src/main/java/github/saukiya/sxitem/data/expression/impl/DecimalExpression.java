package github.saukiya.sxitem.data.expression.impl;

import github.saukiya.sxitem.data.expression.ExpressionHandler;
import github.saukiya.sxitem.data.expression.IExpression;
import github.saukiya.sxitem.util.Util;

/**
 * {@code <d:1_10>} 得到一个随机小数
 * <pre>
 * 特性: 支持负数, 不分顺序
 * {@code
 *  "<d:1.5_2.5>" - 从1.5-2.5随机抽一个小数
 *  "<d:100_10>" - 从10-100随机抽一个小数(不分顺序)
 * }</pre>
 */
public class DecimalExpression implements IExpression {

    @Override
    public String replace(String key, ExpressionHandler handler) {
        int index = key.indexOf('_');
        if (index == -1) return key;
        double min = Double.parseDouble(key.substring(0, index));
        double max = Double.parseDouble(key.substring(index + 1));
        return Util.format(Util.random(min, max));
    }
}
