package github.saukiya.tools.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class TagInt extends TagNumber<Integer> {

    protected static final TagType.Method typeMethod = new TagType.Method() {
        @Override
        public TagInt readTagBase(DataInput dataInput, int depth) throws IOException {
            return new TagInt(dataInput.readInt());
        }

        @Override
        public TagInt toTag(Object object) {
            return object instanceof Integer ? new TagInt((Integer) object) : null;
        }
    };

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
