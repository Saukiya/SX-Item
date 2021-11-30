package github.saukiya.sxitem.util;

import github.saukiya.sxitem.nms.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import lombok.SneakyThrows;
import net.minecraft.nbt.*;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.util.stream.Collectors;

public class NbtUtil_v1_17_R1 extends NbtUtil {

    @Override
    public TagCompound getItemNBT(ItemStack itemStack) {
        return asTagCompoundCopy(CraftItemStack.asNMSCopy(itemStack).getTag());
    }

    @SneakyThrows
    @Override
    public TagCompound asTagCompoundCopy(Object nbtTagCompound) {
        ByteBuf buf = Unpooled.buffer();
        NBTCompressedStreamTools.a((NBTTagCompound) nbtTagCompound, (DataOutput) new ByteBufOutputStream(buf));
        return (TagCompound) readTagBase(new ByteBufInputStream(buf));
    }

    @SneakyThrows
    @Override
    public NBTTagCompound asNMSCompoundCopy(TagCompound tagCompound) {
        ByteBuf buf = Unpooled.buffer();
        writeTagBase(tagCompound, new ByteBufOutputStream(buf));
        return NBTCompressedStreamTools.a(new DataInputStream(new ByteBufInputStream(buf)), NBTReadLimiter.a);
    }

    @Override
    public TagBase asTagCopy(Object nbtBase) {
        TagBase tagBase = null;
        if (nbtBase instanceof NBTBase) {
            if (nbtBase instanceof NBTTagCompound) {
                NBTTagCompound nbtTagCompound = (NBTTagCompound) nbtBase;
                tagBase = nbtTagCompound.getKeys().stream().collect(Collectors.toMap(key -> key, key -> asTagCopy(nbtTagCompound.get(key)), (a, b) -> b, TagCompound::new));
            } else if (nbtBase instanceof NBTTagList) {
                tagBase = ((NBTTagList) nbtBase).stream().map(this::asTagCopy).collect(Collectors.toCollection(TagList::new));
            } else if (nbtBase instanceof NBTTagByteArray) {
                tagBase = new TagByteArray(((NBTTagByteArray) nbtBase).getBytes());
            } else if (nbtBase instanceof NBTTagIntArray) {
                tagBase = new TagIntArray(((NBTTagIntArray) nbtBase).getInts());
            } else if (nbtBase instanceof NBTTagLongArray) {
                tagBase = new TagLongArray(((NBTTagLongArray) nbtBase).getLongs());
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
        } else if (tagBase instanceof TagList) {
            TagList tagList = (TagList) tagBase;
            nbtBase = tagList.stream().map(this::asNMSCopy).collect(Collectors.toCollection(NBTTagList::new));
        } else if (tagBase instanceof TagByteArray) {
            nbtBase = new NBTTagByteArray(((TagByteArray) tagBase).byteArray());
        } else if (tagBase instanceof TagIntArray) {
            nbtBase = new NBTTagIntArray(((TagIntArray) tagBase).intArray());
        } else if (tagBase instanceof TagLongArray) {
            nbtBase = new NBTTagLongArray(((TagLongArray) tagBase).longArray());
        } else if (tagBase instanceof TagByte) {
            nbtBase = NBTTagByte.a(((TagNumber) tagBase).byteValue());
        } else if (tagBase instanceof TagShort) {
            nbtBase = NBTTagShort.a(((TagNumber) tagBase).shortValue());
        } else if (tagBase instanceof TagInt) {
            nbtBase = NBTTagInt.a(((TagNumber) tagBase).intValue());
        } else if (tagBase instanceof TagFloat) {
            nbtBase = NBTTagFloat.a(((TagNumber) tagBase).floatValue());
        } else if (tagBase instanceof TagDouble) {
            nbtBase = NBTTagDouble.a(((TagNumber) tagBase).doubleValue());
        } else if (tagBase instanceof TagLong) {
            nbtBase = NBTTagLong.a(((TagNumber) tagBase).longValue());
        } else if (tagBase instanceof TagString) {
            nbtBase = NBTTagString.a(((TagString) tagBase).getValue());
        } else if (tagBase instanceof TagEnd) {
            nbtBase = NBTTagEnd.b;
        }
        return nbtBase;
    }
}
