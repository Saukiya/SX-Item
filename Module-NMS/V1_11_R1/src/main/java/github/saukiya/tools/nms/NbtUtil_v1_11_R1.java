package github.saukiya.tools.nms;

import github.saukiya.tools.nbt.TagBase;
import github.saukiya.tools.nbt.TagCompound;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import lombok.SneakyThrows;
import net.minecraft.server.v1_11_R1.*;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
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

public class NbtUtil_v1_11_R1 extends NbtUtil {

    @Override
    public ItemWrapper getItemTagWrapper(ItemStack itemStack) {
        return new ItemWrapperImpl(itemStack);
    }

    @Override
    public ItemWrapper getItemTagWrapper(ItemStack itemStack, Object nmsItem) {
        return new ItemWrapperImpl(itemStack, (net.minecraft.server.v1_11_R1.ItemStack) nmsItem);
    }

    @Override
    public Wrapper createTagWrapper(Object nbtTagCompound) {
        return new WrapperImpl((NBTTagCompound) nbtTagCompound);
    }

    @Override
    public net.minecraft.server.v1_11_R1.ItemStack getNMSItem(ItemStack itemStack) {
        return CraftItemStack.asNMSCopy(itemStack);
    }

    @Override
    public void setNMSItem(ItemStack itemStack, Object nmsItem) {
        itemStack.setItemMeta(CraftItemStack.getItemMeta((net.minecraft.server.v1_11_R1.ItemStack) nmsItem));
    }

    @Override
    public NBTTagCompound getNMSItemNBT(Object nmsItem) {
        return ((net.minecraft.server.v1_11_R1.ItemStack) nmsItem).getTag();
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
        return NBTCompressedStreamTools.a(new DataInputStream(new ByteBufInputStream(buf)));
    }

    @Override
    public NBTTagCompound parseNMSCompound(String json) throws Exception {
        return MojangsonParser.parse(json);
    }

    @Override
    public Object getNMSValue(Object nbtBase) {
        if (nbtBase instanceof NBTBase) {
            switch (((NBTBase) nbtBase).getTypeId()) {
                case 10:
                    NBTTagCompound nbtTagCompound = (NBTTagCompound) nbtBase;
                    return nbtTagCompound.c().stream().collect(Collectors.toMap(key -> key, key -> getNMSValue(nbtTagCompound.get(key))));
                case 9:
                    NBTTagList nbtTagList = (NBTTagList) nbtBase;
                    return IntStream.range(0, nbtTagList.size()).mapToObj(i -> getNMSValue(nbtTagList.h(i))).collect(Collectors.toList());
                case 7:
                    return ((NBTTagByteArray) nbtBase).c();
                case 11:
                    return ((NBTTagIntArray) nbtBase).d();
                case 8:
                    return ((NBTTagString) nbtBase).c_();
                case 1:
                    return ((NBTTagByte) nbtBase).g();
                case 2:
                    return ((NBTTagShort) nbtBase).f();
                case 3:
                    return ((NBTTagInt) nbtBase).e();
                case 4:
                    return ((NBTTagLong) nbtBase).d();
                case 5:
                    return ((NBTTagFloat) nbtBase).i();
                case 6:
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
                List list = (List) obj;
                NBTTagList nbtTagList = new NBTTagList();
                for (Object o : list) nbtTagList.add(toNMS(o));
                return nbtTagList;
            } else if (obj instanceof byte[]) {
                return new NBTTagByteArray((byte[]) obj);
            } else if (obj instanceof int[]) {
                return new NBTTagIntArray((int[]) obj);
            } else if (obj.getClass().isArray()) {
                NBTTagList nbtTagList = new NBTTagList();
                IntStream.range(0, Array.getLength(obj)).mapToObj(i -> toNMS(Array.get(obj, i))).forEach(nbtTagList::add);
                return nbtTagList;
            } else if (obj instanceof String) {
                return new NBTTagString(obj.toString());
            } else if (obj instanceof Boolean) {
                return new NBTTagByte((byte) ((Boolean) obj ? 1 : 0));
            } else if (obj instanceof Byte) {
                return new NBTTagByte(((Number) obj).byteValue());
            } else if (obj instanceof Short) {
                return new NBTTagShort(((Number) obj).shortValue());
            } else if (obj instanceof Integer) {
                return new NBTTagInt(((Number) obj).intValue());
            } else if (obj instanceof Long) {
                return new NBTTagLong(((Number) obj).longValue());
            } else if (obj instanceof Float) {
                return new NBTTagFloat(((Number) obj).floatValue());
            } else if (obj instanceof Double) {
                return new NBTTagDouble(((Number) obj).doubleValue());
            }
        }
        return null;
    }

    public final class ItemWrapperImpl extends WrapperImpl implements ItemWrapper {

        final net.minecraft.server.v1_11_R1.ItemStack nmsItem;
        final ItemStack itemStack;

        ItemWrapperImpl(ItemStack itemStack) {
            this(itemStack, getNMSItem(itemStack));
        }

        ItemWrapperImpl(ItemStack itemStack, net.minecraft.server.v1_11_R1.ItemStack nmsItem) {
            super(getNMSItemNBT(nmsItem));
            if (itemStack.getType() == Material.AIR) throw new NullPointerException();
            this.itemStack = itemStack;
            this.nmsItem = nmsItem;
            if (!nmsItem.hasTag()) nmsItem.setTag(handle);
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
            if (path == null) return handle.c();
            NBTBase base = get(handle, path);
            if (base instanceof NBTTagCompound) {
                return ((NBTTagCompound) base).c();
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

        @Override
        public int size() {
            return handle.d();
        }

        @Override
        public void save(ItemStack itemStack) {
            net.minecraft.server.v1_11_R1.ItemStack nmsItem = getNMSItem(itemStack);
            if (nmsItem == null) return;
            nmsItem.setTag(handle);
            setNMSItem(itemStack, nmsItem);
        }
    }
}
