package github.saukiya.sxitem.data.random.sub;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.IRandom;
import github.saukiya.sxitem.data.random.RandomDocker;

public class TimeRandom implements IRandom {

    @Override
    public String replace(String key, RandomDocker docker) {
        String addTime = key + "000";
        long time = System.currentTimeMillis() + Long.parseLong(addTime);
        return SXItem.getSdf().get().format(time);
    }
}
