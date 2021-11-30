package github.saukiya.sxitem.nms;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class TagFloat extends TagNumber<Float> {

    protected static final TagType.Method<TagFloat> typeMethod = new TagType.Method<TagFloat>() {
        @Override
        public TagFloat readTagBase(DataInput dataInput, int depth) throws IOException {
            return new TagFloat(dataInput.readFloat());
        }
    };

    private final float value;

    public TagFloat(float value) {
        this.value = value;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeFloat(value);
    }

    @Override
    public Float getValue() {
        return value;
    }

    @Override
    public TagType getTypeId() {
        return TagType.FLOAT;
    }

    @Override
    public String toString() {
        return getValue() + "f";
    }
}
