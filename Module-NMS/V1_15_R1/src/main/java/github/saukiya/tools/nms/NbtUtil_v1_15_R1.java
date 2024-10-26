package github.saukiya.tools.nms;

import github.saukiya.tools.nbt.TagBase;
import github.saukiya.tools.nbt.TagCompound;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import lombok.SneakyThrows;
import net.minecraft.server.v1_15_R1.*;
import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NbtUtil_v1_15_R1 extends NbtUtil {

    @Override
    public ItemWrapper getItemTagWrapper(ItemStack itemStack) {
        return new ItemWrapperImpl(itemStack);
    }

    @Override
    public ItemWrapper getItemTagWrapper(ItemStack itemStack, Object nmsItem) {
        return new ItemWrapperImpl(itemStack, (net.minecraft.server.v1_15_R1.ItemStack) nmsItem);
    }

    @Override
    public Wrapper createTagWrapper(Object nbtTagCompound) {
        return new WrapperImpl((NBTTagCompound) nbtTagCompound);
    }

    @Override
    public net.minecraft.server.v1_15_R1.ItemStack getNMSItem(ItemStack itemStack) {
        return CraftItemStack.asNMSCopy(itemStack);
    }

    @Override
    public void setNMSItem(ItemStack itemStack, Object nmsItem) {
        itemStack.setItemMeta(CraftItemStack.getItemMeta((net.minecraft.server.v1_15_R1.ItemStack) nmsItem));
    }

    @Override
    public NBTTagCompound getNMSItemNBT(Object nmsItem) {
        return ((net.minecraft.server.v1_15_R1.ItemStack) nmsItem).getTag();
    }

    @Override
    public NBTTagCompound getItemNBT(ItemStack itemStack) {
        return getNMSItemNBT(getNMSItem(itemStack));
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
    public NBTTagCompound parseNMSCompound(String json) throws Exception {
        return MojangsonParser.parse(json);
    }

    @Override
    public Object getNMSValue(Object nbtBase) {
        if (nbtBase instanceof NBTBase) {
            switch (((NBTBase) nbtBase).b().b()) {
                case "TAG_Compound":
                    NBTTagCompound nbtTagCompound = (NBTTagCompound) nbtBase;
                    return nbtTagCompound.getKeys().stream().collect(Collectors.toMap(key -> key, key -> getNMSValue(nbtTagCompound.get(key))));
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
        return nbtBase;
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
                ((Map<?, ?>) obj).forEach((key, value) -> {
                    NBTBase base = toNMS(value);
                    if (key != null && base != null) {
                        nbtTagCompound.set(key.toString(), base);
                    }
                });
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
        return null;
    }

    public final class ItemWrapperImpl extends WrapperImpl implements ItemWrapper {

        final net.minecraft.server.v1_15_R1.ItemStack nmsItem;
        final ItemStack itemStack;

        ItemWrapperImpl(ItemStack itemStack) {
            this(itemStack, getNMSItem(itemStack));
        }

        ItemWrapperImpl(ItemStack itemStack, net.minecraft.server.v1_15_R1.ItemStack nmsItem) {
            super(nmsItem.getOrCreateTag());
            if (nmsItem.isEmpty()) throw new NullPointerException();
            this.itemStack = itemStack;
            this.nmsItem = nmsItem;
        }

        @Override
        public void save() {
            setNMSItem(itemStack, nmsItem);
        }
    }

    public class WrapperImpl implements Wrapper {

        final NBTTagCompound handle;

        WrapperImpl(NBTTagCompound tagCompound) {
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

        protected NBTBase set(NBTTagCompound compound, String path, NBTBase value) {
            Validate.notEmpty(path, "Cannot get to an empty path");
            int index = path.indexOf('.');
            if (index == -1) {
                NBTBase ret = compound.get(path);
                if (value != null) {
                    compound.set(path, value);
                } else {
                    compound.remove(path);
                }
                return ret;
            }
            NBTBase base = compound.get(path.substring(0, index));
            if (!(base instanceof NBTTagCompound)) {
                if (value == null) return null;
                compound.set(path.substring(0, index), base = new NBTTagCompound());
            }
            return set((NBTTagCompound) base, path.substring(index + 1), value);
        }

        @Override
        public Object get(String path) {
            return getNMSValue(get(handle, path));
        }

        @Override
        public Object set(String path, Object value) {
            return getNMSValue(set(handle, path, toNMS(value)));
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
        public Wrapper getWrapper(String path) {
            Validate.notEmpty(path, "Cannot getWrapper to an empty path");
            NBTBase base = get(handle, path);
            if (base instanceof NBTTagCompound) {
                return new WrapperImpl((NBTTagCompound) base);
            }
            return null;
        }

        @Nullable
        @Override
        public Object getNBTBase(@Nonnull String path) {
            return get(handle, path);
        }

        @Override
        public NBTTagCompound getHandle() {
            return handle;
        }

        @Nonnull
        @Override
        public int size() {
            return handle.e();
        }

        @Override
        public void save(ItemStack itemStack) {
            net.minecraft.server.v1_15_R1.ItemStack nmsItem = getNMSItem(itemStack);
            if (nmsItem == null) return;
            nmsItem.setTag(handle);
            setNMSItem(itemStack, nmsItem);
        }
    }
}
