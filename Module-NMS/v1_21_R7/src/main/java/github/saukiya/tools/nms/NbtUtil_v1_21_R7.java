package github.saukiya.tools.nms;

import github.saukiya.tools.nbt.TagBase;
import github.saukiya.tools.nbt.TagCompound;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import lombok.SneakyThrows;
import lombok.val;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.*;
import net.minecraft.world.item.component.CustomData;
import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.v1_21_R6.inventory.CraftItemStack;
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

@SuppressWarnings({"NullableProblems", "unchecked", "unused"})
public class NbtUtil_v1_21_R7 extends NbtUtil {

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
        val dataComponentMap = ((net.minecraft.world.item.ItemStack) nmsItem).a();
        CustomData data = dataComponentMap.a(DataComponents.b);
        return data != null ? data.b() : new NBTTagCompound();
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
            switch (getType((NBTBase) nbtBase)) {
                case "TAG_Compound":
                    NBTTagCompound nbtTagCompound = (NBTTagCompound) nbtBase;
                    return getSet(nbtTagCompound).stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> getNMSValue(entry.getValue())));
                case "TAG_List":
                    return ((NBTTagList) nbtBase).stream().map(this::getNMSValue).collect(Collectors.toList());
                case "TAG_Byte_Array":
                    return getByteArray((NBTTagByteArray) nbtBase);
                case "TAG_Int_Array":
                    return getIntArray((NBTTagIntArray) nbtBase);
                case "TAG_Long_Array":
                    return getLongArray((NBTTagLongArray) nbtBase);
                case "TAG_String":
                    return getString((NBTTagString) nbtBase);
                case "TAG_Byte":
                    return getByte((NBTTagByte) nbtBase);
                case "TAG_Short":
                    return getShort((NBTTagShort) nbtBase);
                case "TAG_Int":
                    return getInt((NBTTagInt) nbtBase);
                case "TAG_Long":
                    return getLong((NBTTagLong) nbtBase);
                case "TAG_Float":
                    return getFloat((NBTTagFloat) nbtBase);
                case "TAG_Double":
                    return getDouble((NBTTagDouble) nbtBase);
            }
        }
        return nbtBase;
    }

    @Override
    public NBTBase toNMS(Object obj) {
        if (obj != null) {
            if (obj instanceof TagBase) {
                return toNMS(((TagBase<?>) obj).getValue());
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
            if (nmsItem.f()) throw new NullPointerException();
            this.itemStack = itemStack;
            this.nmsItem = nmsItem;
        }

        @Override
        public void save() {
            setNMSItemNBT(nmsItem, handle);
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
            if (index == -1) return getNbtBase(compound, path);
            NBTBase base = getNbtBase(compound, path.substring(0, index));
            if (base instanceof NBTTagCompound) {
                return get((NBTTagCompound) base, path.substring(index + 1));
            }
            return null;
        }

        protected NBTBase set(NBTTagCompound compound, String path, NBTBase value) {
            Validate.notEmpty(path, "Cannot get to an empty path");
            int index = path.indexOf('.');
            if (index == -1) {
                NBTBase ret = getNbtBase(compound, path);
                if (value != null) {
                    compound.a(path, value);
                } else {
                    compound.r(path);
                }
                return ret;
            }
            NBTBase base = getNbtBase(compound, path.substring(0, index));
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
            if (path == null) return getKeySet(handle);
            NBTBase base = get(handle, path);
            if (base instanceof NBTTagCompound) {
                return getKeySet((NBTTagCompound) base);
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
            return getSize(handle);
        }

        @Override
        public void save(ItemStack itemStack) {
            net.minecraft.world.item.ItemStack nmsItem = getNMSItem(itemStack);
            if (nmsItem == null) return;
            setNMSItemNBT(nmsItem, handle);
            setNMSItem(itemStack, nmsItem);
        }
    }

    private static void setNMSItemNBT(net.minecraft.world.item.ItemStack nmsItem, NBTTagCompound nbt) {
        nmsItem.b(DataComponents.b, CustomData.a(nbt));
    }

    private static int getSize(NBTTagCompound nbt) {
        return nbt.i();
    }

    private static Set<String> getKeySet(NBTTagCompound base) {
        return base.e();
    }

    private static Set<Map.Entry<String, NBTBase>> getSet(NBTTagCompound nbt) {
        return nbt.g();
    }

    private static String getType(NBTBase nbtBase) {
        return nbtBase.c().b();
    }

    private static byte[] getByteArray(NBTTagByteArray nbt) {
        return nbt.e();
    }

    private static int[] getIntArray(NBTTagIntArray nbt) {
        return nbt.g();
    }

    private static long[] getLongArray(NBTTagLongArray nbt) {
        return nbt.g();
    }

    private static String getString(NBTTagString nbt) {
        return nbt.k();
    }

    private static byte getByte(NBTTagByte nbt) {
        return nbt.j();
    }

    private static short getShort(NBTTagShort nbt) {
        return nbt.i();
    }

    private static int getInt(NBTTagInt nbt) {
        return nbt.h();
    }

    private static long getLong(NBTTagLong nbt) {
        return nbt.g();
    }

    private static float getFloat(NBTTagFloat nbt) {
        return nbt.l();
    }

    private static double getDouble(NBTTagDouble nbt) {
        return nbt.k();
    }

    private static NBTBase getNbtBase(NBTTagCompound compound, String path) {
        return compound.a(path);
    }
}
