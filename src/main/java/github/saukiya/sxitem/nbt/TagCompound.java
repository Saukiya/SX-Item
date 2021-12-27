package github.saukiya.sxitem.nbt;

import lombok.NoArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;

@NoArgsConstructor
public class TagCompound implements TagBase<HashMap<String, ?>>, CompoundBase {

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
                    TagBase tagBase = TagType.getMethods(typeId).readTagBase(dataInput, depth + 1);
                    tagCompound.handle.put(key, tagBase);
                }
                return tagCompound;
            }
        }

        @Override
        public TagCompound toTag(Object object) {
            TagCompound tagCompound = null;
            if (object instanceof Map) {
                tagCompound = new TagCompound();
                Map<String, ?> map = (Map<String, ?>) object;
                for (Map.Entry<String, ?> entry : map.entrySet()) {
                    tagCompound.handle.put(entry.getKey(), TagType.toTag(entry.getValue()));
                }
            }
            if (object instanceof ConfigurationSection) {
                tagCompound = new TagCompound();
                ConfigurationSection section = (ConfigurationSection) object;
                for (String key : section.getKeys(false)) {
                    tagCompound.handle.put(key, TagType.toTag(section.get(key)));
                }
            }
            return tagCompound;
        }
    };

    private final Map<String, TagBase> handle = new HashMap<>();

    public TagCompound(Map<String, TagBase> tagBaseMap) {
        handle.putAll(tagBaseMap);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        for (Map.Entry<String, TagBase> entry : entrySet()) {
            TagBase tagBase = entry.getValue();
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
    public TagBase get(String path) {
        int index = path.indexOf('.');
        if (index == -1) return handle.get(path);
        TagBase tagBase = handle.get(path.substring(0, index));
        if (tagBase instanceof TagCompound) {
            return ((TagCompound) tagBase).get(path.substring(index + 1));
        }
        return null;
    }

    @Override
    public TagBase set(String path, Object value) {
        int index = path.indexOf('.');
        if (index == -1) {
            if (value == null) return handle.remove(path);
            return handle.put(path, TagType.toTag(value));
        }
        String key = path.substring(0, index);
        TagBase tagBase = handle.get(key);
        if (!(tagBase instanceof TagCompound)) {
            if (value == null) return null;
            tagBase = new TagCompound();
            handle.put(key, tagBase);
        }
        return ((TagCompound) tagBase).set(path.substring(index + 1), value);
    }

    @Override
    public Set<String> keySet(@Nullable String path) {
        return handle.keySet();
    }

    @Override
    public <V> V get(String path, Class<V> t) {
        TagBase obj = get(path);
        if (obj != null && t.isAssignableFrom(obj.getValue().getClass())) {
            return (V) obj.getValue();
        }
        return null;
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

    @Nonnull
    public Set<Map.Entry<String, TagBase>> entrySet() {
        return handle.entrySet();
    }
}
