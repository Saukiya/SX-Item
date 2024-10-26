package github.saukiya.tools.nms;

import github.saukiya.tools.base.ICompound;
import github.saukiya.tools.nbt.TagBase;
import github.saukiya.tools.nbt.TagCompound;
import github.saukiya.tools.nbt.TagEnd;
import github.saukiya.tools.nbt.TagType;
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
 * <pre>{@code
 *  val wrapper = NbtUtil.getInst().getItemTagWrapper(itemStack);
 *  wrapper.set("aa.bb", obj);
 *  wrapper.save();
 *
 *  NbtUtil.getInst().getItemTagWrapper(itemStack).builder()
 *      .set("aa.bb", obj)
 *      .save();
 * }</pre>
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
    public final <V> V getTagValue(TagBase<V> tagBase) {
        return tagBase.getValue();
    }

    /**
     * NBTBase、Map、List、Arrays 转 TagBase
     *
     * @param obj NBTBase、Map、List、Arrays 等基础类型
     * @return TagBase
     */
    public final TagBase<?> toTag(Object obj) {
        return TagType.toTag(getNMSValue(obj));
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
     * 物品NBTTagCompound的包装类, 简化NBT处理逻辑
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

            public Builder remove(String key) {
                set(key, null);
                return this;
            }

            public void save() {
                handler.save();
            }
        }
    }

    /**
     * NBTTagCompound的包装类, 简化NBT处理逻辑
     */
    public interface Wrapper extends ICompound {

        /**
         * 获取包装好的NBTTagCompound
         * 返回的对象与父TagCompound关联
         *
         * @param path  要获取TagCompound的路径
         * @return 如果未找到路径，则返回null
         */
        @Nullable
        Wrapper getWrapper(@Nonnull String path);

        /**
         * 获取原始NBTBase数据
         *
         * @param path 要获取TagCompound的路径
         * @return 如果未找到路径，则返回null
         */
        @Nullable
        Object getNBTBase(@Nonnull String path);

        /**
         * 获取所处理的NBTTagCompound
         */
        @Nonnull
        Object getHandle();

        /**
         * 当前层级元素数量
         */
        @Nonnull
        int size();

        /**
         * 将nbt保存到指定物品上
         *
         * @param itemStack
         */
        void save(@Nonnull ItemStack itemStack);

        @Nonnull
        default String nbtToString() {
            return getHandle().toString();
        }
    }
}
