package github.saukiya.tools.nms;

import github.saukiya.tools.nbt.TagBase;
import github.saukiya.tools.nbt.TagCompound;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import lombok.SneakyThrows;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.*;
import net.minecraft.world.item.component.CustomData;
import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
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

/**
 * NBT工具实现 [26.1.x] - Mojang映射(反混淆)
 */
@SuppressWarnings({"NullableProblems", "unchecked", "unused"})
public class NbtUtil_v26_1_R1 extends NbtUtil {

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
        return new WrapperImpl((CompoundTag) nbtTagCompound);
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
    public CompoundTag getNMSItemNBT(Object nmsItem) {
        CustomData data = ((net.minecraft.world.item.ItemStack) nmsItem).getComponents().get(DataComponents.CUSTOM_DATA);
        return data != null ? data.copyTag() : new CompoundTag();
    }

    @Override
    public CompoundTag getItemNBT(ItemStack itemStack) {
        return getNMSItemNBT(getNMSItem(itemStack));
    }

    @SneakyThrows
    @Override
    public TagCompound asTagCompoundCopy(Object nbtTagCompound) {
        ByteBuf buf = Unpooled.buffer();
        NbtIo.write((CompoundTag) nbtTagCompound, (DataOutput) new ByteBufOutputStream(buf));
        return (TagCompound) readTagBase(new ByteBufInputStream(buf));
    }

    @SneakyThrows
    @Override
    public CompoundTag asNMSCompoundCopy(TagCompound tagCompound) {
        ByteBuf buf = Unpooled.buffer();
        writeTagBase(tagCompound, new ByteBufOutputStream(buf));
        return NbtIo.read(new DataInputStream(new ByteBufInputStream(buf)));
    }

    @Override
    public CompoundTag parseNMSCompound(String json) throws Exception {
        return TagParser.parseCompoundFully(json);
    }

