package github.saukiya.sxitem.util;

import github.saukiya.sxitem.nms.TagBase;
import github.saukiya.sxitem.nms.TagCompound;
import github.saukiya.sxitem.nms.TagEnd;
import github.saukiya.sxitem.nms.TagType;
import lombok.SneakyThrows;

import java.io.DataInput;
import java.io.DataOutput;

public abstract class NbtUtil extends NMS {

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
