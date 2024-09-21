package github.saukiya.sxitem.nbt;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * NBTTagCompound的包装类
 * 用来封装每个版本的数据交互
 */
public interface NBTWrapper extends CompoundBase {

    /**
     * 获取包装好的NBTTagCompound
     * 返回的对象与父TagCompound关联
     *
     * @param path 要获取TagCompound的路径
     * @return 如果未找到路径，则返回null
     */
    @Nullable
    NBTWrapper getWrapper(@Nonnull String path);


    void save(@Nonnull ItemStack itemStack);

    /**
     * 获取所处理的NBTTagCompound
     *
     * @return NBTTagCompound
     */
    @Nonnull
    Object getHandle();

    @Nonnull
    default String nbtToString() {
        return getHandle().toString();
    }
}
