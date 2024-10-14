package github.saukiya.test;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.RandomDocker;
import github.saukiya.sxitem.util.Config;
import github.saukiya.util.TestBenchmark;
import github.saukiya.util.helper.PlaceholderHelper;
import lombok.val;
import org.bukkit.configuration.file.YamlConfiguration;
import org.openjdk.jmh.annotations.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@State(Scope.Thread)
public class TestRandom {

    static final TestRandom inst = new TestRandom();

    static final Pattern LONG_PATTERN = Pattern.compile("\\d+");

    final RandomDocker docker = new RandomDocker();

    public static void main(String[] args) throws Exception {
//        TestBenchmark.run("time1", "time2");
//        TestBenchmark.run("double1", "double2");
//        TestBenchmark.run("int1", "int2");
//        TestBenchmark.run("lock1", "lock2");
        TestBenchmark.run();
    }

    public void setup() throws Exception {
        setupInvocation();
        setupTrial();
    }

    @Setup(Level.Invocation)
    public void setupInvocation() {
        docker.getLockMap().clear();
    }

    @Setup(Level.Trial)
    public void setupTrial() throws Exception {
        YamlConfiguration config = new YamlConfiguration();
        config.set(Config.TIME_FORMAT, "yyyy/MM/dd HH:mm");
        val temp = Config.class.getDeclaredField("config");
        temp.setAccessible(true);
        temp.set(null, config);
    }

    @State(Scope.Thread)
    public static class TimeState {
        @Param({"20Y12M31D23h59m59s", "12345"})
        String key = "20Y12M31D23h59m59s";
    }

    @Benchmark
    public String time1(TimeState state) {
        val key = state.key;
        if (LONG_PATTERN.matcher(key).matches()) {
            return SXItem.getSdf().get().format(System.currentTimeMillis() + Long.parseLong(key) * 1000);
        } else {
            Calendar calendar = Calendar.getInstance();
            int num = 0;
            for (int i = 0, length = key.length(); i < length; i++) {
                char c = key.charAt(i);
                switch (c) {
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        num = num * 10 + c - '0';
                        break;
                    case 'Y':
                    case 'y':
                        calendar.add(Calendar.YEAR, num);
                        num = 0;
                        break;
                    case 'M':
                        calendar.add(Calendar.MONTH, num);
                        num = 0;
                        break;
                    case 'D':
                    case 'd':
                        calendar.add(Calendar.DATE, num);
                        num = 0;
                        break;
                    case 'H':
                    case 'h':
                        calendar.add(Calendar.HOUR_OF_DAY, num);
                        num = 0;
                        break;
                    case 'm':
                        calendar.add(Calendar.MINUTE, num);
                        num = 0;
                        break;
                    case 'S':
                    case 's':
                        calendar.add(Calendar.SECOND, num);
                        num = 0;
                        break;
                }
            }
            return SXItem.getSdf().get().format(calendar.getTimeInMillis());
        }
    }

    @Benchmark
    public String time2(TimeState state) {
        val key = state.key;
        if (LONG_PATTERN.matcher(key).matches()) {
            return SXItem.getSdf().get().format(System.currentTimeMillis() + Long.parseLong(key) * 1000);
        } else {
            Calendar calendar = Calendar.getInstance();
            int num = 0;
            for (int i = 0, length = key.length(); i < length; i++) {
                char c = key.charAt(i);
                if (Character.isDigit(c)) {
                    num = num * 10 + (c - '0');
                } else {
                    updateCalendar(calendar, num, c);
                    num = 0;
                }
            }
            return SXItem.getSdf().get().format(calendar.getTimeInMillis());
        }
    }

    private void updateCalendar(Calendar calendar, int num, char unit) {
        switch (unit) {
            case 'Y':
            case 'y':
                calendar.add(Calendar.YEAR, num);
                break;
            case 'M':
                calendar.add(Calendar.MONTH, num);
                break;
            case 'D':
            case 'd':
                calendar.add(Calendar.DATE, num);
                break;
            case 'H':
            case 'h':
                calendar.add(Calendar.HOUR_OF_DAY, num);
                break;
            case 'm':
                calendar.add(Calendar.MINUTE, num);
                break;
            case 'S':
            case 's':
                calendar.add(Calendar.SECOND, num);
                break;
        }
    }

