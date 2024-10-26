package github.saukiya.sxitem.util;

/**
 * @see github.saukiya.tools.nms.NMS
 * @deprecated
 */
public interface NMS extends github.saukiya.tools.nms.NMS {

    @Deprecated
    static <T extends NMS> T getInst(Class<T> target, String... versions) {
        return github.saukiya.tools.nms.NMS.getInst(target, versions);
    }

    @Deprecated
    static <T> T privateField(Object target, String fieldName) {
        return github.saukiya.tools.nms.NMS.privateField(target, fieldName);
    }

    @Deprecated
    static <T> T privateInstance(Class<T> target, Object... args) {
        return github.saukiya.tools.nms.NMS.privateInstance(target, args);
    }

    @Deprecated
    static int compareTo(String version) {
        return github.saukiya.tools.nms.NMS.compareTo(version);
    }

    @Deprecated
    static int compareTo(int... version) {
        return github.saukiya.tools.nms.NMS.compareTo(version);
    }

    @Deprecated
    static boolean hasClass(String className) {
        return github.saukiya.tools.nms.NMS.hasClass(className);
    }
}