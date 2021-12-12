package github.saukiya.sxitem.util;

import github.saukiya.sxitem.nms.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import lombok.SneakyThrows;
import net.minecraft.server.v1_13_R2.*;
import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class NbtUtil_v1_13_R2 extends NbtUtil {

    private final NBTTagEnd nbtTagEnd = new NBTTagEnd();

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
                return ((NBTTagByteArray) nbtBase).c();
            } else if (nbtBase instanceof NBTTagIntArray) {
                return ((NBTTagIntArray) nbtBase).d();
            } else if (nbtBase instanceof NBTTagLongArray) {
                return ((NBTTagLongArray) nbtBase).d();
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
            List list = (List) obj;
            NBTTagList nbtTagList = new NBTTagList();
            for (Object o : list) nbtTagList.add(toNMS(o));
            return nbtTagList;
        } else if (obj instanceof byte[]) {
            return new NBTTagByteArray((byte[]) obj);
        } else if (obj instanceof int[]) {
            return new NBTTagIntArray((int[]) obj);
        } else if (obj instanceof long[]) {
            return new NBTTagLongArray((long[]) obj);
        } else if (obj instanceof String) {
            return new NBTTagString(obj.toString());
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
        return nbtTagEnd;
    }

    public class NBTTagWrapperImpl implements NBTTagWrapper {

        protected final NBTTagCompound handle;

        protected NBTTagWrapperImpl(NBTTagCompound tagCompound) {
            if (tagCompound == null) throw new NullPointerException();
            this.handle = tagCompound;
        }

        protected NBTBase get(NBTTagCompound compound, String path) {
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
            Validate.notEmpty(path, "Cannot get to an empty path");
            return getNMSValue(get(handle, path));
        }

        @Override
        public Object set(String path, Object value) {
            Validate.notEmpty(path, "Cannot set to an empty path");
            int right;
            NBTTagCompound current = handle;
            NBTBase base;
            while ((right = path.indexOf('.')) != -1) {
                base = current.get(path.substring(0, right));
                if (!(base instanceof NBTTagCompound)) {
                    base = new NBTTagCompound();
                    current.set(path.substring(0, right), base);
                }
                current = (NBTTagCompound) base;
                path = path.substring(right + 1);
            }
            Validate.notEmpty(path, "Cannot set to an empty path");//这段代码直接从MemorySection那边CV过来的!
            Object ret = current.get(path);
            if (value != null) {
                current.set(path, toNMS(value));
            } else {
                current.remove(path);
            }
            return getNMSValue(ret);
        }

        @Override
        public Object remove(String path) {
            Validate.notEmpty(path, "Cannot remove to an empty path");
            int lastIndex = path.lastIndexOf('.');
            NBTTagCompound tagCompound = handle;
            if (lastIndex != -1) {
                NBTBase base = get(handle, path.substring(0, lastIndex));
                if (base instanceof NBTTagCompound) {
                    tagCompound = (NBTTagCompound) base;
                    path = path.substring(lastIndex + 1);
                } else {
                    return null;
                }
            }
            Object ret = tagCompound.get(path);
            tagCompound.remove(path);
            return getNMSValue(ret);
        }

        @Override
        public Set<String> getKeys(String path) {
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
        public Object getHandle() {
            return handle;
        }
    }
}
