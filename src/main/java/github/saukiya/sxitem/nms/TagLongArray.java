package github.saukiya.sxitem.nms;

import lombok.NoArgsConstructor;

import java.io.DataOutput;
import java.io.IOException;
import java.util.stream.IntStream;

@NoArgsConstructor
public class TagLongArray extends TagListBase<TagLong> {

    protected static final TagType.Method<TagLongArray> typeMethod = (dataInput, depth) -> {
        int length = dataInput.readInt();
        TagLongArray tagLongArray = new TagLongArray();
        for (int i = 0; i < length; ++i) {
            tagLongArray.add(new TagLong(dataInput.readLong()));
        }
        return tagLongArray;
    };

    public TagLongArray(long[] bytes) {
        for (long v : bytes) {
            add(new TagLong(v));
        }
    }

    @Override
    public String getToStringPrefix() {
        return "L;";
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(size());
        for (TagLong tagLong : this) {
            dataOutput.writeLong(tagLong.getValue());
        }
    }

    @Override
    public TagType getTypeId() {
        return TagType.LONG_ARRAY;
    }

    public long[] longArray() {
        long[] arrays = new long[size()];
        IntStream.range(0, size()).forEach(i -> arrays[i] = get(i).getValue());
        return arrays;
    }
}
