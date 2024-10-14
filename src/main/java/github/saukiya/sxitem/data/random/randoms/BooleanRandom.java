package github.saukiya.sxitem.data.random.randoms;

import github.saukiya.sxitem.data.random.IRandom;
import github.saukiya.sxitem.data.random.RandomDocker;

public class BooleanRandom implements IRandom {

    /**
     * 支持格式
     * <pre>
     *  &lt;b:AA#AA,BB,CC,DD&gt; TODO 准备
     *  &lt;b:AA,BB,CC,DD&gt; TODO 删除
     * </pre>
     *
     * @param key    处理的key
     * @param docker 缓存
     * @return
     */
    @Override
    public String replace(String key, RandomDocker docker) {
        String comStr = null;
        for (String str : key.split(":")) {
            if (comStr == null) comStr = str;
            else if (str.equals(comStr)) return "";
        }
        return null;
    }
}
