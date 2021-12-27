package github.saukiya.sxitem.data.random.randoms;

import github.saukiya.sxitem.data.random.IRandom;
import github.saukiya.sxitem.data.random.RandomDocker;

public class LockStringRandom implements IRandom {

    @Override
    public String replace(String key, RandomDocker docker) {
        if (docker.getLockMap() == null) return docker.random(key);
        //LockStringRandom需要直接获取字符串的最终结果，并保存到lockMap中
        docker.getLockLog().add(key);
        return docker.getLockMap().computeIfAbsent(key, (k) -> docker.replace(docker.random(key)));
    }
}
