package github.saukiya.sxitem.nms;

import com.google.common.base.Preconditions;
import lombok.NoArgsConstructor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.stream.IntStream;

@NoArgsConstructor
public class TagIntArray extends TagListBase<TagInt> {

    protected static final TagType.Method<TagIntArray> typeMethod = new TagType.Method<TagIntArray>() {
        @Override
        public TagIntArray readTagBase(DataInput dataInput, int depth) throws IOException {
            int length = dataInput.readInt();
            Preconditions.checkArgument(length < 16777216);
            TagIntArray tagIntArray = new TagIntArray();
            for (int i = 0; i < length; i++) {
                tagIntArray.add(new TagInt(dataInput.readInt()));
            }
            return tagIntArray;
        }
    };

    public TagIntArray(int[] bytes) {
        for (int v : bytes) {
            add(new TagInt(v));
        }
    }

    @Override
    public String getToStringPrefix() {
        return "I;";
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(size());
        for (TagInt tagInt : this) {
            dataOutput.writeInt(tagInt.getValue());
        }
    }

    @Override
    public TagType getTypeId() {
        return TagType.INT_ARRAY;
    }

    public int[] intArray() {
        int[] arrays = new int[size()];
        IntStream.range(0, size()).forEach(i -> arrays[i] = get(i).getValue());
        return arrays;
    }
}
