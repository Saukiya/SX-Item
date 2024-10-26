package github.saukiya.tools.util;

import github.saukiya.tools.base.Tuple;

import java.util.LinkedList;

/**
 * 耗时检查工具 - 无意义
 */
public class TimingsUtil {

    private final LinkedList<Tuple<String, Long>> timings = new LinkedList<>();
    private final long startTime = System.nanoTime();

    /**
     * 重置耗时
     */
    public void dot() {
        dot(null);
    }

    /**
     * 记录耗时
     *
     * @param desc 描述
     */
    public void dot(String desc) {
        long time = System.nanoTime();
        timings.add(Tuple.of(desc, time));
    }

    /**
     * 输出耗时
     */
    public void print() {
        print("");
    }

    /**
     * 输出耗时
     * @param name 名称
     */
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
