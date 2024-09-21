package github.saukiya.util.nms;

import github.saukiya.util.base.Base;
import github.saukiya.util.nbt.TagBase;
import github.saukiya.util.nbt.TagCompound;
import github.saukiya.util.nbt.TagEnd;
import github.saukiya.util.nbt.TagType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.DataInput;
import java.io.DataOutput;

/**
 * NBT工具类
 */
public abstract class NbtUtil implements NMS {

    @Getter
    private final static NbtUtil inst = NMS.getInst(NbtUtil.class);

    /**
     * 获取 ITEM-NBT封装器
     *
     * @param itemStack BukkitItem
     * @return NBTItemWrapper
     */
    public abstract ItemWrapper getItemTagWrapper(ItemStack itemStack);

    public abstract ItemWrapper getItemTagWrapper(ItemStack itemStack, Object nmsItem);

    /**
     * 创建一个空的 NBT 封装器
     *
     * @return NBTWrapper
     */
    public final Wrapper createTagWrapper() {
        return createTagWrapper(null);
    }

    /**
     * 创建一个 NBT封装器
     *
     * @return NBTWrapper
     */
    public abstract Wrapper createTagWrapper(Object nbtTagCompound);

    /**
     * BukkitItem 转换 NMSItem
     *
     * @param itemStack BukkitItem
     * @return NMSItem
     */
    public abstract <V> V getNMSItem(ItemStack itemStack);

    public abstract void setNMSItem(ItemStack itemStack, Object nmsItem);

    /**
     * NMSItem 取 NBTTagCompound
     *
     * @param nmsItem NMSItem
     * @return NBTTagCompound
     */
    public abstract <V> V getNMSItemNBT(Object nmsItem);

    /**
     * BukkitItem 取 NBTTagCompound
     *
     * @param itemStack BukkitItem
     * @return NBTTagCompound
     */
    public abstract <V> V getItemNBT(ItemStack itemStack);

    /**
     * nmsNBTCompound 转 sxNBTCompound
     *
     * @param nbtTagCompound nmsNBTCompound
     * @return sxNBTCompound
     */
    public abstract TagCompound asTagCompoundCopy(Object nbtTagCompound);

    /**
     * sxNBTCompound 转 nmsNBTCompound
     *
     * @param tagCompound sxNBTCompound
     * @return nmsNBTCompound
     */
    public abstract <V> V asNMSCompoundCopy(TagCompound tagCompound);

    /**
     * nbtJson 转 nmsNBTCompound
     *
     * @param nbtJson nbtJson
     * @return nmsNBTCompound
     */
    public abstract <V> V parseNMSCompound(String nbtJson) throws Exception;

    /**
     * NBTBase 转 Map、List、Arrays
     *
     * @param nbtBase NBTBase
     * @return Map、List、Arrays 等基础类型
     */
    public abstract <V> V getNMSValue(Object nbtBase);

    /**
     * TagBase、Map、List、Arrays 转 NBTBase
     *
     * @param obj TagBase、Map、List、Arrays 等基础类型
     * @return NBTBase
     */
    public abstract <V> V toNMS(Object obj);

    /**
     * TagBase 转 Map、List、Arrays
     * <s>防止视力不好的人看不到</s>
     *
     * @param tagBase TagBase
     * @return Map、List、Arrays 等基础类型
     */
    public final <V> V getTagValue(TagBase tagBase) {
        return (V) tagBase.getValue();
    }

    /**
     * NBTBase、Map、List、Arrays 转 TagBase
     *
     * @param obj NBTBase、Map、List、Arrays 等基础类型
     * @return TagBase
     */
    public final <V extends TagBase> V toTag(Object obj) {
        return (V) TagType.toTag(getNMSValue(obj));
    }

    /**
     * BukkitItem 取 sxTagCompound
     *
     * @param itemStack BukkitItem
     * @return sxTagCompound
     */
    public final TagCompound getItemTag(ItemStack itemStack) {
        Object nbt = getItemNBT(itemStack);
        if (nbt == null) return null;
        return asTagCompoundCopy(nbt);
    }

    /**
     * 向IO流输出sxNBT
     *
     * @param tagBase    需要输出的sxNBT
     * @param dataOutput 例如ByteBufOutputStream
     */
    @SneakyThrows
    public final void writeTagBase(TagBase tagBase, DataOutput dataOutput) {
        dataOutput.writeByte(tagBase.getTypeId().getId());
        if (tagBase.getTypeId().getId() != 0) {
            dataOutput.writeUTF("");
            tagBase.write(dataOutput);
        }
    }

    /**
     * 向IO流中读取sxNBT
     *
     * @param dataInput 例如ByteBufInputStream
     * @return sxNBT
     */
    @SneakyThrows
    public final TagBase readTagBase(DataInput dataInput) {
        byte typeId = dataInput.readByte();
        if (typeId == 0) {
            return TagEnd.getInst();
        } else {
            dataInput.readUTF();
            return TagType.getMethods(typeId).readTagBase(dataInput, 0);
        }
    }

    /**
     * 只对单一ItemStack进行操作的封装类
     * 减少使用CraftItemStack.asNMSCopy
     * <p>
     * 写入\删除NBT后，需要save才能生效
     */
    public interface ItemWrapper extends Wrapper {

        /**
         * 保存到当前的ItemStack中
         */
        void save();

        /**
         * 链式操作器
         *
         * @return Builder
         */
        default Builder builder() {
            return new Builder(this);
        }

        @AllArgsConstructor
        class Builder {

            ItemWrapper handler;

            public Builder set(String key, Object value) {
                handler.set(key, value);
                return this;
            }

            public void save() {
                handler.save();
            }
        }
    }

    /**
     * NBTTagCompound的包装类
     * 用来封装每个版本的数据交互
     */
    public interface Wrapper extends Base.Compound {

        /**
         * 获取包装好的NBTTagCompound
         * 返回的对象与父TagCompound关联
         *
         * @param path 要获取TagCompound的路径
         * @return 如果未找到路径，则返回null
         */
        @Nullable
        Wrapper getWrapper(@Nonnull String path);


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
}
