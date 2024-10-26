package github.saukiya.sxitem.data.expression.impl;

import github.saukiya.sxitem.data.expression.ExpressionHandler;
import github.saukiya.sxitem.data.expression.IExpression;

import java.util.UUID;

/**
 * {@code <u:random>} 返回一串uuid
 * <pre>{@code
 *  "<u:random>" - 随机生成UUID
 *  "<u:xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx>" - 按照格式生成UUID
 * }</pre>
 */
public class UUIDExpression implements IExpression {

    @Override
    public String replace(String key, ExpressionHandler handler) {
        if (key.equals("random")) {
            return UUID.randomUUID().toString();
        }
        return UUID.nameUUIDFromBytes(key.getBytes()).toString();
    }
}
