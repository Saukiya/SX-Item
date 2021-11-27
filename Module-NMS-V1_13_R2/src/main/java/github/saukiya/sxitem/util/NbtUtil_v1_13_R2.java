package github.saukiya.sxitem.util;

import github.saukiya.sxitem.nms.*;
import net.minecraft.server.v1_13_R2.*;

import java.util.stream.Collectors;

public class NbtUtil_v1_13_R2 extends NbtUtil {

    private final NBTTagEnd nbtTagEnd = new NBTTagEnd();

    @Override
    public TagBase asTagCopy(Object nbtBase) {
        TagBase tagBase = null;
        if (nbtBase instanceof NBTBase) {
            if (nbtBase instanceof NBTTagCompound) {
                NBTTagCompound nbtTagCompound = (NBTTagCompound) nbtBase;
                tagBase = nbtTagCompound.getKeys().stream().collect(Collectors.toMap(key -> key, key -> asTagCopy(nbtTagCompound.get(key)), (a, b) -> b, TagCompound::new));
            } else if (nbtBase instanceof NBTTagList) {
                NBTTagList nbtTagList = (NBTTagList) nbtBase;
                tagBase = nbtTagList.stream().map(this::asTagCopy).collect(Collectors.toCollection(TagList::new));
            } else if (nbtBase instanceof NBTTagByteArray) {
                tagBase = new TagByteArray(((NBTTagByteArray) nbtBase).c());
            } else if (nbtBase instanceof NBTTagIntArray) {
                tagBase = new TagIntArray(((NBTTagIntArray) nbtBase).d());
            } else if (nbtBase instanceof NBTTagLongArray) {
                tagBase = new TagLongArray(((NBTTagLongArray) nbtBase).d());
            } else if (nbtBase instanceof NBTTagByte) {
                tagBase = new TagByte(((NBTTagByte) nbtBase).asByte());
            } else if (nbtBase instanceof NBTTagShort) {
                tagBase = new TagShort(((NBTTagShort) nbtBase).asShort());
            } else if (nbtBase instanceof NBTTagInt) {
                tagBase = new TagInt(((NBTTagInt) nbtBase).asInt());
            } else if (nbtBase instanceof NBTTagFloat) {
                tagBase = new TagFloat(((NBTTagFloat) nbtBase).asFloat());
            } else if (nbtBase instanceof NBTTagDouble) {
                tagBase = new TagDouble(((NBTTagDouble) nbtBase).asDouble());
            } else if (nbtBase instanceof NBTTagLong) {
                tagBase = new TagLong(((NBTTagLong) nbtBase).asLong());
            } else if (nbtBase instanceof NBTTagString) {
                tagBase = new TagString(((NBTTagString) nbtBase).asString());
            } else if (nbtBase instanceof NBTTagEnd) {
                tagBase = TagEnd.getInst();
            }
        }
        return tagBase;
    }

    @Override
    public NBTBase asNMSCopy(TagBase tagBase) {
        NBTBase nbtBase = null;
        if (tagBase instanceof TagCompound) {
            TagCompound tagCompound = (TagCompound) tagBase;
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            tagCompound.forEach((key, value) -> nbtTagCompound.set(key, asNMSCopy(value)));
            nbtBase = nbtTagCompound;
        } else if(tagBase instanceof TagList) {
            TagList tagList = (TagList) tagBase;
            nbtBase = tagList.stream().map(this::asNMSCopy).collect(Collectors.toCollection(NBTTagList::new));
        } else if(tagBase instanceof TagByteArray) {
            nbtBase = new NBTTagByteArray(((TagByteArray) tagBase).byteArray());
        } else if(tagBase instanceof TagIntArray) {
            nbtBase = new NBTTagIntArray(((TagIntArray) tagBase).intArray());
        } else if(tagBase instanceof TagLongArray) {
            nbtBase = new NBTTagLongArray(((TagLongArray) tagBase).longArray());
        } else if(tagBase instanceof TagByte) {
            nbtBase = new NBTTagByte(((TagNumber) tagBase).byteValue());
        } else if(tagBase instanceof TagShort) {
            nbtBase = new NBTTagShort(((TagNumber) tagBase).shortValue());
        } else if(tagBase instanceof TagInt) {
            nbtBase = new NBTTagInt(((TagNumber) tagBase).intValue());
        } else if(tagBase instanceof TagFloat) {
            nbtBase = new NBTTagFloat(((TagNumber) tagBase).floatValue());
        } else if(tagBase instanceof TagDouble) {
            nbtBase = new NBTTagDouble(((TagNumber) tagBase).doubleValue());
        } else if(tagBase instanceof TagLong) {
            nbtBase = new NBTTagLong(((TagNumber) tagBase).longValue());
        } else if(tagBase instanceof TagString) {
            nbtBase = new NBTTagString(((TagString) tagBase).getValue());
        } else if(tagBase instanceof TagEnd) {
            nbtBase = nbtTagEnd;
        }
        return nbtBase;
    }
}
