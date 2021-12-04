package github.saukiya.sxitem.nms;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public abstract class TagListBase<T extends TagBase> extends ArrayList<T> implements TagBase<List<?>> {

    public TagListBase(Collection<T> tagBases) {
        super(tagBases);
    }

    public String getToStringPrefix() {
        return "";
    }

    @Override
    public List<?> getValue() {
        return this.stream().map(TagBase::getValue).collect(Collectors.toList());
    }

    @Override
    public Object[] toArray() {
        return this.stream().map(TagBase::getValue).toArray();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(getToStringPrefix());
        for (int i = 0; i < this.size(); i++) {
            if (i != 0) {
                sb.append(',');
            }
            sb.append(this.get(i));
        }
        sb.append(']');
        return sb.toString();
    }
}