    @State(Scope.Thread)
    public static class DoubleState {
        @Param({"1.5_2.5", "200_100"})
        String key = "1.5_2.5";
    }

    @Benchmark
    public String double1(DoubleState state) {
        val key = state.key;
        String[] strSplit = key.split("_");
        if (strSplit.length == 1) return key;
        double[] doubles = {Double.parseDouble(strSplit[0]), Double.parseDouble(strSplit[1])};
        return SXItem.getDf().format(SXItem.getRandom().nextDouble() * (doubles[1] - doubles[0]) + doubles[0]);
    }

    @Benchmark
    public String double2(DoubleState state) {
        val key = state.key;
        int index = key.indexOf('_');
        if (index == -1) return key;
        double min = Double.parseDouble(key.substring(0, index));
        double max = Double.parseDouble(key.substring(index + 1));
        return SXItem.getDf().format(SXItem.getRandom().nextDouble() * (max - min) + min);
    }

    @State(Scope.Thread)
    public static class IntState {
        @Param({"1_2", "200_100"})
        String key = "1_2";
    }

    @Benchmark
    public String int1(IntState state) {
        val key = state.key;
        int[] ints = Arrays.stream(key.split("_")).mapToInt(Integer::parseInt).sorted().toArray();
        return String.valueOf(SXItem.getRandom().nextInt(1 + ints[1] - ints[0]) + ints[0]);
    }

    @Benchmark
    public String int2(IntState state) {
        val key = state.key;
        val index = key.indexOf('_');
        int min = Integer.parseInt(key.substring(0, index));
        int max = Integer.parseInt(key.substring(index + 1));
        return String.valueOf(SXItem.getRandom().nextInt(1 + Math.abs(max - min)) + Math.min(max, min));
    }

    @State(Scope.Thread)
    public static class LockState {
        @Param({"KEY#100,200,300"})
        String key = "KEY#100,200,300";
    }

    @Benchmark
    public String lock1(LockState state) {
        String key = state.key;
        String value;
        if (key.contains("#")) {
            String[] temp = key.substring(key.indexOf("#") + 1).split(",");
            String finalKey = key = key.substring(0, key.indexOf("#"));
            String tempValue = docker.getOtherList().stream().map(map -> map.get(finalKey)).filter(Objects::nonNull).findFirst().orElse(null);
            value = PlaceholderHelper.setPlaceholders(docker.getPlayer(), docker.replace(tempValue != null ? tempValue : temp[SXItem.getRandom().nextInt(temp.length)]));
        } else {
            value = PlaceholderHelper.setPlaceholders(docker.getPlayer(), docker.replace(docker.random(key)));
        }
        return docker.getLockMap() == null ? value : docker.getLockMap().computeIfAbsent(key, k -> value);
    }

    @Benchmark
    public String lock2(LockState state) {
        String key = state.key;
        String value = null;
        String temp = null;
        int indexOf = key.indexOf('#');

        if (indexOf >= 0) {
            temp = key.substring(indexOf + 1);
            key = key.substring(0, indexOf);
        }

        if (docker.getLockMap() != null && (value = docker.getLockMap().get(key)) != null) {
            return value;
        }

        if (temp != null) {
            for (Map<String, String> map : docker.getOtherList()) {
                value = map.get(key);
                if (value != null) break;
            }
            if (value == null) {
                value = randomArray(temp.split(","));
            }
        } else {
            value = docker.replace(docker.random(key));
        }

        if (docker.getLockMap() != null) {
            docker.getLockMap().put(key, value);
        }
        return value;
    }

    public String randomArray(String[] array) {
        return array[SXItem.getRandom().nextInt(array.length)];
    }
}
