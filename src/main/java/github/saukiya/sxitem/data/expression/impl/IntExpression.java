package github.saukiya.sxitem.data.expression.impl;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.expression.ExpressionSpace;
import github.saukiya.sxitem.data.expression.IExpression;

/**
 * &lt;i:&gt; 创建一个随机整数
 */
public class IntExpression implements IExpression {

    /**
     * 支持格式
     * <pre>
     *  &lt;i:10_100&gt; - 从10-100随机抽一个整数
     *  &lt;i:100_10&gt; - 从10-100随机抽一个整数(不分顺序)
     * </pre>
     *
     * @param key    处理的key
     * @param space 缓存
     * @return
     */
    @Override
    public String replace(String key, ExpressionSpace space) {
        int index = key.indexOf('_');
        if (index == -1) return key;
        int min = Integer.parseInt(key.substring(0, index));
        int max = Integer.parseInt(key.substring(index + 1));
        return String.valueOf(SXItem.getRandom().nextInt(1 + Math.abs(max - min)) + Math.min(max, min));
    }
}
