package github.saukiya.sxitem.nms;

import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TagCompound extends HashMap<String, TagBase> implements TagBase<HashMap<String, ?>> {

    protected static final TagType.Method<TagCompound> typeMethod = (dataInput, depth) -> {
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
    };

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
            sb.append(key).append(':').append(String.valueOf(get(key)));
        }
        sb.append('}');
        return sb.toString();
    }

    public <T extends TagBase> T get(String key) {
        TagBase tagBase;
        if (key.length() == 0) {
            tagBase = this;
        } else {
            char separator = '.';
            int i1 = -1;
            Object tagCompound = this;

            int i2;
            while ((i1 = key.indexOf(separator, i2 = i1 + 1)) != -1) {
                tagCompound = ((TagCompound) tagCompound).get(key.substring(i2,i1));
                if (tagCompound == null) {
                    return (T) TagEnd.getInst();
                }
            }

            key = key.substring(i2);

            if (tagCompound == this) {
                tagBase = super.get(key);
            } else {
                tagBase = ((TagCompound) tagCompound).get(key);
            }
        }
        return (T) (tagBase == null ? TagEnd.getInst() : tagBase);
    }
}
