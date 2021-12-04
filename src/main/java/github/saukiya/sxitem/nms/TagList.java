package github.saukiya.sxitem.nms;


import java.io.DataOutput;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
public class TagList extends TagListBase<TagBase> {

    protected static final TagType.Method<TagList> typeMethod = (dataInput, depth) -> {
        if (depth > 512) {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
        } else {
            byte typeId = dataInput.readByte();
            int length = dataInput.readInt();
            if (typeId == 0 && length > 0) {
                throw new RuntimeException("Missing type on ListTag");
            } else {
                TagType.Method method = TagType.getMethods(typeId);
                TagList tagList = new TagList();
                for (int i = 0; i < length; ++i) {
                    tagList.add(method.readTagBase(dataInput, depth + 1));
                }
                return tagList;
            }
        }
    };

    public TagList(Collection<TagBase> tagBases) {
        super(tagBases);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        TagType type = isEmpty() ? TagType.END : get(0).getTypeId();
        dataOutput.writeByte(type.getId());
        dataOutput.writeInt(size());
        for (TagBase tagBase : this) {
            tagBase.write(dataOutput);
        }
    }

    @Override
    public TagType getTypeId() {
        return TagType.LIST;
    }
}
