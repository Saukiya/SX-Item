package github.saukiya.sxitem.nbt;

import github.saukiya.util.nms.NbtUtil;
import lombok.AllArgsConstructor;

@Deprecated
public class NBTItemWrapper extends NBTTagWrapper {

    NbtUtil.ItemWrapper target;

    public NBTItemWrapper(NbtUtil.ItemWrapper target) {
        super(target);
        this.target = target;
    }

    public void save() {
        target.save();
    }

    public Builder builder() {
        return new Builder();
    }

    @AllArgsConstructor
    public class Builder {

        public Builder set(String key, Object value) {
            target.set(key, value);
            return this;
        }

        public void save() {
            target.save();
        }
    }
}
