package github.saukiya.sxitem.util;

import github.saukiya.sxitem.SXItem;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * NMS
 */
public interface NMS {

    static <T extends NMS> T getInst(Class<T> target, String... versions) {
        NMS t = Data.INST_MAP.get(target);
        if (t == null) {
            synchronized (target) {
                t = Data.INST_MAP.computeIfAbsent(target, k -> {
                    try {
                        String version = Arrays.stream(versions).filter(ver -> compareTo(ver) >= 0).findFirst().orElse(Data.VERSION);
                        SXItem.getInst().getLogger().info(target.getSimpleName() + " use Version: " + version);
                        return (NMS) Class.forName(target.getName() + "_" + version).getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                        SXItem.getInst().getLogger().warning(target.getSimpleName() + " No Version : " + Data.VERSION);
                    }
                    return null;
                });
            }
        }
        return (T) t;
    }

    /**
     * 访问私有字段
     *
     * @param target    目标
     * @param fieldName 字段名
     * @return 返回字段
     */
    @SneakyThrows
    static <T> T getPrivateField(Object target, String fieldName) {
        Field privateField = Data.FIELD_CACHE_MAP
                .computeIfAbsent(target.getClass(), key -> new HashMap<>())
                .computeIfAbsent(fieldName, key -> {
                    Field temp = null;
                    try {
                        temp = target.getClass().getDeclaredField(fieldName);
                        temp.setAccessible(true);
                    } catch (NoSuchFieldException ignored) {
                    }
                    return temp;
                });
        return (T) privateField.get(target);
    }

    /**
     * 实例化私有构造器(暂时没将构造器缓存到map中，只适合单例使用)
     *
     * @param target 目标Class
     * @param args   构造参数
     * @return 返回实例
     */
    @SneakyThrows
    static <T> T newPrivateInstance(Class<T> target, Object... args) {
        Constructor[] constructors = target.getDeclaredConstructors();
        for (Constructor constructor : constructors) {
            if (constructor.getParameterCount() == args.length) {
                Class<?>[] classes = constructor.getParameterTypes();
                if (IntStream.range(0, constructor.getParameterCount()).allMatch(i -> Data.checkClass(classes[i], args[i].getClass()))) {
                    constructor.setAccessible(true);//暂时没将构造器缓存到map中
                    return (T) constructor.newInstance(args);
                }
            }
        }
        throw new InstantiationException();
    }

    /**
     * 比较版本
     *
     * @param version 所需版本(v?_?_R?)
     * @return 1/0/-1   当前版本(1大于/0等于/-1小于)所需版本
     */
    static int compareTo(String version) {
        Matcher matcher = Data.VERSION_PATTERN.matcher(version);
        if (!matcher.matches()) return 0;
        return IntStream.range(0, 3).map(i -> Integer.compare(Data.thisVersionSplit[i], Integer.parseInt(matcher.group(i + 1)))).filter(ct -> ct != 0).findFirst().orElse(0);
    }
}

class Data {
    protected static final Map<Class<? extends NMS>, NMS> INST_MAP = new HashMap<>();
    protected static final Map<Class<?>, Map<String, Field>> FIELD_CACHE_MAP = new HashMap<>();
    protected static final Map<Class<?>, Class<?>> CLASS_WRAPS_MAP = new HashMap<>();
    protected static final String VERSION = Bukkit.getServer() != null ? Bukkit.getServer().getClass().getPackage().getName().split("^.+\\.")[1] : "v1_17_R1";
    protected static final Pattern VERSION_PATTERN = Pattern.compile("v(\\d+)_(\\d+)_R(\\d+)");
    protected static final int[] thisVersionSplit;

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
        Matcher matcher = VERSION_PATTERN.matcher(VERSION);
        matcher.matches();
        thisVersionSplit = IntStream.range(0, matcher.groupCount()).map(i -> Integer.parseInt(matcher.group(i + 1))).toArray();
    }

    protected static boolean checkClass(@Nonnull Class<?> c1, @NonNull Class<?> c2) {
        if (c1.isPrimitive()) {
            return checkClass(CLASS_WRAPS_MAP.get(c1), c2);
        } else if (c2.isPrimitive()) {
            return checkClass(CLASS_WRAPS_MAP.get(c2), c1);
        }
        return c1.equals(c2);
    }
}
