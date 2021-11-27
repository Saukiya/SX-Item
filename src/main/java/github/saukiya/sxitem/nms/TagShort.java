package github.saukiya.sxitem.nms;

public class TagShort extends TagNumber<Short> {
    private final short value;

    public TagShort(short value) {
        this.value = value;
    }

    @Override
    public Short getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue() + "s";
    }
}
