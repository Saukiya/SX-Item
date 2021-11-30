package github.saukiya.sxitem.nms;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class TagByte extends TagNumber<Byte> {

    protected static final TagType.Method<TagByte> typeMethod = new TagType.Method<TagByte>() {
        @Override
        public TagByte readTagBase(DataInput dataInput, int depth) throws IOException {
            return new TagByte(dataInput.readByte());
        }
    };

    private final byte value;

    public TagByte(byte value) {
        this.value = value;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeByte(value);
    }

    @Override
    public Byte getValue() {
        return value;
    }

    @Override
    public TagType getTypeId() {
        return TagType.BYTE;
    }

    @Override
    public String toString() {
        return getValue() + "B";
    }
}
