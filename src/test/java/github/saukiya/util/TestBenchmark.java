package github.saukiya.util;

import lombok.val;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.TimeUnit;

public class TestBenchmark {

    public static void run(String... args) throws RunnerException {
        val className = Thread.currentThread().getStackTrace()[2].getClassName();
        val builder = new OptionsBuilder()
                .measurementTime(TimeValue.seconds(1))
                .measurementIterations(4)
                .measurementBatchSize(4)
                .warmupTime(TimeValue.seconds(1))
                .warmupIterations(4)
                .warmupBatchSize(4)
                .timeUnit(TimeUnit.NANOSECONDS)
                .mode(Mode.AverageTime)
                .threads(2)
                .forks(1);
        if (args.length == 0) {
            builder.include(className);
        } else {
            for (String arg : args) {
                builder.include(className + '.' + arg);
            }
        }
        new Runner(builder.build()).run();
    }
}
