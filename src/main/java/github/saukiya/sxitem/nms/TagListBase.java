package github.saukiya.sxitem.nms;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class TagListBase<T extends TagBase> extends ArrayList<T> implements TagBase<List<?>> {

    @Override
    public List<?> getValue() {
        return this.stream().map(TagBase::getValue).collect(Collectors.toList());
    }

    @Override
    public Object[] toArray() {
        return this.stream().map(TagBase::getValue).toArray();
    }
}
