package github.saukiya.sxitem.nms;

import java.io.DataOutput;
import java.io.IOException;

public class TagLong extends TagNumber<Long> {

    protected static final TagType.Method<TagLong> typeMethod = (dataInput, depth) -> new TagLong(dataInput.readLong());

    private final long value;

    public TagLong(long value) {
        this.value = value;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeLong(value);
    }

    @Override
    public Long getValue() {
        return value;
    }

    @Override
    public TagType getTypeId() {
        return TagType.LONG;
    }

    @Override
    public String toString() {
        return getValue() + "L";
    }
}
