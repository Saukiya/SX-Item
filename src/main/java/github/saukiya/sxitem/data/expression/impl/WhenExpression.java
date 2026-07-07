package github.saukiya.sxitem.data.expression.impl;

import github.saukiya.sxitem.data.expression.ExpressionHandler;
import github.saukiya.sxitem.data.expression.IExpression;

/**
 * {@code <when:待匹配$值1:返回1|值2:返回2|default:兜底>} 多分支选择
 * <pre>{@code
 *  "<when:<l:品质>$史诗:&c史诗|传说:&6传说|default:&7普通>" - 按品质返回对应文本
 *  "<when:A$A:命中>" - 匹配则返回"命中", 无匹配且无兜底返回空串
 * }</pre>
 * 说明:
 *  1. '$' 分隔"待匹配值"与"分支列表", '|' 分隔各分支, 首个 ':' 分隔"匹配值:返回值"
 *  2. 兜底分支的匹配值为 default 或 else, 命中所有分支失败时返回
 *  3. 缺少 '$' 或无匹配且无兜底时返回空串
 */
public class WhenExpression implements IExpression {

    @Override
    public String replace(String key, ExpressionHandler handler) {
        int d = key.indexOf('$');
        if (d == -1) return "";
        String match = key.substring(0, d);
        String[] cases = key.substring(d + 1).split("\\|");
        String def = "";
        for (String c : cases) {
            int i = c.indexOf(':');
            if (i == -1) continue;
            String caseValue = c.substring(0, i);
            String result = c.substring(i + 1);
            if (caseValue.equals(match)) {
                return result;
            }
            if (caseValue.equals("default") || caseValue.equals("else")) {
                def = result;
            }
        }
        return def;
    }
}
