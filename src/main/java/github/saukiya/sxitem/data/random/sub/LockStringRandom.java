package github.saukiya.sxitem.data.random.sub;

import github.saukiya.sxitem.data.random.IRandom;
import github.saukiya.sxitem.data.random.RandomDocker;

public class LockStringRandom implements IRandom {

    @Override
    public String replace(String key, RandomDocker docker) {
        return docker.getLockMap().computeIfAbsent(key, k -> docker.get(key));
    }
}
