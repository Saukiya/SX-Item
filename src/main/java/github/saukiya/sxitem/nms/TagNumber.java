package github.saukiya.sxitem.nms;

public abstract class TagNumber<T extends Number> extends Number implements TagBase<T> {

    @Override
    public int intValue() {
        return getValue().intValue();
    }

    @Override
    public long longValue() {
        return getValue().longValue();
    }

    @Override
    public float floatValue() {
        return getValue().floatValue();
    }

    @Override
    public double doubleValue() {
        return getValue().doubleValue();
    }
}