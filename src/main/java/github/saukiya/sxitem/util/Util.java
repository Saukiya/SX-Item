package github.saukiya.sxitem.util;

import java.util.List;
import java.util.Random;

/**
 * 通用工具类(不知道放哪了)
 */
public class Util {

    private static final Random random = new Random();

    protected static double decimalPrecision = 100;

    public static String format(double value) {
        return String.valueOf(Math.round(value * decimalPrecision) / decimalPrecision);
    }

    public static <T> T random(T[] array) {
        return array[random.nextInt(array.length)];
    }

    public static <T> T random(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

    public static int random(int v1, int v2) {
        return random.nextInt(1 + Math.abs(v2 - v1)) + Math.min(v1, v2);
    }

    public static double random(double v1, double v2) {
        return random.nextDouble() * (v2 - v1) + v1;
    }

    public static float random(float v1, float v2) {
        return random.nextFloat() * (v2 - v1) + v1;
    }
}
