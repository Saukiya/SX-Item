package github.saukiya.util.common;

import github.saukiya.util.base.Tuple;

import java.util.LinkedList;

public class TimingsUtil {

    private final LinkedList<Tuple<String, Long>> timings = new LinkedList<>();
    private final long startTime = System.nanoTime();

    public void dot() {
        dot(null);
    }

    public void dot(String desc) {
        long time = System.nanoTime();
        timings.add(Tuple.of(desc, time));
    }

    public void print() {
        print("");
    }

    public void print(String name) {
        System.out.println("timings: " + name);
        long lastValue = startTime;
        for (Tuple<String, Long> timing : timings) {
            if (timing.a() != null) {
                System.out.println("-\t" + timing.a() + ": " + ((timing.b() - lastValue) / 1000000D) + "ms");
            }
            lastValue = timing.b();
        }
        System.out.println("--\t*: " + ((lastValue - startTime) / 1000000D) + "ms\n");
    }
}
