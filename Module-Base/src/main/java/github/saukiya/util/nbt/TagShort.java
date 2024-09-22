package github.saukiya.util.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class TagShort extends TagNumber<Short> {

    protected static final TagType.Method typeMethod = new TagType.Method() {
        @Override
        public TagShort readTagBase(DataInput dataInput, int depth) throws IOException {
            return new TagShort(dataInput.readShort());
        }

        @Override
        public TagShort toTag(Object object) {
            return object instanceof Short ? new TagShort((Short) object) : null;
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
