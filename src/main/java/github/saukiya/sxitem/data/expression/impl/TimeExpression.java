package github.saukiya.sxitem.data.expression.impl;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.expression.ExpressionSpace;
import github.saukiya.sxitem.data.expression.IExpression;
import org.apache.commons.lang.StringUtils;

import java.util.Calendar;

/**
 * &lt;t:&gt; 动态生成时间文本
 */
public class TimeExpression implements IExpression {

    /**
     * 支持格式
     * <pre>
     *  &lt;t:600&gt; - 10分钟后的时间文本,等价于10m
     *  &lt;t:1Y1M1D&gt; - 1年1月1天后的时间文本
     *  &lt;t:1h1m1s&gt; - 1小时1分钟1秒后的时间文本
     * </pre>
     *
     * @param key    处理的key
     * @param space 缓存
     * @return
     */
    @Override
    public String replace(String key, ExpressionSpace space) {
        if (StringUtils.isNumeric(key)) {
            return SXItem.getSdf().get().format(System.currentTimeMillis() + Long.parseLong(key) * 1000);
        }
        Calendar calendar = Calendar.getInstance();
        int num = 0;
        for (char c : key.toCharArray()) {
            switch (c) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    num = c - 48 + num * 10;
                    continue;
                case 'Y':
                case 'y':
                    calendar.add(Calendar.YEAR, num);
                    break;
                case 'M':
                    calendar.add(Calendar.MONTH, num);
                    break;
                case 'D':
                case 'd':
                    calendar.add(Calendar.DATE, num);
                    break;
                case 'H':
                case 'h':
                    calendar.add(Calendar.HOUR_OF_DAY, num);
                    break;
                case 'm':
                    calendar.add(Calendar.MINUTE, num);
                    break;
                case 'S':
                case 's':
                    calendar.add(Calendar.SECOND, num);
                    break;
                default:
                    continue;
            }
            num = 0;
        }
        return SXItem.getSdf().get().format(calendar.getTime());
    }
}
