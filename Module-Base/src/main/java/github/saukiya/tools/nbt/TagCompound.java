package github.saukiya.tools.nbt;

import github.saukiya.tools.base.ICompound;
import lombok.NoArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;

@NoArgsConstructor
public class TagCompound implements TagBase<HashMap<String, ?>>, ICompound {

    protected static final TagType.Method typeMethod = new TagType.Method() {
        @Override
        public TagCompound readTagBase(DataInput dataInput, int depth) throws IOException {
            if (depth > 512) {
                throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
            } else {
                TagCompound tagCompound = new TagCompound();

                byte typeId;
                while ((typeId = dataInput.readByte()) != 0) {
                    String key = dataInput.readUTF();
                    TagBase<?> tagBase = TagType.getMethods(typeId).readTagBase(dataInput, depth + 1);
                    tagCompound.handle.put(key, tagBase);
                }
                return tagCompound;
            }
        }

        @Override
        public TagCompound toTag(Object object) {
            if (object instanceof Map) {
                return new TagCompound((Map) object);
            }
            if (object instanceof ConfigurationSection) {
                return new TagCompound(((ConfigurationSection) object).getValues(false));
            }
            return null;
        }
    };

    private final Map<String, TagBase<?>> handle = new HashMap<>();

    public TagCompound(Map<?, ?> tagBaseMap) {
        tagBaseMap.forEach((key, value) -> {
            if (key != null) set(key.toString(), value);
        });
    }

    public TagCompound(ICompound compoundBase) {
        compoundBase.keySet().forEach(key -> set(key, compoundBase.get(key)));
    }

    public TagBase<?> getTagBase(String path) {
        int index = path.indexOf('.');
        if (index == -1) return handle.get(path);
        TagBase<?> tagBase = handle.get(path.substring(0, index));
        if (tagBase instanceof TagCompound) {
            return ((TagCompound) tagBase).getTagBase(path.substring(index + 1));
        }
        return null;
    }

    public TagBase setTagBase(String path, TagBase value) {
        int index = path.indexOf('.');
        if (index == -1) {
            if (value == null) return handle.remove(path);
            return handle.put(path, value);
        }
        String key = path.substring(0, index);
        TagBase tagBase = handle.get(key);
        if (!(tagBase instanceof TagCompound)) {
            if (value == null) return null;
            tagBase = new TagCompound();
            handle.put(key, tagBase);
        }
        return ((TagCompound) tagBase).setTagBase(path.substring(index + 1), value);
    }

    @Nonnull
    public Set<Map.Entry<String, TagBase<?>>> entrySet() {
        return handle.entrySet();
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        for (Map.Entry<String, TagBase<?>> entry : entrySet()) {
            TagBase<?> tagBase = entry.getValue();
            dataOutput.writeByte(tagBase.getTypeId().getId());
            if (tagBase.getTypeId().getId() != 0) {
                dataOutput.writeUTF(entry.getKey());
                tagBase.write(dataOutput);
            }
        }
        dataOutput.writeByte(0);
    }

    @Override
    public HashMap<String, ?> getValue() {
        HashMap<String, Object> map = new HashMap<>();
        handle.forEach((key, value) -> map.put(key, value.getValue()));
        return map;
    }

    @Override
    public TagType getTypeId() {
        return TagType.COMPOUND;
    }

    @Override
    public Object get(String path) {
        TagBase<?> tagBase = getTagBase(path);
        return tagBase != null ? tagBase.getValue() : null;
    }

    @Override
    public Object set(String path, Object value) {
        TagBase<?> tagBase = setTagBase(path, value == null ? null : TagType.toTag(value));
        return tagBase != null ? tagBase.getValue() : null;
    }

    @Override
    public Set<String> keySet(@Nullable String path) {
        return handle.keySet();
    }

    @Override
    public int size() {
        return handle.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        List<String> list = new ArrayList<>(keySet());
        Collections.sort(list);

        String key;
        for (int i = 0; i < list.size(); i++) {
            if (i != 0) sb.append(',');
            key = list.get(i);
            sb.append(key).append(':').append(handle.get(key));
        }
        sb.append('}');
        return sb.toString();
    }
}
