package github.saukiya.tools.base;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class Tuple<A, B> {
    private final A a;
    private final B b;

    public A a() {
        return this.a;
    }

    public B b() {
        return this.b;
    }

    public static <A, B> Tuple<A, B> of(A var0, B var1) {
        return new Tuple<>(var0, var1);
    }
}