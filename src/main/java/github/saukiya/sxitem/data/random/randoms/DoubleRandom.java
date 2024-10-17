package github.saukiya.sxitem.data.random.randoms;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.IRandom;
import github.saukiya.sxitem.data.random.RandomDocker;

/**
 * &lt;d:&gt; 创建一个随机浮点数
 */
public class DoubleRandom implements IRandom {

    /**
     * 支持格式
     * <pre>
     *  &lt;d:1.5_2.5&gt; - 从1.5-2.5随机抽一个小数
     *  &lt;d:100_10&gt; - 从10-100随机抽一个小数(不分顺序)
     * </pre>
     *
     * @param key    处理的key
     * @param docker 缓存
     * @return
     */
    @Override
    public String replace(String key, RandomDocker docker) {
        int index = key.indexOf('_');
        if (index == -1) return key;
        double min = Double.parseDouble(key.substring(0, index));
        double max = Double.parseDouble(key.substring(index + 1));
        double result = SXItem.getRandom().nextDouble() * (max - min) + min;
        return String.valueOf(Math.round(result * 100) / 100D);
    }
}
