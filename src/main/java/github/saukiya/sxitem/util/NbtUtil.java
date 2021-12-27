package github.saukiya.sxitem.util;

import github.saukiya.sxitem.nms.*;
import lombok.SneakyThrows;
import org.bukkit.inventory.ItemStack;

import java.io.DataInput;
import java.io.DataOutput;

public abstract class NbtUtil implements NMS {

    public static NbtUtil getInst() {
        return NMS.getInst(NbtUtil.class);
    }

    /**
     * 向IO流输出sxNBT
     *
     * @param tagBase    需要输出的sxNBT
     * @param dataOutput 例如ByteBufOutputStream
     */
    @SneakyThrows
    public void writeTagBase(TagBase tagBase, DataOutput dataOutput) {
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
    public TagBase readTagBase(DataInput dataInput) {
        byte typeId = dataInput.readByte();
        if (typeId == 0) {
            return TagEnd.getInst();
        } else {
            dataInput.readUTF();
            return TagType.getMethods(typeId).readTagBase(dataInput, 0);
        }
    }

    /**
     * 获取物品的TagCompound
     *
     * @param itemStack 物品
     * @return TagCompound
     */
    public TagCompound getItemTag(ItemStack itemStack) {
        Object nbt = getItemNBT(itemStack);
        if (nbt == null) return null;
        return asTagCompoundCopy(nbt);
    }

    /**
     * 获取物品的NBTTagCompound
     *
     * @param itemStack 物品
     * @return NBTTagCompound
     */
    public abstract Object getItemNBT(ItemStack itemStack);

    /**
     * 获取NBT封装器
     *
     * @param itemStack 物品
     * @return NBTItemWrapper
     */
    public abstract NBTItemWrapper getItemTagWrapper(ItemStack itemStack);

    /**
     * nmsNBTCompound转sxNBTCompound
     *
     * @param nbtTagCompound nmsNBTCompound
     * @return sxNBTCompound
     */
    public abstract TagCompound asTagCompoundCopy(Object nbtTagCompound);

    /**
     * sxNBTCompound转nmsNBTCompound
     *
     * @param tagCompound sxNBTCompound
     * @return nmsNBTCompound
     */
    public abstract <V> V asNMSCompoundCopy(TagCompound tagCompound);

    /**
     * json字符串转nmsNBTCompound
     * 用法: {@link NbtUtil#parseNMSCompound(String nbtBase.toString)}
     *
     * @param json NBTJson
     * @return nmsNBTCompound
     */
    public abstract <V> V parseNMSCompound(String json) throws Exception;

    /**
     * 讲 TagBase 转为 Map、List、Arrays 等基础类型
     * <s>防止视力不好的人看不到</s>
     *
     * @param tagBase TagBase
     * @return Map、List、Arrays 等基础类型
     */
    public <V> V getTagValue(TagBase tagBase) {
        return (V) tagBase.getValue();
    }

    /**
     * 将 obj 转为TagBase
     *
     * @param obj NBTBase、Map、List、Arrays 等基础类型
     * @return TagBase
     */
    public abstract <V extends TagBase> V toTag(Object obj);

    /**
     * 将 NBTBase 转为 Map、List、Arrays 等基础类型
     *
     * @param nbtBase NBTBase
     * @return Map、List、Arrays 等基础类型
     */
    public abstract <V> V getNMSValue(Object nbtBase);

    /**
     * 将 obj 转为 NBTBase
     *
     * @param obj TagBase、Map、List、Arrays 等基础类型
     * @return NBTBase
     */
    public abstract <V> V toNMS(Object obj);
}
