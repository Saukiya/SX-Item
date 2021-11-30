package github.saukiya.sxitem.util;

import github.saukiya.sxitem.nms.TagBase;
import github.saukiya.sxitem.nms.TagEnd;
import github.saukiya.sxitem.nms.TagType;
import lombok.SneakyThrows;

import java.io.DataInput;
import java.io.DataOutput;

public abstract class NbtUtil extends NMS {

    public static NbtUtil getInst() {
        return NMS.getInst(NbtUtil.class);
    }

    @SneakyThrows
    public void writeTagBase(TagBase tagBase, DataOutput dataOutput) {
        dataOutput.writeByte(tagBase.getTypeId().getId());
        if (tagBase.getTypeId().getId() != 0) {
            dataOutput.writeUTF("");
            tagBase.write(dataOutput);
        }
    }

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
     * nbt转化工具
     *
     * @param nbtBase NBTBase
     * @return TagBase
     */
    public abstract <T extends TagBase> T asTagCopy(Object nbtBase);

    public abstract <T> T asNMSCopy(TagBase base);
}
