package github.saukiya.sxitem.data.expression;

/**
 * 表达式接口
 */
public interface IExpression {

    /**
     * 替换字符串内容
     *
     * @param key    处理的key
     * @param handler 表达式处理器
     * @return 返回为空则删除此行
     */
    String replace(String key, ExpressionHandler handler);

    /**
     * 验证字符串内容是否正常
     *
     * @param key
     * @param handler
     * @return
     */
    default boolean validation(String key, ExpressionHandler handler) {
        return true;
    }
}
