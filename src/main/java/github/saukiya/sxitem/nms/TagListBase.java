package github.saukiya.sxitem.nms;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@NoArgsConstructor
public abstract class TagListBase<T, V> extends ArrayList<T> implements TagBase<V> {

    public TagListBase(Collection<T> collection) {
        super(collection);
    }

    public String getToStringPrefix() {
        return "";
    }

    public String getToStringSuffix() {
        return "";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(getToStringPrefix());
        for (int i = 0; i < this.size(); i++) {
            if (i != 0) {
                sb.append(',');
            }
            sb.append(this.get(i)).append(getToStringSuffix());
        }
        sb.append(']');
        return sb.toString();
    }
}
