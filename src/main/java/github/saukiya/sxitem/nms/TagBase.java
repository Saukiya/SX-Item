package github.saukiya.sxitem.nms;

public interface TagBase<T> {

    T getValue();

    static <V extends TagBase> V parse(Object object) {
        TagBase tagBase = null;
        if (object instanceof Byte) {
            tagBase = new TagByte((Byte) object);
        } else if (object instanceof Short) {
            tagBase = new TagShort((Short) object);
        } else if (object instanceof Integer) {
            tagBase = new TagInt((Integer) object);
        } else if (object instanceof Float) {
            tagBase = new TagFloat((Float) object);
        } else if (object instanceof Double) {
            tagBase = new TagDouble((Double) object);
        } else if (object instanceof Long) {
            tagBase = new TagLong((Long) object);
        } else if (object instanceof String) {
            tagBase = new TagString((String) object);
        }
        return (V) tagBase;
    }
}
