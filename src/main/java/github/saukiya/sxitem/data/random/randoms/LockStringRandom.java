package github.saukiya.sxitem.data.random.randoms;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.IRandom;
import github.saukiya.sxitem.data.random.RandomDocker;

/**
 * &lt;l:&gt; 获取并锁定一个随机文本
 */
public class LockStringRandom implements IRandom {


    /**
     * 支持格式
     * <pre>
     *  &lt;l:key&gt; - 从key集合中随机抽取一个值并锁定
     *  &lt;l:key#AAA:BBB&gt; - 从AAA/BBB中随机抽取一个值并锁定
     * </pre>
     *
     * @param key    处理的key
     * @param docker 缓存
     * @return
     */
    @Override
    public String replace(String key, RandomDocker docker) {
        int indexOf = key.indexOf('#');
        String temp = indexOf == -1 ? null : key.substring(indexOf + 1);
        String finalKey = indexOf == -1 ? key : key.substring(0, indexOf);

        return docker.getLockMap().computeIfAbsent(finalKey, k -> {
            String value;
            if (temp != null) {
                value = docker.getOtherMap().get(finalKey);
                if (value == null) {
                    value = randomArray(temp.split(":"));
                }
            } else {
                value = docker.random(finalKey);
            }
            return docker.replace(value);
        });
    }

    public static String randomArray(String[] array) {
        return array[SXItem.getRandom().nextInt(array.length)];
    }
}
