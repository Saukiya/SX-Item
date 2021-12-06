package github.saukiya.sxitem.util;

import github.saukiya.sxitem.nms.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import lombok.SneakyThrows;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NbtUtil_v1_15_R1 extends NbtUtil {

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
    public TagBase toTag(Object obj) {
        return TagBase.toTag(obj instanceof NBTBase ? getNMSValue(obj) : obj);
    }

    @Override
    public Object getNMSValue(Object nbtBase) {
        if (nbtBase instanceof NBTBase) {
            if (nbtBase instanceof NBTTagCompound) {
                NBTTagCompound nbtTagCompound = (NBTTagCompound) nbtBase;
                return nbtTagCompound.getKeys().stream().collect(Collectors.toMap(key -> key, key -> getNMSValue(nbtTagCompound.get(key)), (a, b) -> b));
            } else if (nbtBase instanceof NBTTagList) {
                return ((NBTTagList) nbtBase).stream().map(this::getNMSValue).collect(Collectors.toList());
            } else if (nbtBase instanceof NBTTagByteArray) {
                return ((NBTTagByteArray) nbtBase).getBytes();
            } else if (nbtBase instanceof NBTTagIntArray) {
                return ((NBTTagIntArray) nbtBase).getInts();
            } else if (nbtBase instanceof NBTTagLongArray) {
                return ((NBTTagLongArray) nbtBase).getLongs();
            } else if (nbtBase instanceof NBTTagString) {
                return ((NBTTagString) nbtBase).asString();
            } else if (nbtBase instanceof NBTTagByte) {
                return ((NBTTagByte) nbtBase).asByte();
            } else if (nbtBase instanceof NBTTagShort) {
                return ((NBTTagShort) nbtBase).asShort();
            } else if (nbtBase instanceof NBTTagInt) {
                return ((NBTTagInt) nbtBase).asInt();
            } else if (nbtBase instanceof NBTTagLong) {
                return ((NBTTagLong) nbtBase).asLong();
            } else if (nbtBase instanceof NBTTagFloat) {
                return ((NBTTagFloat) nbtBase).asFloat();
            } else if (nbtBase instanceof NBTTagDouble) {
                return ((NBTTagDouble) nbtBase).asDouble();
            }
        }
        return null;
    }

    @Override
    public NBTBase toNMS(Object obj) {
        if (obj instanceof TagBase) {
            return toNMS(((TagBase) obj).getValue());
        } else if (obj instanceof NBTBase) {
            return (NBTBase) obj;
        } else if (obj instanceof Map) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            ((Map<String, ?>) obj).forEach((key, value) -> nbtTagCompound.set(key, toNMS(value)));
            return nbtTagCompound;
        } else if (obj instanceof List) {
            return ((List<?>) obj).stream().map(this::toNMS).collect(Collectors.toCollection(NBTTagList::new));
        } else if (obj instanceof byte[]) {
            return new NBTTagByteArray((byte[]) obj);
        } else if (obj instanceof int[]) {
            return new NBTTagIntArray((int[]) obj);
        } else if (obj instanceof long[]) {
            return new NBTTagLongArray((long[]) obj);
        } else if (obj instanceof String) {
            return NBTTagString.a(obj.toString());
        } else if (obj instanceof Byte) {
            return NBTTagByte.a(((Number) obj).byteValue());
        } else if (obj instanceof Short) {
            return NBTTagShort.a(((Number) obj).shortValue());
        } else if (obj instanceof Integer) {
            return NBTTagInt.a(((Number) obj).intValue());
        } else if (obj instanceof Long) {
            return NBTTagLong.a(((Number) obj).longValue());
        } else if (obj instanceof Float) {
            return NBTTagFloat.a(((Number) obj).floatValue());
        } else if (obj instanceof Double) {
            return NBTTagDouble.a(((Number) obj).doubleValue());
        }
        return NBTTagEnd.b;
    }
}
