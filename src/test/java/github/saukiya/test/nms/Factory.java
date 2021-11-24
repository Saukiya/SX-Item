package github.saukiya.test.nms;

import java.util.Arrays;
import java.util.HashMap;

public class Factory {
    private static String version = "v1_17_R1";

    private static HashMap<Class<? extends Factory>, Factory> map = new HashMap<>();

    public static <T> T getInst() {
        System.out.println(Arrays.toString(Thread.currentThread().getStackTrace()));
        return null;
    }

    public static void method() {
        StackTraceElement[] elements = (new Throwable()).getStackTrace();
        for (StackTraceElement ele : elements) {
            System.out.println(ele.getClassName() + "调用了method方法");
        }
    }
}
