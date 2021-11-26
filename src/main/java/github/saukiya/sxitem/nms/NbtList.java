package github.saukiya.sxitem.nms;

import java.util.AbstractList;

public abstract class NbtList<T extends NbtBase> extends AbstractList<T> implements NbtBase {

    public abstract T set(int var1, T var2);

    public abstract void add(int var1, T var2);

    public abstract T remove(int var1);
}
