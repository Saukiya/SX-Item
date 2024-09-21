package github.saukiya.sxitem.nbt;

import lombok.AllArgsConstructor;

/**
 * 只对单一ItemStack进行操作的封装类
 * 减少使用CraftItemStack.asNMSCopy
 * <p>
 * 写入\删除NBT后，需要save才能生效
 */
public interface NBTItemWrapper extends NBTWrapper {

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

        NBTItemWrapper handler;

        public Builder set(String key, Object value) {
            handler.set(key, value);
            return this;
        }

        public void save() {
            handler.save();
        }
    }
}
