package github.saukiya.sxitem.data.expression.impl;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.expression.ExpressionSpace;
import github.saukiya.sxitem.data.expression.IExpression;

/**
 * &lt;l:&gt; 获取并锁定一个随机文本
 */
public class LockStringExpression implements IExpression {

    /**
     * 支持格式
     * <pre>
     *  &lt;l:key&gt; - 从key集合中随机抽取一个值并锁定
     *  &lt;l:key#AAA:BBB&gt; - 从AAA/BBB中随机抽取一个值并锁定
     * </pre>
     *
     * @param key    处理的key
     * @param space 缓存
     * @return
     */
    @Override
    public String replace(String key, ExpressionSpace space) {
        int indexOf = key.indexOf('#');
        String temp = null;
        if (indexOf != -1) {
            temp = key.substring(indexOf + 1);
            key = key.substring(0, indexOf);
        }

        String value = space.getLockMap().get(key);
        if (value != null) {
            return value;
        }

        if (temp != null) {
            value = space.getOtherMap().get(key);
            if (value == null) {
                value = randomArray(temp.split(":"));
            }
        } else {
            value = space.random(key);
        }

        space.getLockMap().put(key, value = space.replace(value));
        return value;
    }

    public static String randomArray(String[] array) {
        return array[SXItem.getRandom().nextInt(array.length)];
    }
}
