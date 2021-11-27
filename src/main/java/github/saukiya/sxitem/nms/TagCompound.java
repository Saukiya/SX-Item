package github.saukiya.sxitem.nms;

import com.google.common.collect.Lists;

import java.util.*;

public class TagCompound extends HashMap<String, TagBase> implements TagBase<HashMap<String,TagBase>> {

    @Override
    public HashMap<String, TagBase> getValue() {
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        List<String> list = Lists.newArrayList(keySet());
        Collections.sort(list);

        String temp;
        for (Iterator<String> iterator = list.iterator(); iterator.hasNext(); sb.append(temp).append(":").append(String.valueOf(get(temp)))) {
            temp = iterator.next();
            if (sb.length() != 1) {
                sb.append(',');
            }
        }
        sb.append('}');
        return sb.toString();
    }

    public <T extends TagBase> T get(String key) {
        return (T) super.get(key);
    }
}