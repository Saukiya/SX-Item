package github.saukiya.sxitem.data.random.randoms;

import github.saukiya.sxitem.data.random.IRandom;
import github.saukiya.sxitem.data.random.RandomDocker;

/**
 * &lt;b:&gt; 判断两个值或多个值是否相同
 */
public class BooleanRandom implements IRandom {

    /**
     * 支持格式
     * <pre>
     *  &lt;b:AA:AA&gt; 判断AA是否与AA相同, 相同则保留此行
     *  &lt;b:AA:BB:CC&gt; 判断AA是否与BB,CC相同, 不同则删除此行
     *  &lt;b:AA#AA&gt; 与第一条逻辑相同
     *  &lt;b:AA#BB:CC&gt; 与第二条逻辑相同
     * </pre>
     *
     * @param key    处理的key
     * @param docker 缓存
     * @return
     */
    @Override
    public String replace(String key, RandomDocker docker) {
        char[] chars = key.toCharArray();
        int index = 0, check = 0, length = chars.length;

        for (int i = 0; i < length; i++) {
            switch (chars[i]) {
                case '#':
                case ':':
                    check = i;
                    break;
                default:
                    continue;
            }
            break;
        }
        if (check == 0) return null;
        for (int i = check + 1; i < length; i++) {
            char c = chars[i];
            if (c == ':') {
                if (index == check) {
                    return "";
                }
                index = 0;
            } else {
                if (index != -1 && chars[index++] != c) {
                    index = -1;
                }
            }
        }
        return index == check ? "" : null;
    }
}
