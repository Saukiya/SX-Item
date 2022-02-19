package github.saukiya.sxitem.data.random.randoms;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.IRandom;
import github.saukiya.sxitem.data.random.RandomDocker;

import java.util.Arrays;

public class IntRandom implements IRandom {

    @Override
    public String replace(String key, RandomDocker docker) {
        int[] ints = Arrays.stream(key.split("_")).mapToInt(Integer::parseInt).sorted().toArray();
        return String.valueOf(SXItem.getRandom().nextInt(1 + ints[1] - ints[0]) + ints[0]);
    }
}
