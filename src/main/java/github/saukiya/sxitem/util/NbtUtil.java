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

    public abstract TagCompound getItemNBT(ItemStack itemStack);

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
     * nmsNBT转sxNBT
     *
     * @param nbtBase nmsNBT
     * @return sxNBT
     */
    @Deprecated
    public abstract <V extends TagBase> V asTagCopy(Object nbtBase);

    /**
     * sxNBT转nmsNBT
     *
     * @param tagBase sxNBT
     * @return nmsNBT
     */
    @Deprecated
    public abstract <V> V asNMSCopy(TagBase tagBase);
}
