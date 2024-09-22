package github.saukiya.sxitem.nbt;

import github.saukiya.util.nms.NbtUtil;
import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @see github.saukiya.util.nms.NbtUtil.Wrapper
 * @deprecated
 */
@AllArgsConstructor
public class NBTTagWrapper {

    @Deprecated
    @Delegate
    NbtUtil.Wrapper target;

    @Deprecated
    @Nullable
    public NBTTagWrapper getWrapper(@Nonnull String path) {
        return new NBTTagWrapper(target.getWrapper(path));
    }
}
