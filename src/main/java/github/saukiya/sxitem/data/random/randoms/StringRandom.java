package github.saukiya.sxitem.data.random.randoms;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.IRandom;
import github.saukiya.sxitem.data.random.RandomDocker;
import github.saukiya.util.helper.PlaceholderHelper;

public class StringRandom implements IRandom {

    @Override
    public String replace(String key, RandomDocker docker) {
        if (key.contains(":")) {
            String[] temp = key.split(":");
            return PlaceholderHelper.setPlaceholders(docker.getPlayer(), temp[SXItem.getRandom().nextInt(temp.length)]);
        }
        return PlaceholderHelper.setPlaceholders(docker.getPlayer(), docker.random(key));
    }
}
