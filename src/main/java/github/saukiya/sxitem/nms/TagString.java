package github.saukiya.sxitem.nms;

import java.io.DataOutput;
import java.io.IOException;

public class TagString implements TagBase<String> {

    protected static final TagType.Method<TagString> typeMethod = (dataInput, depth) -> new TagString(dataInput.readUTF());

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
