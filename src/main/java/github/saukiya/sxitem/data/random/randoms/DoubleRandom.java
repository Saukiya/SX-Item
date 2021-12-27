package github.saukiya.sxitem.data.random.randoms;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.IRandom;
import github.saukiya.sxitem.data.random.RandomDocker;

public class DoubleRandom implements IRandom {

    @Override
    public String replace(String key, RandomDocker docker) {
        String[] strSplit = key.split("_");
        if (strSplit.length > 1) {
            double[] doubles = {Double.parseDouble(strSplit[0]), Double.parseDouble(strSplit[1])};
            return SXItem.getDf().format(SXItem.getRandom().nextDouble() * (doubles[1] - doubles[0]) + doubles[0]);
        }
        SXItem.getInst().getLogger().warning("DoubleRandom Error: " + key);
        return null;
    }
}
