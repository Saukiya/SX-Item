package github.saukiya.sxitem.data.random.randoms;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.IRandom;
import github.saukiya.sxitem.data.random.RandomDocker;
import github.saukiya.util.helper.PlaceholderHelper;

public class StringRandom implements IRandom {

    /**
     * 支持格式
     * <pre>
     *  &lt;s:key&gt;
     *  &lt;s:AAA,BBB,CCC&gt;
     * </pre>
     *
     * @param key    处理的key
     * @param docker 缓存
     * @return
     */
    @Override
    public String replace(String key, RandomDocker docker) {
        if (key.indexOf(':') >= 0) {
            String[] temp = key.split(",");
            return PlaceholderHelper.setPlaceholders(docker.getPlayer(), temp[SXItem.getRandom().nextInt(temp.length)]);
        }
        return PlaceholderHelper.setPlaceholders(docker.getPlayer(), docker.random(key));
    }
}
