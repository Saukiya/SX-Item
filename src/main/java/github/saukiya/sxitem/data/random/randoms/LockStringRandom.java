package github.saukiya.sxitem.data.random.randoms;

import github.saukiya.sxitem.data.random.IRandom;
import github.saukiya.sxitem.data.random.RandomDocker;

public class LockStringRandom implements IRandom {

    @Override
    public String replace(String key, RandomDocker docker) {
        if (docker.getLockMap() == null) return docker.random(key);
        // 先赋值, 防止出现computeIfAbsent并发问题
        String value = docker.replace(docker.random(key));
        return docker.getLockMap().computeIfAbsent(key, k -> value);
    }
}
