package github.saukiya.sxitem.nms;

import java.io.DataOutput;
import java.io.IOException;

public class TagInt extends TagNumber<Integer> {

    protected static final TagType.Method<TagInt> typeMethod = (dataInput, depth) -> new TagInt(dataInput.readInt());

    private final int value;

    public TagInt(int value) {
        this.value = value;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(value);
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public TagType getTypeId() {
        return TagType.INT;
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }
}
