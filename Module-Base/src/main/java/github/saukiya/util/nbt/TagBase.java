package github.saukiya.util.nbt;

import github.saukiya.util.nms.ComponentUtil;

import java.io.DataOutput;
import java.io.IOException;


public interface TagBase<T> extends Cloneable {

    void write(DataOutput dataOutput) throws IOException;

    /**
     * 获取基础数据
     * 例如 List、Map、Array、byte、int 等
     *
     * @return
     */
    T getValue();

    TagType getTypeId();

    /**
     * 将NBT以带制符表的方式显示出来
     * 无法显示带转义(\)的Json
     *
     * @return String
     */
    default String toJsonString() {
        return ComponentUtil.getGson().toJson(getValue());
    }

    @Deprecated
    static TagBase<?> toTag(Object object) {
        return TagType.toTag(object);
    }
}
