package github.saukiya.sxitem.data.random.randoms;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.IRandom;
import github.saukiya.sxitem.data.random.RandomDocker;

import java.util.Calendar;
import java.util.regex.Pattern;

public class TimeRandom implements IRandom {

    static final Pattern PATTERN = Pattern.compile("\\d+");

    /**
     * 支持格式
     * <pre>
     *  &lt;t:600&gt; - 10分钟后的时间文本,等价于10m
     *  &lt;t:1Y1M1D&gt; - 1年1月1天后的时间文本
     *  &lt;t:1h1m1s&gt; - 1小时1分钟1秒后的时间文本
     * </pre>
     *
     * @param key    处理的key
     * @param docker 缓存
     * @return
     */
    @Override
    public String replace(String key, RandomDocker docker) {
        if (PATTERN.matcher(key).matches()) {
            return SXItem.getSdf().get().format(System.currentTimeMillis() + Long.parseLong(key) * 1000);
        } else {
            Calendar calendar = Calendar.getInstance();
            int num = 0;
            for (int i = 0, length = key.length(); i < length; i++) {
                char c = key.charAt(i);
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
                        num = num * 10 + c - 48;
                        break;
                    case 'Y':
                    case 'y':
                        calendar.add(Calendar.YEAR, num);
                        num = 0;
                        break;
                    case 'M':
                        calendar.add(Calendar.MONTH, num);
                        num = 0;
                        break;
                    case 'D':
                    case 'd':
                        calendar.add(Calendar.DATE, num);
                        num = 0;
                        break;
                    case 'H':
                    case 'h':
                        calendar.add(Calendar.HOUR_OF_DAY, num);
                        num = 0;
                        break;
                    case 'm':
                        calendar.add(Calendar.MINUTE, num);
                        num = 0;
                        break;
                    case 'S':
                    case 's':
                        calendar.add(Calendar.SECOND, num);
                        num = 0;
                        break;
                }
            }
            return SXItem.getSdf().get().format(calendar.getTimeInMillis());
        }
    }
}
