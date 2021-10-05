package github.saukiya.sxitem.data.random.sub;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.IRandom;
import github.saukiya.sxitem.data.random.RandomDocker;

import java.util.Arrays;

public class DoubleRandom implements IRandom {

    @Override
    public String replace(String key, RandomDocker docker) {
        String[] strSplit = key.split("_");
        if (strSplit.length > 1) {
            double[] doubles = {Double.parseDouble(strSplit[0]), Double.parseDouble(strSplit[1])};
            Arrays.sort(doubles);
            return SXItem.getDf().format(doubles[0] != doubles[1] ? SXItem.getRandom().nextDouble() * (doubles[1] - doubles[0]) + doubles[0] : doubles[0]);
        }
        return null;
    }
}
