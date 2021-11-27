package github.saukiya.sxitem.nms;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class TagCompound extends HashMap<String, TagBase> implements TagBase<HashMap<String,TagBase>> {

    private static final Pattern a = Pattern.compile("[A-Za-z0-9._+-]+");

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
        for (Iterator<String> iterator = list.iterator(); iterator.hasNext(); sb.append(temp).append(":").append(get(temp))) {
            temp = iterator.next();
            if (sb.length() != 1) {
                sb.append(',');
            }
        }
        sb.append('}');
        return sb.toString();
    }
}
