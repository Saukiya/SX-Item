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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class NbtUtil_v1_17_R1 extends NbtUtil {

    //TODO  为什么要转？
    //      为什么不直接同源?
    public NBTTagWrapper newItemTagWrapper(TagCompound tagCompound) {
        return new NBTTagWrapper_v1_17_R1(asNMSCompoundCopy(tagCompound));
    }

    @Override
    public NBTTagWrapper getItemTagWrapper(ItemStack itemStack) {
        return new NBTTagWrapper_v1_17_R1(CraftItemStack.asNMSCopy(itemStack).getTag());
    }

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

    /**
     * 将 NBTBase 转为 Map、List、Arrays 等基础类型
     *
     * @param nbtBase NBTBase
     * @return Map、List、Arrays 等基础类型
     */
    public Object getNMSValue(Object nbtBase) {
        if (nbtBase instanceof NBTBase) {
            if (nbtBase instanceof NBTTagCompound) {
                NBTTagCompound nbtTagCompound = (NBTTagCompound) nbtBase;
                Map<String, Object> map = new HashMap<>();
                for (String key : nbtTagCompound.getKeys()) {
                    map.put(key, getNMSValue(nbtTagCompound.get(key)));
                }
                return map;
            } else if (nbtBase instanceof NBTTagList) {
                return ((NBTTagList) nbtBase).stream().map(this::getNMSValue).collect(Collectors.toList());
            } else if (nbtBase instanceof NBTTagByteArray) {
                return ((NBTTagByteArray) nbtBase).getBytes();
            } else if (nbtBase instanceof NBTTagIntArray) {
                return ((NBTTagIntArray) nbtBase).getInts();
            } else if (nbtBase instanceof NBTTagLongArray) {
                return ((NBTTagLongArray) nbtBase).getLongs();
            } else if (nbtBase instanceof NBTTagByte) {
                return ((NBTTagByte) nbtBase).asByte();
            } else if (nbtBase instanceof NBTTagShort) {
                return ((NBTTagShort) nbtBase).asShort();
            } else if (nbtBase instanceof NBTTagInt) {
                return ((NBTTagInt) nbtBase).asInt();
            } else if (nbtBase instanceof NBTTagFloat) {
                return ((NBTTagFloat) nbtBase).asFloat();
            } else if (nbtBase instanceof NBTTagDouble) {
                return ((NBTTagDouble) nbtBase).asDouble();
            } else if (nbtBase instanceof NBTTagLong) {
                return ((NBTTagLong) nbtBase).asLong();
            } else if (nbtBase instanceof NBTTagString) {
                return ((NBTTagString) nbtBase).asString();
            }
        }
        return null;
    }

    /**
     * 讲 TagBase 转为 Map、List、Arrays 等基础类型
     * <s>防止眼瞎的人看不到方法</s>
     *
     * @param tagBase TagBase
     * @return Map、List、Arrays 等基础类型
     */
    public Object getTagValue(TagBase tagBase) {
        return tagBase.getValue();
    }

    /**
     * 将 obj 转为TagBase
     *
     * @param obj NBTBase / Object
     * @return TagBase
     */
    @Override
    public TagBase toTag(Object obj) {
        return TagBase.toTag(obj instanceof NBTBase ? getNMSValue(obj) : obj);
    }


    /**
     * 将 obj 转为 NMSBase
     *
     * @param obj TagBase / Object
     * @return
     */
    public NBTBase toNMS(Object obj) {
        if (obj instanceof TagBase) {
            return toNMS(((TagBase) obj).getValue());
        } else if (obj instanceof Map) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            Map<String, ?> map = (Map<String, ?>) obj;
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                nbtTagCompound.set(key, toNMS(value));
            }
            return nbtTagCompound;
        } else if (obj instanceof List) {
            List list = (List) obj;
            NBTTagList nbtTagList = new NBTTagList();
            for (Object o : list) {
                nbtTagList.add(toNMS(o));
            }
            return nbtTagList;
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
        } else if (obj instanceof Float) {
            return NBTTagFloat.a(((Number) obj).floatValue());
        } else if (obj instanceof Double) {
            return NBTTagDouble.a(((Number) obj).doubleValue());
        } else if (obj instanceof Long) {
            return NBTTagLong.a(((Number) obj).longValue());
        }
        return NBTTagEnd.b;
    }

    // 待删除
    @Override
    public NBTBase asNMSCopy(TagBase tagBase) {
        return toNMS(tagBase);
    }

    public static class NBTTagWrapper_v1_17_R1 implements NBTTagWrapper {

        public NBTTagCompound nbtTagCompound;

        public NBTTagWrapper_v1_17_R1(NBTTagCompound tagCompound) {
            this.nbtTagCompound = tagCompound;
        }

        public NBTTagCompound getCompound(NBTTagCompound compound, String path) {
            if (compound == null) return null;
            int index = path.indexOf('.');
            if (index != -1)
                return getCompound(getCompound(compound, path.substring(0, index)), path.substring(index + 1));
            NBTBase nbtBase = compound.get(path);
            if (nbtBase instanceof NBTTagCompound) return (NBTTagCompound) nbtBase;
            return null;
        }

        @Override
        public void setNBT(String path, Object def) {

        }

        @Override
        public void removeNBT(String path) {

        }

        @Override
        public Set<String> getKeys(String path) {
            return null;
        }

        @Override
        public <V> V getNBT(String path, V def) {
            return null;
        }
    }
}
