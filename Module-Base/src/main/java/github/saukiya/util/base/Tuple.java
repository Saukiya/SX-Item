package github.saukiya.util.base;

import lombok.ToString;

@ToString
public class Tuple<A, B> {
    private A a;
    private B b;

    public Tuple(A var0, B var1) {
        this.a = var0;
        this.b = var1;
    }

    public A a() {
        return this.a;
    }

    public void a(A var0) {
        this.a = var0;
    }

    public B b() {
        return this.b;
    }

    public void b(B var0) {
        this.b = var0;
    }

    public static <A, B> Tuple<A, B> of(A var0, B var1) {
        return new Tuple<>(var0, var1);
    }
}