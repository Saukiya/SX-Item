package github.saukiya.sxitem.nms;

import lombok.NoArgsConstructor;
import org.bukkit.configuration.MemorySection;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;

//TODO 需要与NBTTagWrapper作出相同的功能
@NoArgsConstructor
public class TagCompound extends HashMap<String, TagBase> implements TagBase<HashMap<String, ?>> {

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
                    tagCompound.put(key, tagBase);
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
                for (Entry<String, ?> entry : map.entrySet()) {
                    tagCompound.put(entry.getKey(), TagType.toTag(entry.getValue()));
                }
            }
            if (object instanceof MemorySection) {
                tagCompound = new TagCompound();
                MemorySection memorySection = (MemorySection) object;
                for (String key : memorySection.getKeys(false)) {
                    tagCompound.put(key, TagType.toTag(memorySection.get(key)));
                }
            }
            return tagCompound;
        }
    };

    public TagCompound(Map<String, TagBase> tagBaseMap) {
        super(tagBaseMap);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        for (Entry<String, TagBase> entry : this.entrySet()) {
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
        this.forEach((key, value) -> map.put(key, value.getValue()));
        return map;
    }

    @Override
    public TagType getTypeId() {
        return TagType.COMPOUND;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        List<String> list = new ArrayList<>(keySet());
        Collections.sort(list);

        String key;
        for (int i = 0; i < list.size(); i++) {
            if (i != 0) {
                sb.append(',');
            }
            key = list.get(i);
            sb.append(key).append(':').append(super.get(key));
        }
        sb.append('}');
        return sb.toString();
    }

    public <T extends TagBase> T get(String path) {
        TagBase tagBase;
        if (path.length() == 0) {
            tagBase = this;
        } else {
            char separator = '.';
            int i1 = -1;
            Object tagCompound = this;

            int i2;
            while ((i1 = path.indexOf(separator, i2 = i1 + 1)) != -1) {
                tagCompound = ((TagCompound) tagCompound).get(path.substring(i2,i1));
                if (tagCompound == null) {
                    return (T) TagEnd.getInst();
                }
            }

            path = path.substring(i2);

            if (tagCompound == this) {
                tagBase = super.get(path);
            } else {
                tagBase = ((TagCompound) tagCompound).get(path);
            }
        }
        return (T) (tagBase == null ? TagEnd.getInst() : tagBase);
    }
}
