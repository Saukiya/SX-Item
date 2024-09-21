package github.saukiya.sxitem.util;

import github.saukiya.sxitem.nbt.NBTItemWrapper;
import github.saukiya.sxitem.nbt.NBTTagWrapper;
import github.saukiya.util.nms.NMS;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Delegate;
import org.bukkit.inventory.ItemStack;

@Deprecated
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NbtUtil {

    @Getter
    private final static NbtUtil inst = new NbtUtil(NMS.getInst(github.saukiya.util.nms.NbtUtil.class));

    @Delegate
    private github.saukiya.util.nms.NbtUtil target;


    @Deprecated
    public NBTItemWrapper getItemTagWrapper(ItemStack itemStack) {
        return new NBTItemWrapper(target.getItemTagWrapper(itemStack));
    }

    @Deprecated
    public NBTTagWrapper createTagWrapper(Object nbtTagCompound) {
        return new NBTTagWrapper(target.createTagWrapper(nbtTagCompound));
    }
}
