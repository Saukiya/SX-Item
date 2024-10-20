package github.saukiya.sxitem.data.expression.impl;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.expression.ExpressionSpace;
import github.saukiya.sxitem.data.expression.IExpression;
import github.saukiya.util.helper.PlaceholderHelper;

/**
 * &lt;s:&gt; 获取一个随机文本
 */
public class StringRandomExpression implements IExpression {

    /**
     * 支持格式
     * <pre>
     *  &lt;s:key&gt; - 从key集合中随机抽取一个值
     *  &lt;s:AAA:BBB:CCC&gt; - 从AAA/BBB/CCC中随机抽取一个值
     * </pre>
     * 
     * @param key    处理的key
     * @param space 缓存
     * @return
     */
    @Override
    public String replace(String key, ExpressionSpace space) {
        if (key.indexOf(':') >= 0) {
            String[] temp = key.split(":");
            return PlaceholderHelper.setPlaceholders(space.getPlayer(), temp[SXItem.getRandom().nextInt(temp.length)]);
        }
        return PlaceholderHelper.setPlaceholders(space.getPlayer(), space.random(key));
    }
}
