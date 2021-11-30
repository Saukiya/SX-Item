package github.saukiya.sxitem.nms;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class TagShort extends TagNumber<Short> {

    protected static final TagType.Method<TagShort> typeMethod = new TagType.Method<TagShort>() {
        @Override
        public TagShort readTagBase(DataInput dataInput, int depth) throws IOException {
            return new TagShort(dataInput.readShort());
        }
    };

    private final short value;

    public TagShort(short value) {
        this.value = value;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeShort(value);
    }

    @Override
    public Short getValue() {
        return value;
    }

    @Override
    public TagType getTypeId() {
        return TagType.SHORT;
    }

    @Override
    public String toString() {
        return getValue() + "s";
    }
}
