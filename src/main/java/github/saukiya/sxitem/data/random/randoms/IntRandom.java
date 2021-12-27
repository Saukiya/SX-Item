package github.saukiya.sxitem.data.random.randoms;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.IRandom;
import github.saukiya.sxitem.data.random.RandomDocker;

import java.util.Arrays;

public class IntRandom implements IRandom {

    @Override
    public String replace(String key, RandomDocker docker) {
        String[] strSplit = key.split("_");
        if (strSplit.length > 1) {
            int[] ints = {Integer.parseInt(strSplit[0]), Integer.parseInt(strSplit[1])};
            Arrays.sort(ints);
            return String.valueOf(SXItem.getRandom().nextInt(1 + ints[1] - ints[0]) + ints[0]);
        }
        SXItem.getInst().getLogger().warning("IntRandom Error: " + key);
        return null;
    }
}