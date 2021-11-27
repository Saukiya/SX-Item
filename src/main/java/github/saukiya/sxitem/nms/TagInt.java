package github.saukiya.sxitem.nms;

public class TagInt extends TagNumber<Integer> {
    private final int value;

    public TagInt(int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
