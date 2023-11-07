package github.saukiya.sxitem.data.random.randoms;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.IRandom;
import github.saukiya.sxitem.data.random.RandomDocker;

import java.util.Arrays;
import java.util.UUID;

public class UUIDRandom implements IRandom {

    @Override
    public String replace(String key, RandomDocker docker) {
        if (key.equals("random")) {
            return UUID.randomUUID().toString();
        }
        return UUID.nameUUIDFromBytes(key.getBytes()).toString();
    }
}
