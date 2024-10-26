package github.saukiya.sxitem.data.expression.impl;

import github.saukiya.sxitem.data.expression.ExpressionHandler;
import github.saukiya.sxitem.data.expression.IExpression;
import github.saukiya.sxitem.util.Util;

/**
 * {@code <l:randomKey>} 获取并锁定一个随机文本
 * <pre>{@code
 *  "<l:key>" - 从key集合中随机抽取一个值并锁定
 *  "<l:key#AAA:BBB>" - 从AAA/BBB中随机抽取一个值并锁定
 * }</pre>
 */
public class LockStringExpression implements IExpression {

    @Override
    public String replace(String key, ExpressionHandler handler) {
        int indexOf = key.indexOf('#');
        String temp = null;
        if (indexOf != -1) {
            temp = key.substring(indexOf + 1);
            key = key.substring(0, indexOf);
        }

        String value = handler.getLockMap().get(key);
        if (value != null) {
            return value;
        }

        if (temp != null) {
            value = handler.getOtherMap().get(key);
            if (value == null) {
                value = Util.random(temp.split(":"));
            }
        } else {
            value = handler.random(key);
        }

        handler.getLockMap().put(key, value = handler.replace(value));
        return value;
    }
}
