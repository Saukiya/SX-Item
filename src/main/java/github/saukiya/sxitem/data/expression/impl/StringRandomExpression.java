package github.saukiya.sxitem.data.expression.impl;

import github.saukiya.sxitem.data.expression.ExpressionHandler;
import github.saukiya.sxitem.data.expression.IExpression;
import github.saukiya.sxitem.util.Util;
import github.saukiya.tools.helper.PlaceholderHelper;

/**
 * {@code <s:randomKey>} 获取一个随机文本
 * <pre>{@code
 *  "<s:key>" - 从key集合中随机抽取一个值
 *  "<s:AAA:BBB:CCC>" - 从AAA/BBB/CCC中随机抽取一个值
 * }</pre>
 */
public class StringRandomExpression implements IExpression {

    @Override
    public String replace(String key, ExpressionHandler handler) {
        if (key.indexOf(':') >= 0) {
            return PlaceholderHelper.setPlaceholders(handler.getPlayer(), Util.random(key.split(":")));
        }
        return PlaceholderHelper.setPlaceholders(handler.getPlayer(), handler.random(key));
    }
}
