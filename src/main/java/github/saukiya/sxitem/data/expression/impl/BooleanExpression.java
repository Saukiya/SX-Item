package github.saukiya.sxitem.data.expression.impl;

import github.saukiya.sxitem.data.expression.ExpressionHandler;
import github.saukiya.sxitem.data.expression.IExpression;

/**
 * {@code <b:AA#AA:BB>} 判断首值与后续值是否相同
 * <pre>{@code
 *  "<b:AA#AA>" - 判断AA是否与AA相同, 相同则保留此行
 *  "<b:AA#BB:CC>" - 判断AA是否与BB,CC相同, 不同则删除此行
 *  "<b:AA:AA>" - 与第一条逻辑相同
 *  "<b:AA:BB:CC>" - 与第二条逻辑相同
 * }</pre>
 */
public class BooleanExpression implements IExpression {

    @Override
    public String replace(String key, ExpressionHandler handler) {
        char[] chars = key.toCharArray();
        int index = 0, check = 0, length = chars.length;

        for (int i = 0; i < length; i++) {
            switch (chars[i]) {
                case '#':
                case ':':
                    check = i;
                    break;
                default:
                    continue;
            }
            break;
        }
        if (check == 0) return null;
        for (int i = check + 1; i < length; i++) {
            char c = chars[i];
            if (c == ':') {
                if (index == check) {
                    return "";
                }
                index = 0;
            } else {
                if (index != -1 && chars[index++] != c) {
                    index = -1;
                }
            }
        }
        return index == check ? "" : null;
    }
}
