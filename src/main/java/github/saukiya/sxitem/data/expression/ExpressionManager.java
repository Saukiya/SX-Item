package github.saukiya.sxitem.data.expression;

import java.util.HashMap;
import java.util.Map;

/**
 * 表达式管理器
 */
public class ExpressionManager {

    protected static final Map<String, IExpression> RANDOMS = new HashMap<>();

    /**
     * 注册新的表达式类型
     *
     * @param expression 表达式
     * @param types      类型
     */
    public static void register(IExpression expression, char... types) {
        for (char type : types) {
            RANDOMS.put(String.valueOf(type), expression);
        }
    }

    /**
     * 注册新的表达式类型
     *
     * @param expression 表达式
     * @param types      类型
     */
    public static void register(IExpression expression, String... types) {
        for (String type : types) {
            RANDOMS.put(type, expression);
        }
    }

    /**
     * 获取随机类型
     *
     * @param type 类型
     * @return 随机处理
     */
    public static IExpression get(String type) {
        return RANDOMS.get(type);
    }
}