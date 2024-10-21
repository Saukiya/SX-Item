package github.saukiya.sxitem.data.expression.impl;

import github.saukiya.sxitem.data.expression.ExpressionSpace;
import github.saukiya.sxitem.data.expression.IExpression;

/**
 * &lt;max:&gt; 获取最大值
 */
public class MaxExpression implements IExpression {

    /**
     * 支持格式
     * <pre>
     *  &lt;max:12.3:34.5:45.6&gt; - 从12.3/34.5/45.6中找出最大值
     * </pre>
     *
     * @param key   处理的key
     * @param space 缓存
     * @return
     */
    @Override
    public String replace(String key, ExpressionSpace space) {
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