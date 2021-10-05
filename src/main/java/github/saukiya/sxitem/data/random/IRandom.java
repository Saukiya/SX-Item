package github.saukiya.sxitem.data.random;

public interface IRandom {
    /**
     * @param key    处理的key
     * @param docker 缓存
     * @return 返回为空则是不处理
     */
    String replace(String key, RandomDocker docker);
}