    @Override
    public Object getNMSValue(Object nbtBase) {
        if (nbtBase instanceof Tag) {
            Tag tag = (Tag) nbtBase;
            byte id = tag.getId();
            if (id == Tag.TAG_COMPOUND) {
                return getSet((CompoundTag) tag).stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> getNMSValue(entry.getValue())));
            } else if (id == Tag.TAG_LIST) {
                return ((ListTag) tag).stream().map(this::getNMSValue).collect(Collectors.toList());
            } else if (id == Tag.TAG_BYTE_ARRAY) {
                return ((ByteArrayTag) tag).getAsByteArray();
            } else if (id == Tag.TAG_INT_ARRAY) {
                return ((IntArrayTag) tag).getAsIntArray();
            } else if (id == Tag.TAG_LONG_ARRAY) {
                return ((LongArrayTag) tag).getAsLongArray();
            } else if (id == Tag.TAG_STRING) {
                return ((StringTag) tag).value();
            } else if (id == Tag.TAG_BYTE) {
                return ((NumericTag) tag).byteValue();
            } else if (id == Tag.TAG_SHORT) {
                return ((NumericTag) tag).shortValue();
            } else if (id == Tag.TAG_INT) {
                return ((NumericTag) tag).intValue();
            } else if (id == Tag.TAG_LONG) {
                return ((NumericTag) tag).longValue();
            } else if (id == Tag.TAG_FLOAT) {
                return ((NumericTag) tag).floatValue();
            } else if (id == Tag.TAG_DOUBLE) {
                return ((NumericTag) tag).doubleValue();
            }
        }
        return nbtBase;
    }

    @Override
    public Tag toNMS(Object obj) {
        if (obj != null) {
            if (obj instanceof TagBase) {
                return toNMS(((TagBase<?>) obj).getValue());
            } else if (obj instanceof Tag) {
                return (Tag) obj;
            } else if (obj instanceof Map) {
                CompoundTag compoundTag = new CompoundTag();
                ((Map<?, ?>) obj).forEach((key, value) -> {
                    Tag base = toNMS(value);
                    if (key != null && base != null) {
                        compoundTag.put(key.toString(), base);
                    }
                });
                return compoundTag;
            } else if (obj instanceof List) {
                return new ListTag(((List<?>) obj).stream().map(this::toNMS).collect(Collectors.toList()));
            } else if (obj instanceof byte[]) {
                return new ByteArrayTag((byte[]) obj);
            } else if (obj instanceof int[]) {
                return new IntArrayTag((int[]) obj);
            } else if (obj instanceof long[]) {
                return new LongArrayTag((long[]) obj);
            } else if (obj.getClass().isArray()) {
                return new ListTag(IntStream.range(0, Array.getLength(obj)).mapToObj(i -> toNMS(Array.get(obj, i))).collect(Collectors.toList()));
            } else if (obj instanceof String) {
                return StringTag.valueOf(obj.toString());
            } else if (obj instanceof Boolean) {
                return ByteTag.valueOf((Boolean) obj);
            } else if (obj instanceof Byte) {
                return ByteTag.valueOf(((Number) obj).byteValue());
            } else if (obj instanceof Short) {
                return ShortTag.valueOf(((Number) obj).shortValue());
            } else if (obj instanceof Integer) {
                return IntTag.valueOf(((Number) obj).intValue());
            } else if (obj instanceof Long) {
                return LongTag.valueOf(((Number) obj).longValue());
            } else if (obj instanceof Float) {
                return FloatTag.valueOf(((Number) obj).floatValue());
            } else if (obj instanceof Double) {
                return DoubleTag.valueOf(((Number) obj).doubleValue());
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
            if (nmsItem.isEmpty()) throw new NullPointerException();
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

        final CompoundTag handle;

        WrapperImpl(CompoundTag tagCompound) {
            handle = tagCompound != null ? tagCompound : new CompoundTag();
        }

        protected Tag get(CompoundTag compound, String path) {
            Validate.notEmpty(path, "Cannot get to an empty path");
            int index = path.indexOf('.');
            if (index == -1) return getNbtBase(compound, path);
            Tag base = getNbtBase(compound, path.substring(0, index));
            if (base instanceof CompoundTag) {
                return get((CompoundTag) base, path.substring(index + 1));
            }
            return null;
        }

        protected Tag set(CompoundTag compound, String path, Tag value) {
            Validate.notEmpty(path, "Cannot get to an empty path");
            int index = path.indexOf('.');
            if (index == -1) {
                Tag ret = getNbtBase(compound, path);
                if (value != null) {
                    compound.put(path, value);
                } else {
                    compound.remove(path);
                }
                return ret;
            }
            Tag base = getNbtBase(compound, path.substring(0, index));
            if (!(base instanceof CompoundTag)) {
                if (value == null) return null;
                compound.put(path.substring(0, index), base = new CompoundTag());
            }
            return set((CompoundTag) base, path.substring(index + 1), value);
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
            Tag base = get(handle, path);
            if (base instanceof CompoundTag) {
                return getKeySet((CompoundTag) base);
            }
            return null;
        }

        @Override
        public Wrapper getWrapper(String path) {
            Validate.notEmpty(path, "Cannot getWrapper to an empty path");
            Tag base = get(handle, path);
            if (base instanceof CompoundTag) {
                return new WrapperImpl((CompoundTag) base);
            }
            return null;
        }

        @Nullable
        @Override
        public Object getNBTBase(@Nonnull String path) {
            return get(handle, path);
        }

        @Override
        public CompoundTag getHandle() {
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

    private static void setNMSItemNBT(net.minecraft.world.item.ItemStack nmsItem, CompoundTag nbt) {
        nmsItem.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
    }

    private static int getSize(CompoundTag nbt) {
        return nbt.size();
    }

    private static Set<String> getKeySet(CompoundTag base) {
        return base.keySet();
    }

    private static Set<Map.Entry<String, Tag>> getSet(CompoundTag nbt) {
        return nbt.entrySet();
    }

    private static Tag getNbtBase(CompoundTag compound, String path) {
        return compound.get(path);
    }
}
