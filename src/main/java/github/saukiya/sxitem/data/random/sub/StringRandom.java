package github.saukiya.sxitem.data.random.sub;

import github.saukiya.sxitem.data.random.IRandom;
import github.saukiya.sxitem.data.random.RandomStringManager;

public class StringRandom implements IRandom {

    @Override
    public void replace(char prefix, String key, RandomStringManager.RandomData data) {
        if (prefix == 'l') {
            String str = data.get(String.class, key);
        }
    }
}
