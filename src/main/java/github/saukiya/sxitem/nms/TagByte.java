package github.saukiya.sxitem.nms;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class TagByte extends TagNumber<Byte> {

    protected static final TagByte TRUE = new TagByte((byte) 1);
    protected static final TagByte FALSE = new TagByte((byte) 0);
    protected static final TagType.Method typeMethod = new TagType.Method() {
        @Override
        public TagByte readTagBase(DataInput dataInput, int depth) throws IOException {
            return new TagByte(dataInput.readByte());
        }

        @Override
        public TagByte toTag(Object object) {
            if (object instanceof Boolean) return ((Boolean) object) ? TRUE : FALSE;
            return object instanceof Byte ? new TagByte((Byte) object) : null;
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
        return getValue() + "b";
    }
}
