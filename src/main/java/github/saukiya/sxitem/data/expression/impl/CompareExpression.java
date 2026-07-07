package github.saukiya.sxitem.data.expression.impl;

import github.saukiya.sxitem.data.expression.ExpressionHandler;
import github.saukiya.sxitem.data.expression.IExpression;

/**
 * {@code <cmp:A 算子 B>} 比较两值, 返回 true/false
 * <pre>{@code
 *  "<cmp:10 gt 9>"  - 10 > 9  返回 true
 *  "<cmp:10 lt 9>"  - 10 < 9  返回 false
 *  "<cmp:5 ge 5>"   - 5 >= 5  返回 true
 *  "<cmp:3 le 5>"   - 3 <= 5  返回 true
 *  "<cmp:a ne b>"   - a != b  返回 true
 *  "<cmp:5.0 eq 5>" - 数值相等 返回 true
 *  "<if:<cmp:<l:等级> ge 10>?可用:不可用>" - 结合 <if:> 做数值门槛
 * }</pre>
 * 说明:
 *  1. 算子: eq(等于) ne(不等) gt(大于) lt(小于) ge(不小于) le(不大于), 前后须各有一个空格
 *  2. 类型感知: 两侧均可解析为数字时按数值比较, 否则退化为字符串比较
 *     (eq/ne 用 equals, gt/lt/ge/le 用字典序 compareTo)
 *  3. 缺少合法算子时返回 false
 */
public class CompareExpression implements IExpression {

    private static final String[] OPERATORS = {"eq", "ne", "gt", "lt", "ge", "le"};

    @Override
    public String replace(String key, ExpressionHandler handler) {
        String op = null;
        int opIndex = -1;
        for (String candidate : OPERATORS) {
            int i = key.indexOf(' ' + candidate + ' ');
            if (i != -1 && (opIndex == -1 || i < opIndex)) {
                opIndex = i;
                op = candidate;
            }
        }
        if (op == null) return "false";

        String a = key.substring(0, opIndex).trim();
        String b = key.substring(opIndex + op.length() + 2).trim();

        Double da = parseDouble(a);
        Double db = parseDouble(b);
        boolean result;
        if (da != null && db != null) {
            result = compare(op, Double.compare(da, db));
        } else if ("eq".equals(op)) {
            result = a.equals(b);
        } else if ("ne".equals(op)) {
            result = !a.equals(b);
        } else {
            result = compare(op, a.compareTo(b));
        }
        return String.valueOf(result);
    }

    /**
     * 根据算子与比较结果(compareTo/compare 的符号)得出布尔值
     *
     * @param op 算子
     * @param c  比较结果 (<0 / 0 / >0)
     * @return 布尔结果
     */
    private boolean compare(String op, int c) {
        switch (op) {
            case "eq":
                return c == 0;
            case "ne":
                return c != 0;
            case "gt":
                return c > 0;
            case "lt":
                return c < 0;
            case "ge":
                return c >= 0;
            case "le":
                return c <= 0;
            default:
                return false;
        }
    }

    private Double parseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
