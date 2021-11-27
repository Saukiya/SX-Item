package github.saukiya.sxitem.nms;

public class TagLong extends TagNumber<Long> {
    private final long value;

    public TagLong(long value) {
        this.value = value;
    }

    @Override
    public Long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue() + "L";
    }
}
