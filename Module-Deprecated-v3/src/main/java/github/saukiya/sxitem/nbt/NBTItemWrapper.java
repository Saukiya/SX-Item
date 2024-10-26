package github.saukiya.sxitem.nbt;

import github.saukiya.tools.nms.NbtUtil;
import lombok.AllArgsConstructor;

/**
 * @see github.saukiya.tools.nms.NbtUtil.ItemWrapper
 * @deprecated
 */
public class NBTItemWrapper extends NBTTagWrapper {

    NbtUtil.ItemWrapper target;

    @Deprecated
    public NBTItemWrapper(NbtUtil.ItemWrapper target) {
        super(target);
        this.target = target;
    }

    @Deprecated
    public void save() {
        target.save();
    }

    @Deprecated
    public Builder builder() {
        return new Builder();
    }

    @Deprecated
    @AllArgsConstructor
    public class Builder {

        @Deprecated
        public Builder set(String key, Object value) {
            target.set(key, value);
            return this;
        }

        @Deprecated
        public void save() {
            target.save();
        }
    }
}
