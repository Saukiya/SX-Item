package github.saukiya.sxitem.util;

@Deprecated
public interface NMS extends github.saukiya.util.nms.NMS {

    static <T extends NMS> T getInst(Class<T> target, String... versions) {
        return github.saukiya.util.nms.NMS.getInst(target, versions);
    }

    static <T> T privateField(Object target, String fieldName) {
        return github.saukiya.util.nms.NMS.privateField(target, fieldName);
    }

    static <T> T privateInstance(Class<T> target, Object... args) {
        return github.saukiya.util.nms.NMS.privateInstance(target, args);
    }

    static int compareTo(String version) {
        return github.saukiya.util.nms.NMS.compareTo(version);
    }

    static int compareTo(int... version) {
        return github.saukiya.util.nms.NMS.compareTo(version);
    }

    static boolean hasClass(String className) {
        return github.saukiya.util.nms.NMS.hasClass(className);
    }
}