package github.saukiya.util.nbt;

import com.google.common.base.Preconditions;
import lombok.NoArgsConstructor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.IntStream;

@NoArgsConstructor
public class TagIntArray extends TagListBase<Integer, int[]> {

    protected static final TagType.Method typeMethod = new TagType.Method() {
        @Override
        public TagIntArray readTagBase(DataInput dataInput, int depth) throws IOException {
            int length = dataInput.readInt();
            Preconditions.checkArgument(length < 16777216);
            TagIntArray tagIntArray = new TagIntArray();
            for (int i = 0; i < length; i++) {
                tagIntArray.add(dataInput.readInt());
            }
            return tagIntArray;
        }

        @Override
        public TagIntArray toTag(Object object) {
            return object instanceof int[] ? new TagIntArray((int[]) object) : null;
        }
    };

    public TagIntArray(Collection<Integer> collection) {
        super(collection);
    }

    public TagIntArray(int[] bytes) {
        Arrays.stream(bytes).forEach(this::add);
    }

    @Override
    public String getToStringPrefix() {
        return "I;";
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(size());
        for (Integer value : this) {
            dataOutput.writeInt(value);
        }
    }

    @Override
    public int[] getValue() {
        int[] arrays = new int[size()];
        IntStream.range(0, size()).forEach(i -> arrays[i] = get(i));
        return arrays;
    }

    @Override
    public TagType getTypeId() {
        return TagType.INT_ARRAY;
    }
}
