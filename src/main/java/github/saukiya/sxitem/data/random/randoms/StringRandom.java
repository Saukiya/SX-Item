package github.saukiya.sxitem.data.random.randoms;

import github.saukiya.sxitem.data.random.IRandom;
import github.saukiya.sxitem.data.random.RandomDocker;
import github.saukiya.sxitem.helper.PlaceholderHelper;

public class StringRandom implements IRandom {

    @Override
    public String replace(String key, RandomDocker docker) {
        return PlaceholderHelper.setPlaceholders(docker.getPlayer(), docker.random(key));
    }
}
