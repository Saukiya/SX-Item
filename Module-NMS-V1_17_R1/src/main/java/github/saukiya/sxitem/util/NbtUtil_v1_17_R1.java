package github.saukiya.sxitem.util;

import github.saukiya.sxitem.nbt.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import lombok.SneakyThrows;
import net.minecraft.nbt.*;
import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NbtUtil_v1_17_R1 extends NbtUtil {

    @Override
    public NBTTagCompound getItemNBT(ItemStack itemStack) {
        return CraftItemStack.asNMSCopy(itemStack).getTag();
    }

    @Override
    public NBTItemWrapper getItemTagWrapper(ItemStack itemStack) {
        return new NBTItemWrapperImpl(itemStack);
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
        return TagType.toTag(obj instanceof NBTBase ? getNMSValue(obj) : obj);
    }

    @Override
    public Object getNMSValue(Object nbtBase) {
        if (nbtBase instanceof NBTBase) {
            switch (((NBTBase) nbtBase).b().b()) {
                case "TAG_Compound":
                    NBTTagCompound nbtTagCompound = (NBTTagCompound) nbtBase;
                    return nbtTagCompound.getKeys().stream().collect(Collectors.toMap(key -> key, key -> getNMSValue(nbtTagCompound.get(key)), (a, b) -> b));
                case "TAG_List":
                    return ((NBTTagList) nbtBase).stream().map(this::getNMSValue).collect(Collectors.toList());
                case "TAG_Byte_Array":
                    return ((NBTTagByteArray) nbtBase).getBytes();
                case "TAG_Int_Array":
                    return ((NBTTagIntArray) nbtBase).getInts();
                case "TAG_Long_Array":
                    return ((NBTTagLongArray) nbtBase).getLongs();
                case "TAG_String":
                    return ((NBTTagString) nbtBase).asString();
                case "TAG_Byte":
                    return ((NBTTagByte) nbtBase).asByte();
                case "TAG_Short":
                    return ((NBTTagShort) nbtBase).asShort();
                case "TAG_Int":
                    return ((NBTTagInt) nbtBase).asInt();
                case "TAG_Long":
                    return ((NBTTagLong) nbtBase).asLong();
                case "TAG_Float":
                    return ((NBTTagFloat) nbtBase).asFloat();
                case "TAG_Double":
                    return ((NBTTagDouble) nbtBase).asDouble();
            }
        }
        return null;
    }

    @Override
    public NBTBase toNMS(Object obj) {
        if (obj != null) {
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
            } else if (obj.getClass().isArray()) {
                return IntStream.range(0, Array.getLength(obj)).mapToObj(i -> toNMS(Array.get(obj, i))).collect(Collectors.toCollection(NBTTagList::new));
            } else if (obj instanceof String) {
                return NBTTagString.a(obj.toString());
            } else if (obj instanceof Boolean) {
                return NBTTagByte.a((Boolean) obj);
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
        }
        return NBTTagEnd.b;
    }

    public final class NBTItemWrapperImpl extends NBTTagWrapperImpl implements NBTItemWrapper {
        net.minecraft.world.item.ItemStack nmsItem;
        ItemStack itemStack;

        protected NBTItemWrapperImpl(ItemStack itemStack) {
            this(itemStack, CraftItemStack.asNMSCopy(itemStack));
        }

        NBTItemWrapperImpl(ItemStack itemStack, net.minecraft.world.item.ItemStack nmsItem) {
            super(nmsItem.getTag());
            this.itemStack = itemStack;
            this.nmsItem = nmsItem;
            if (nmsItem.isEmpty()) throw new NullPointerException();
        }

        @Override
        public void save() {
            itemStack.setItemMeta(CraftItemStack.getItemMeta(nmsItem));
        }
    }

    public class NBTTagWrapperImpl implements NBTTagWrapper {

        private final NBTTagCompound handle;

        private NBTTagWrapperImpl(NBTTagCompound tagCompound) {
            handle = tagCompound != null ? tagCompound : new NBTTagCompound();
        }

        protected NBTBase get(NBTTagCompound compound, String path) {
            Validate.notEmpty(path, "Cannot get to an empty path");
            int index = path.indexOf('.');
            if (index == -1) return compound.get(path);
            NBTBase base = compound.get(path.substring(0, index));
            if (base instanceof NBTTagCompound) {
                return get((NBTTagCompound) base, path.substring(index + 1));
            }
            return null;
        }

        @Override
        public Object get(String path) {
            return getNMSValue(get(handle, path));
        }

        @Override
        public Object set(String path, Object value) {
            Validate.notEmpty(path, "Cannot set to an empty path");
            int right;
            NBTTagCompound current = handle;
            NBTBase base;
            String key;
            while ((right = path.indexOf('.')) != -1) {
                key = path.substring(0, right);
                Validate.notEmpty(key, "Cannot set to an empty path");
                base = current.get(key);
                if (!(base instanceof NBTTagCompound)) {
                    if (value == null) return null;
                    base = new NBTTagCompound();
                    current.set(key, base);
                }
                current = (NBTTagCompound) base;
                path = path.substring(right + 1);
            }
            Validate.notEmpty(path, "Cannot set to an empty path");//这段代码直接从MemorySection那边CV过来的!
            Object ret;
            if (value != null) {
                ret = current.set(path, toNMS(value));
            } else {
                ret = current.get(path);
                current.remove(path);
            }
            return getNMSValue(ret);
        }

        @Override
        public Set<String> keySet(String path) {
            if (path == null) return handle.getKeys();
            NBTBase base = get(handle, path);
            if (base instanceof NBTTagCompound) {
                return ((NBTTagCompound) base).getKeys();
            }
            return null;
        }

        @Override
        public NBTTagWrapper getWrapper(String path) {
            Validate.notEmpty(path, "Cannot getWrapper to an empty path");
            NBTBase base = get(handle, path);
            if (base instanceof NBTTagCompound) {
                return new NBTTagWrapperImpl((NBTTagCompound) base);
            }
            return null;
        }

        @Override
        public void save(ItemStack itemStack) {
            net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
            if (nmsItem != null) {
                nmsItem.setTag(handle);
                itemStack.setItemMeta(CraftItemStack.getItemMeta(nmsItem));
            }
        }

        @Override
        public NBTTagCompound getHandle() {
            return handle;
        }
    }
}
