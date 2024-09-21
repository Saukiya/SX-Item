package github.saukiya.sxitem.data.random.randoms;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.IRandom;
import github.saukiya.sxitem.data.random.RandomDocker;
import github.saukiya.util.helper.PlaceholderHelper;

import java.util.Objects;

public class LockStringRandom implements IRandom {

    @Override
    public String replace(String key, RandomDocker docker) {
        String value;
        if (key.contains("#")) {
            String[] temp = key.substring(key.indexOf("#") + 1).split(":");
            String finalKey = key = key.substring(0, key.indexOf("#"));
            String tempValue = docker.getOtherList().stream().map(map -> map.get(finalKey)).filter(Objects::nonNull).findFirst().orElse(null);
            value = PlaceholderHelper.setPlaceholders(docker.getPlayer(), docker.replace(tempValue != null ? tempValue : temp[SXItem.getRandom().nextInt(temp.length)]));
        } else {
            value = PlaceholderHelper.setPlaceholders(docker.getPlayer(), docker.replace(docker.random(key)));
        }
        return docker.getLockMap() == null ? value : docker.getLockMap().computeIfAbsent(key, k -> value);
    }
}
