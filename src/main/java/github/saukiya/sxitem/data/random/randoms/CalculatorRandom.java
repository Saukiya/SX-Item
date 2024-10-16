package github.saukiya.sxitem.data.random.randoms;

import github.saukiya.sxitem.data.random.IRandom;
import github.saukiya.sxitem.data.random.RandomDocker;
import github.saukiya.util.common.CalculatorUtil;

/**
 * 计算器处理
 */
public class CalculatorRandom implements IRandom {

    /**
     * 支持格式
     * <pre>
     *  &lt;c:100 / 3&gt; - 计算 100 / 3 并返回小数(33.33)
     *  &lt;c:int -5 * &lt;d:0.8_1.2&gt;&gt; - 计算 5 * (0.8~1.2) 并返回整数(4/5/6)
     *  &lt;c:&lt;i:1_20&gt; * &lt;l:level#1.5,2.5,3.5&gt;&gt; - 结合其他随机模式, 套公式返回小数
     * </pre>
     *
     * @param key    处理的key
     * @param docker 缓存
     * @return
     */
    @Override
    public String replace(String key, RandomDocker docker) {
        if (key.startsWith("int")) {
            double result = CalculatorUtil.calculator(key.substring(3));
            return String.valueOf(Math.round(result));
        }
        double result = CalculatorUtil.calculator(key);
        return String.valueOf(Math.round(result * 100) / 100D);
    }
}
