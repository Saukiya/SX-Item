package github.saukiya.sxitem.nms;

public class TagByte extends TagNumber<Byte> {
    private final byte value;

    public TagByte(byte value) {
        this.value = value;
    }

    @Override
    public Byte getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue() + "B";
    }
}
