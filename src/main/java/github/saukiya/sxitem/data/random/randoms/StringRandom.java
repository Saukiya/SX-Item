package github.saukiya.sxitem.data.random.randoms;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.IRandom;
import github.saukiya.sxitem.data.random.RandomDocker;
import github.saukiya.util.helper.PlaceholderHelper;

/**
 * &lt;s:&gt; 获取一个随机文本
 */
public class StringRandom implements IRandom {

    /**
     * 支持格式
     * <pre>
     *  &lt;s:key&gt; - 从key集合中随机抽取一个值
     *  &lt;s:AAA:BBB:CCC&gt; - 从AAA/BBB/CCC中随机抽取一个值
     * </pre>
     * 
     * @param key    处理的key
     * @param docker 缓存
     * @return
     */
    @Override
    public String replace(String key, RandomDocker docker) {
        if (key.indexOf(':') >= 0) {
            String[] temp = key.split(":");
            return PlaceholderHelper.setPlaceholders(docker.getPlayer(), temp[SXItem.getRandom().nextInt(temp.length)]);
        }
        return PlaceholderHelper.setPlaceholders(docker.getPlayer(), docker.random(key));
    }
}
