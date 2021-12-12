package github.saukiya.sxitem.nms;

/**
 * 只对单一ItemStack进行操作的封装类
 * 减少使用CraftItemStack.asNMSCopy
 * <p>
 * 写入\删除NBT后，需要save才能生效
 */
public interface NBTItemWrapper extends NBTTagWrapper {

    void save();
}
