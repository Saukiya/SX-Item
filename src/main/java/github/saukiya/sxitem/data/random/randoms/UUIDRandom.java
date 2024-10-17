package github.saukiya.sxitem.data.random.randoms;

import github.saukiya.sxitem.data.random.IRandom;
import github.saukiya.sxitem.data.random.RandomDocker;

import java.util.UUID;

/**
 * &lt;u:&gt; 返回一串uuid
 */
public class UUIDRandom implements IRandom {

    /**
     * 支持格式
     * <pre>
     *  &lt;u:random&gt; - 随机生成UUID
     *  &lt;u:xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx&gt; - 按照格式生成UUID
     * </pre>
     *
     * @param key    处理的key
     * @param docker 缓存
     * @return
     */
    @Override
    public String replace(String key, RandomDocker docker) {
        if (key.equals("random")) {
            return UUID.randomUUID().toString();
        }
        return UUID.nameUUIDFromBytes(key.getBytes()).toString();
    }
}
