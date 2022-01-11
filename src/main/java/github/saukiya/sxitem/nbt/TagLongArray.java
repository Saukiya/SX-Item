package github.saukiya.sxitem.nbt;

import github.saukiya.sxitem.util.NMS;
import lombok.NoArgsConstructor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@NoArgsConstructor
public class TagLongArray extends TagListBase<Long, long[]> {

    protected static final TagType.Method typeMethod = new TagType.Method() {
        @Override
        public TagLongArray readTagBase(DataInput dataInput, int depth) throws IOException {
            int length = dataInput.readInt();
            TagLongArray tagLongArray = new TagLongArray();
            for (int i = 0; i < length; ++i) {
                tagLongArray.add(dataInput.readLong());
            }
            return tagLongArray;
        }

        @Override
        public TagListBase toTag(Object object) {
            if (object instanceof long[]) {
                long[] longs = (long[]) object;
                if (NMS.compareTo(1, 12, 1) >= 0) return new TagLongArray(longs);
                return Arrays.stream(longs).mapToObj(TagLong::new).collect(Collectors.toCollection(TagList::new));
            }
            return null;
        }
    };

    public TagLongArray(Collection<Long> collection) {
        super(collection);
    }

    public TagLongArray(long[] bytes) {
        Arrays.stream(bytes).forEach(this::add);
    }

    @Override
    public String getToStringPrefix() {
        return "L;";
    }

    @Override
    public String getToStringSuffix() {
        return "L";
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(size());
        for (Long value : this) {
            dataOutput.writeLong(value);
        }
    }

    @Override
    public long[] getValue() {
        long[] arrays = new long[size()];
        IntStream.range(0, size()).forEach(i -> arrays[i] = get(i));
        return arrays;
    }

    @Override
    public TagType getTypeId() {
        return TagType.LONG_ARRAY;
    }
}
