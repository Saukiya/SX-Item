package github.saukiya.sxitem.data.random;

public interface IRandom {
    /**
     * 替换字符串内容
     *
     * @param key    处理的key
     * @param docker 缓存
     * @return 返回为空则删除此行
     */
    String replace(String key, RandomDocker docker);

}
