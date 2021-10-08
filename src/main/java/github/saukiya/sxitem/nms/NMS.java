package github.saukiya.sxitem.nms;

import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public abstract class NMS {

    public static String version = "v1_16_R1";
//    public static String version = Bukkit.getServer().getClass().getPackage().getName().split("^.+\\.")[1];

    public static Map<Class<? extends NMS>, NMS> map = new HashMap<>();

    @SneakyThrows
    protected static <T extends NMS> T getInst(Class<T> target) {
        System.out.println(Thread.currentThread().getStackTrace()[1].getClassName());
        NMS t = map.get(target);
        if (t == null) {
            synchronized (target) {
                if (!map.containsKey(target)) {
                    t = (NMS) Class.forName(NMS.class.getPackage().getName() + "." + version + ".NMS" + target.getSimpleName()).getDeclaredConstructor().newInstance();
                    map.put(target, t);
                } else {
                    t = map.get(target);
                }
            }
        }
        return (T) t;
    }

//    public static final Map<String, NMS> map = new HashMap<>();
//
//    @SneakyThrows
//    protected static <T extends NMS> T getInst() {
//        String className = Thread.currentThread().getStackTrace()[2].getClassName().split("^.+\\.")[1];
//        System.out.println(className);
//        NMS t = map.get(className);
//        if (t == null) {
//            Thread.sleep(1000);
//            synchronized (map) {
//                Thread.sleep(200);
//                if (!map.containsKey(className)) {
//                    t = (NMS) Class.forName(NMS.class.getPackage().getName() + "." + version + ".NMS" + className).getDeclaredConstructor().newInstance();
//                    map.put(className, t);
//                } else {
//                    t = map.get(className);
//                }
//            }
//        }
//        return (T) t;
//    }
}
