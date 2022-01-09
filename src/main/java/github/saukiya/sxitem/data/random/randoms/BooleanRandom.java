package github.saukiya.sxitem.data.random.randoms;

import github.saukiya.sxitem.data.random.IRandom;
import github.saukiya.sxitem.data.random.RandomDocker;

public class BooleanRandom implements IRandom {

    @Override
    public String replace(String key, RandomDocker docker) {
        String comStr = null;
        for (String str : key.split(":")) {
            if (comStr == null) comStr = str;
            else if (str.equals(comStr)) return "";
        }
        return null;
    }
}
