package github.saukiya.sxitem.util;

import github.saukiya.sxitem.nms.TagBase;

public abstract class NbtUtil extends NMS {

    /**
     * nbt转化工具
     *
     * @param nbtBase NBTBase
     * @return TagBase
     */
    public abstract <T extends TagBase> T asTagCopy(Object nbtBase);

    public abstract <T> T asNMSCopy(TagBase base);

    public static NbtUtil getInst() {
        return NMS.getInst(NbtUtil.class);
    }
}
