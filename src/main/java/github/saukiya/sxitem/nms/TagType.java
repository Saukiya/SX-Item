package github.saukiya.sxitem.nms;

import java.io.DataInput;
import java.io.IOException;

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
    LONG_ARRAY(TagLongArray.typeMethod);

    private final byte id = (byte) this.ordinal();

    private final Method<? extends TagBase> methods;

    TagType(Method<? extends TagBase> function) {
        this.methods = function;
    }

    public static TagType.Method getMethods(int index) {
        return index >= 0 && index < TagType.values().length ? TagType.values()[index].methods : null;
    }

    public byte getId() {
        return id;
    }

    public interface Method<T extends TagBase> {

        /**
         * @param dataInput 数据读取
         * @param depth     深度值 暂时保留
         * @return TagBase
         * @throws IOException 不可能出现的IO报错
         */
        T readTagBase(DataInput dataInput, int depth) throws IOException;

    }
}