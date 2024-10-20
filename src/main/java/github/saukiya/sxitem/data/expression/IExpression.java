package github.saukiya.sxitem.data.expression;

public interface IExpression {

    /**
     * 替换字符串内容
     *
     * @param key    处理的key
     * @param space 表达式空间
     * @return 返回为空则删除此行
     */
    String replace(String key, ExpressionSpace space);

}
