package github.saukiya.sxitem.nms;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class TagDouble extends TagNumber<Double> {

    protected static final TagType.Method typeMethod = new TagType.Method() {
        @Override
        public TagDouble readTagBase(DataInput dataInput, int depth) throws IOException {
            return new TagDouble(dataInput.readDouble());
        }

        @Override
        public TagDouble toTag(Object object) {
            return object instanceof Double ? new TagDouble((Double) object) : null;
        }
    };

    private final double value;

    public TagDouble(double value) {
        this.value = value;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeDouble(value);
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public TagType getTypeId() {
        return TagType.DOUBLE;
    }

    @Override
    public String toString() {
        return getValue() + "d";
    }
}
