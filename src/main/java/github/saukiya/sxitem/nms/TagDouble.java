package github.saukiya.sxitem.nms;

public class TagDouble extends TagNumber<Double> {
    private final double value;

    public TagDouble(double value) {
        this.value = value;
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue() + "d";
    }
}
