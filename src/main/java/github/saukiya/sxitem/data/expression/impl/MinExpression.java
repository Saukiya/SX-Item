package github.saukiya.sxitem.data.expression.impl;

import github.saukiya.sxitem.data.expression.ExpressionSpace;
import github.saukiya.sxitem.data.expression.IExpression;

/**
 * &lt;min:&gt; 获取最小值
 */
public class MinExpression implements IExpression {

    /**
     * 支持格式
     * <pre>
     *  &lt;min:12.3:34.5:45.6&gt; - 从12.3/34.5/45.6中找出最小值
     * </pre>
     *
     * @param key   处理的key
     * @param space 缓存
     * @return
     */
    @Override
    public String replace(String key, ExpressionSpace space) {
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