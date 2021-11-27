package github.saukiya.sxitem.nms;

public class TagEnd implements TagBase {
    @Override
    public Object getValue() {
        return "END";
    }

    @Override
    public String toString() {
        return getValue().toString();
    }
}
