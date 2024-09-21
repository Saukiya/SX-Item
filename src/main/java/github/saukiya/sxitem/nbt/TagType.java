package github.saukiya.sxitem.nbt;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.util.NbtUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.DataInput;
import java.io.IOException;

@RequiredArgsConstructor
public enum TagType {
    END(TagEnd.typeMethod),
    BYTE(TagByte.typeMethod),
    SHORT(TagShort.typeMethod),
    INT(TagInt.typeMethod),
    LONG(TagLong.typeMethod),
    FLOAT(TagFloat.typeMethod),
    DOUBLE(TagDouble.typeMethod),
    BYTE_ARRAY(TagByteArray.typeMethod),
    STRING(TagString.typeMethod),
    LIST(TagList.typeMethod),
    COMPOUND(TagCompound.typeMethod),
    INT_ARRAY(TagIntArray.typeMethod),
    LONG_ARRAY(TagLongArray.typeMethod);//v1_12_R1+

    @Getter
    private final byte id = (byte) this.ordinal();

    private final Method methods;

    /**
     * 获取{@link Method#readTagBase(DataInput, int)} 和 {@link Method#toTag(Object)} 方法
     *
     * @param index nmsNBT的id
     * @return Method
     */
    public static TagType.Method getMethods(int index) {
        return index >= 0 && index < TagType.values().length ? TagType.values()[index].methods : null;
    }

    /**
     * 从基础数据类型中转换成TagBase
     * 例如:
     * {@link java.util.Map}
     * {@link java.util.List}
     * {@link java.lang.reflect.Array}
     * {@link org.bukkit.configuration.MemorySection}
     * 以及基础类型
     *
     * @param object 基础数据
     * @return TagBase
     */
    public static TagBase toTag(Object object) {
        if (object == null) return TagEnd.getInst();
        if (object instanceof TagBase) return (TagBase) object;
        if (object instanceof NBTWrapper) return NbtUtil.getInst().toTag(((NBTWrapper) object).getHandle());
        TagBase tagBase;
        for (TagType type : TagType.values()) {
            if ((tagBase = type.methods.toTag(object)) != null) {
                return tagBase;
            }
        }
        //Log 不支持的转化类型
        SXItem.getInst().getLogger().warning("TagType.toTag error : " + object);
        return TagEnd.getInst();
    }

    public interface Method {

        /**
         * 从IO中读取TagBase
         *
         * @param dataInput 数据读取
         * @param depth     深度值 暂时保留
         * @return TagBase
         * @throws IOException 不可能出现的IO报错
         */
        TagBase readTagBase(DataInput dataInput, int depth) throws IOException;

        /**
         * {@link TagType#toTag(Object)}
         *
         * @param object 基础数据
         * @return TagBase
         */
        TagBase toTag(Object object);

    }
}