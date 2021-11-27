package github.saukiya.sxitem.nms;

public class TagString implements TagBase<String> {
    private final String value;

    public TagString(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return '"' + getValue() + '"';
    }
}
