package github.saukiya.sxitem.data.random.randoms;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.IRandom;
import github.saukiya.sxitem.data.random.RandomDocker;

import java.util.Map;

public class LockStringRandom implements IRandom {

    public String separator = ":";
//    public String separator = ","; // TODO

    /**
     * 支持格式
     * <pre>
     *  &lt;l:key&gt; - 从key集合中随机抽取一个值并锁定
     *  &lt;l:key#AAA,BBB&gt; - 从AAA/BBB中随机抽取一个值并锁定
     * </pre>
     *
     * @param key    处理的key
     * @param docker 缓存
     * @return
     */
    @Override
    public String replace(String key, RandomDocker docker) {
        String value = null;
        String temp = null;
        int indexOf = key.indexOf('#');

        if (indexOf >= 0) {
            temp = key.substring(indexOf + 1);
            key = key.substring(0, indexOf);
        }

        if (docker.getLockMap() != null && (value = docker.getLockMap().get(key)) != null) {
            return value;
        }

        if (temp != null) {
            for (Map<String, String> map : docker.getOtherList()) {
                value = map.get(key);
                if (value != null) break;
            }
            if (value == null) {
                value = randomArray(temp.split(separator));
            }
        } else {
            value = docker.replace(docker.random(key));
        }

        if (docker.getLockMap() != null) {
            docker.getLockMap().put(key, value);
        }
        return value;
    }

    public static String randomArray(String[] array) {
        return array[SXItem.getRandom().nextInt(array.length)];
    }
}
