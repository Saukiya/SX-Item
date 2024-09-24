package github.saukiya.util.nms;

import github.saukiya.util.nbt.TagBase;
import github.saukiya.util.nbt.TagCompound;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import lombok.SneakyThrows;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.*;
import net.minecraft.world.item.component.CustomData;
import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NbtUtil_v1_21_R1 extends NbtUtil {

    @Override
    public ItemWrapper getItemTagWrapper(ItemStack itemStack) {
        return new ItemWrapperImpl(itemStack);
    }

    @Override
    public ItemWrapper getItemTagWrapper(ItemStack itemStack, Object nmsItem) {
        return new ItemWrapperImpl(itemStack, (net.minecraft.world.item.ItemStack) nmsItem);
    }

    @Override
    public Wrapper createTagWrapper(Object nbtTagCompound) {
        return new WrapperImpl((NBTTagCompound) nbtTagCompound);
    }

    @Override
    public net.minecraft.world.item.ItemStack getNMSItem(ItemStack itemStack) {
        return CraftItemStack.asNMSCopy(itemStack);
    }

    @Override
    public void setNMSItem(ItemStack itemStack, Object nmsItem) {
        itemStack.setItemMeta(CraftItemStack.getItemMeta((net.minecraft.world.item.ItemStack) nmsItem));
    }

    @Override
    public NBTTagCompound getNMSItemNBT(Object nmsItem) {
        var dataComponentMap = ((net.minecraft.world.item.ItemStack) nmsItem).a();
        CustomData data = dataComponentMap.a(DataComponents.b);
        return data != null ? data.d() : new NBTTagCompound();
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
        return MojangsonParser.a(json);
    }

    @Override
    public Object getNMSValue(Object nbtBase) {
        if (nbtBase instanceof NBTBase) {
            switch (((NBTBase) nbtBase).c().b()) {
                case "TAG_Compound":
                    NBTTagCompound nbtTagCompound = (NBTTagCompound) nbtBase;
                    return nbtTagCompound.e().stream().collect(Collectors.toMap(key -> key, key -> getNMSValue(nbtTagCompound.c(key))));
                case "TAG_List":
                    return ((NBTTagList) nbtBase).stream().map(this::getNMSValue).collect(Collectors.toList());
                case "TAG_Byte_Array":
                    return ((NBTTagByteArray) nbtBase).e();
                case "TAG_Int_Array":
                    return ((NBTTagIntArray) nbtBase).g();
                case "TAG_Long_Array":
                    return ((NBTTagLongArray) nbtBase).g();
                case "TAG_String":
                    return ((NBTTagString) nbtBase).s_();
                case "TAG_Byte":
                    return ((NBTTagByte) nbtBase).i();
                case "TAG_Short":
                    return ((NBTTagShort) nbtBase).h();
                case "TAG_Int":
                    return ((NBTTagInt) nbtBase).g();
                case "TAG_Long":
                    return ((NBTTagLong) nbtBase).f();
                case "TAG_Float":
                    return ((NBTTagFloat) nbtBase).k();
                case "TAG_Double":
                    return ((NBTTagDouble) nbtBase).j();
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
                        nbtTagCompound.a(key.toString(), base);
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

        final net.minecraft.world.item.ItemStack nmsItem;
        final ItemStack itemStack;

        ItemWrapperImpl(ItemStack itemStack) {
            this(itemStack, getNMSItem(itemStack));
        }

        ItemWrapperImpl(ItemStack itemStack, net.minecraft.world.item.ItemStack nmsItem) {
            super(getNMSItemNBT(nmsItem));
            if (nmsItem.e()) throw new NullPointerException();
            this.itemStack = itemStack;
            this.nmsItem = nmsItem;
        }

        @Override
        public void save() {
            nmsItem.b(DataComponents.b, CustomData.a(handle));
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
            if (index == -1) return compound.c(path);
            NBTBase base = compound.c(path.substring(0, index));
            if (base instanceof NBTTagCompound) {
                return get((NBTTagCompound) base, path.substring(index + 1));
            }
            return null;
        }

        protected NBTBase set(NBTTagCompound compound, String path, NBTBase value) {
            Validate.notEmpty(path, "Cannot get to an empty path");
            int index = path.indexOf('.');
            if (index == -1) {
                NBTBase ret = compound.c(path);
                if (value != null) {
                    compound.a(path, value);
                } else {
                    compound.r(path);
                }
                return ret;
            }
            NBTBase base = compound.c(path.substring(0, index));
            if (!(base instanceof NBTTagCompound)) {
                if (value == null) return null;
                compound.a(path.substring(0, index), base = new NBTTagCompound());
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
            if (path == null) return handle.e();
            NBTBase base = get(handle, path);
            if (base instanceof NBTTagCompound) {
                return ((NBTTagCompound) base).e();
            }
            return null;
        }

        @Override
        public int size() {
            return handle.f();
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

        @Override
        public void save(ItemStack itemStack) {
            net.minecraft.world.item.ItemStack nmsItem = getNMSItem(itemStack);
            if (nmsItem == null) return;
            nmsItem.b(DataComponents.b, CustomData.a(handle));
            setNMSItem(itemStack, nmsItem);
        }

        @Override
        public NBTTagCompound getHandle() {
            return handle;
        }
    }
}
