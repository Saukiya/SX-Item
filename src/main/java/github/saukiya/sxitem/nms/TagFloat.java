package github.saukiya.sxitem.nms;

public class TagFloat extends TagNumber<Float> {
    private final float value;

    public TagFloat(float value) {
        this.value = value;
    }

    @Override
    public Float getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue() + "f";
    }
}
