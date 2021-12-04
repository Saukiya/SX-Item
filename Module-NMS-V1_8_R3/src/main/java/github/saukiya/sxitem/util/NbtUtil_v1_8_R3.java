package github.saukiya.sxitem.util;

import github.saukiya.sxitem.nms.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NbtUtil_v1_8_R3 extends NbtUtil {

    private final NBTTagEnd nbtTagEnd = NMS.newPrivateInstance(NBTTagEnd.class);

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

    @Override
    public NBTTagCompound parseNMSCompound(String json) throws Exception {
        return MojangsonParser.parse(json);
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
                tagBase = nbtTagCompound.c().stream().collect(Collectors.toMap(key -> key, key -> asTagCopy(nbtTagCompound.get(key)), (a, b) -> b, TagCompound::new));
            } else if (nbtBase instanceof NBTTagList) {
                NBTTagList nbtTagList = (NBTTagList) nbtBase;
                tagBase = IntStream.range(0, nbtTagList.size()).mapToObj(i -> asTagCopy(nbtTagList.g(i))).collect(Collectors.toCollection(TagList::new));
            } else if (nbtBase instanceof NBTTagByteArray) {
                tagBase = new TagByteArray(((NBTTagByteArray) nbtBase).c());
            } else if (nbtBase instanceof NBTTagIntArray) {
                tagBase = new TagIntArray(((NBTTagIntArray) nbtBase).c());
            } else if (nbtBase instanceof NBTTagByte) {
                tagBase = new TagByte(((NBTTagByte) nbtBase).f());
            } else if (nbtBase instanceof NBTTagShort) {
                tagBase = new TagShort(((NBTTagShort) nbtBase).e());
            } else if (nbtBase instanceof NBTTagInt) {
                tagBase = new TagInt(((NBTTagInt) nbtBase).d());
            } else if (nbtBase instanceof NBTTagFloat) {
                tagBase = new TagFloat(((NBTTagFloat) nbtBase).h());
            } else if (nbtBase instanceof NBTTagDouble) {
                tagBase = new TagDouble(((NBTTagDouble) nbtBase).g());
            } else if (nbtBase instanceof NBTTagLong) {
                tagBase = new TagLong(((NBTTagLong) nbtBase).c());
            } else if (nbtBase instanceof NBTTagString) {
                tagBase = new TagString(((NBTTagString) nbtBase).a_());
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
        } else if (tagBase instanceof TagList || tagBase instanceof TagLongArray) {
            TagList tagList = (TagList) tagBase;
            NBTTagList nbtTagList = new NBTTagList();
            tagList.stream().map(this::asNMSCopy).forEach(nbtTagList::add);
            nbtBase = nbtTagList;
        } else if (tagBase instanceof TagByteArray) {
            nbtBase = new NBTTagByteArray(((TagByteArray) tagBase).byteArray());
        } else if (tagBase instanceof TagIntArray) {
            nbtBase = new NBTTagIntArray(((TagIntArray) tagBase).intArray());
        } else if (tagBase instanceof TagByte) {
            nbtBase = new NBTTagByte(((TagNumber) tagBase).byteValue());
        } else if (tagBase instanceof TagShort) {
            nbtBase = new NBTTagShort(((TagNumber) tagBase).shortValue());
        } else if (tagBase instanceof TagInt) {
            nbtBase = new NBTTagInt(((TagNumber) tagBase).intValue());
        } else if (tagBase instanceof TagFloat) {
            nbtBase = new NBTTagFloat(((TagNumber) tagBase).floatValue());
        } else if (tagBase instanceof TagDouble) {
            nbtBase = new NBTTagDouble(((TagNumber) tagBase).doubleValue());
        } else if (tagBase instanceof TagLong) {
            nbtBase = new NBTTagLong(((TagNumber) tagBase).longValue());
        } else if (tagBase instanceof TagString) {
            nbtBase = new NBTTagString(((TagString) tagBase).getValue());
        } else if (tagBase instanceof TagEnd) {
            nbtBase = nbtTagEnd;
        }
        return nbtBase;
    }
}
