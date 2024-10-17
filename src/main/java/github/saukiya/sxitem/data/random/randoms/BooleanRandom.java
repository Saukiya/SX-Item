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
            switch (c) {
                case ',':
                case ':':
                    if (index == check) {
                        return "";
                    }
                    index = 0;
                    break;
                default:
                    if (index != -1 && chars[index++] != c) {
                        index = -1;
                    }
                    break;
            }
        }
        return index == check ? "" : null;
    }
}
