package github.saukiya.sxitem.nms;

import java.io.DataOutput;
import java.io.IOException;

public class TagDouble extends TagNumber<Double> {

    protected static final TagType.Method<TagDouble> typeMethod = (dataInput, depth) -> new TagDouble(dataInput.readDouble());

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
