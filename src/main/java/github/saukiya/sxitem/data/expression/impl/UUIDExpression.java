package github.saukiya.sxitem.data.expression.impl;

import github.saukiya.sxitem.data.expression.ExpressionSpace;
import github.saukiya.sxitem.data.expression.IExpression;

import java.util.UUID;

/**
 * &lt;u:&gt; 返回一串uuid
 */
public class UUIDExpression implements IExpression {

    /**
     * 支持格式
     * <pre>
     *  &lt;u:random&gt; - 随机生成UUID
     *  &lt;u:xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx&gt; - 按照格式生成UUID
     * </pre>
     *
     * @param key    处理的key
     * @param space 缓存
     * @return
     */
    @Override
    public String replace(String key, ExpressionSpace space) {
        if (key.equals("random")) {
            return UUID.randomUUID().toString();
        }
        return UUID.nameUUIDFromBytes(key.getBytes()).toString();
    }
}
