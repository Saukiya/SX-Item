package github.saukiya.tools.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class TagString implements TagBase<String> {

    protected static final TagType.Method typeMethod = new TagType.Method() {
        @Override
        public TagString readTagBase(DataInput dataInput, int depth) throws IOException {
            return new TagString(dataInput.readUTF());
        }

        @Override
        public TagString toTag(Object object) {
            return object instanceof String ? new TagString((String) object) : null;
        }
    };

    private final String value;

    public TagString(String value) {
        this.value = value;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(value);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public TagType getTypeId() {
        return TagType.STRING;
    }

    @Override
    public String toString() {
        return '"' + getValue() + '"';
    }
}
