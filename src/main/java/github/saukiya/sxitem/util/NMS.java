package github.saukiya.sxitem.util;

import lombok.NonNull;
import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.IntStream;

public abstract class NMS {

    public static String version = "v1_17_R1";
//    public static String version = Bukkit.getServer().getClass().getPackage().getName().split("^.+\\.")[1];

    public static final Map<Class<? extends NMS>, NMS> INST_MAP = new HashMap<>();

    private static final Map<Class<?>, Map<String, Field>> FIELD_CACHE_MAP = new HashMap<>();

    private static final Map<Class<?>, Class<?>> CLASS_WRAPS_MAP = new HashMap<>();

    static {
        CLASS_WRAPS_MAP.put(void.class, Void.class);
        CLASS_WRAPS_MAP.put(boolean.class, Boolean.class);
        CLASS_WRAPS_MAP.put(byte.class, Byte.class);
        CLASS_WRAPS_MAP.put(char.class, Character.class);
        CLASS_WRAPS_MAP.put(short.class, Short.class);
        CLASS_WRAPS_MAP.put(int.class, Integer.class);
        CLASS_WRAPS_MAP.put(float.class, Float.class);
        CLASS_WRAPS_MAP.put(double.class, Double.class);
        CLASS_WRAPS_MAP.put(long.class, Long.class);
    }

    @SneakyThrows
    protected static <T extends NMS> T getInst(Class<T> target) {
        NMS t = INST_MAP.get(target);
        if (t == null) {
            synchronized (target) {
                if (!INST_MAP.containsKey(target)) {
                    t = (NMS) Class.forName(target.getPackage().getName() + "." + target.getSimpleName() + "_" + version).getDeclaredConstructor().newInstance();
                    INST_MAP.put(target, t);
                } else {
                    t = INST_MAP.get(target);
                }
            }
        }
        return (T) t;
    }

    /**
     * 访问私有字段
     * @param target 目标
     * @param fieldName 字段名
     * @return 返回字段
     */
    @SneakyThrows
    protected static <T> T getPrivateField(Object target, String fieldName) {
        Field privateField = FIELD_CACHE_MAP
                .computeIfAbsent(target.getClass(), key -> new HashMap<>())
                .computeIfAbsent(fieldName, key -> {
                    Field temp = null;
                    try {
                        temp = target.getClass().getDeclaredField(fieldName);
                        temp.setAccessible(true);
                    } catch (NoSuchFieldException ignored) { }
                    return temp;
                });
        return (T) privateField.get(target);
    }

    /**
     * 实例化私有构造器
     * @param target 目标Class
     * @param args 构造参数
     * @return 返回实例
     */
    @SneakyThrows
    protected static <T> T newPrivateInstance(Class<T> target, Object... args) {
        Constructor[] constructors = target.getDeclaredConstructors();
        for (Constructor constructor : constructors) {
            if (constructor.getParameterCount() == args.length) {
                Class<?>[] classes = constructor.getParameterTypes();
                if (IntStream.range(0, constructor.getParameterCount()).allMatch(i -> checkClass(classes[i], args[i].getClass()))) {
                    constructor.setAccessible(true);//暂时没将构造器缓存到map中
                    return (T) constructor.newInstance(args);
                }
            }
        }
        throw new InstantiationException();
    }

    private static boolean checkClass(@NonNull Class<?> c1,@NonNull Class<?> c2) {
        if (c1.isPrimitive()) {
            return checkClass(CLASS_WRAPS_MAP.get(c1), c2);
        } else if (c2.isPrimitive()) {
            return checkClass(CLASS_WRAPS_MAP.get(c2), c1);
        }
        return c1.equals(c2);
    }
}
