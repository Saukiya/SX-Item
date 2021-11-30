package github.saukiya.sxitem.nms;

import java.io.DataOutput;
import java.io.IOException;

public interface TagBase<T> {

    void write(DataOutput dataOutput) throws IOException;

    T getValue();

    TagType getTypeId();
}
