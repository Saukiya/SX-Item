package github.saukiya.sxitem.nms;

import github.saukiya.sxitem.util.NMS;
import lombok.NoArgsConstructor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
public class TagList extends TagListBase<TagBase, List<?>> {

    protected static final TagType.Method typeMethod = new TagType.Method() {
        @Override
        public TagList readTagBase(DataInput dataInput, int depth) throws IOException {
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
        }

        @Override
        public TagListBase toTag(Object object) {
            if (object instanceof List) {
                List<?> objectList = (List<?>) object;
                if (!objectList.isEmpty()) {
                    Set<Class> classSet = objectList.stream().map(Object::getClass).collect(Collectors.toSet());
                    if (classSet.contains(Long.class) && NMS.compareTo("v1_12_R1") > -1) {
                        return objectList.stream().map(o -> ((Number) o).longValue()).collect(Collectors.toCollection(TagLongArray::new));
                    } else if (classSet.contains(Integer.class)) {
                        return objectList.stream().map(o -> ((Number) o).intValue()).collect(Collectors.toCollection(TagIntArray::new));
                    } else if (classSet.contains(Byte.class)) {
                        return objectList.stream().map(o -> ((Number) o).byteValue()).collect(Collectors.toCollection(TagByteArray::new));
                    }
                    return objectList.stream().map(TagType::toTag).collect(Collectors.toCollection(TagList::new));
                }
            }
            return null;
        }
    };

    public TagType elementType = null;

    //TODO 类型检查未完全实现: addAll、Constructor等变更元素的方法
    @Override
    public boolean add(TagBase tagBase) {
//        // 错误的实现方式
//        if (isEmpty()) {
//            elementType = tagBase.getTypeId();
//        } else if (elementType != tagBase.getTypeId()) {
//            return false;
//        }
        return super.add(tagBase);
    }

    public TagList(Collection<TagBase> collection) {
        super(collection);
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
    public List<?> getValue() {
        return this.stream().map(TagBase::getValue).collect(Collectors.toList());
    }

    @Override
    public TagType getTypeId() {
        return TagType.LIST;
    }

    @Override
    public Object[] toArray() {
        return this.stream().map(TagBase::getValue).toArray();
    }
}
