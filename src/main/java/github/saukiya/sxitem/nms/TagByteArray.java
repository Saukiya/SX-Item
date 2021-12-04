package github.saukiya.sxitem.nms;

import com.google.common.base.Preconditions;
import lombok.NoArgsConstructor;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.IntStream;

@NoArgsConstructor
public class TagByteArray extends TagListBase<TagByte> {

    protected static final TagType.Method<TagByteArray> typeMethod = (dataInput, depth) -> {
        int length = dataInput.readInt();
        Preconditions.checkArgument(length < 16777216);
        byte[] bytes = new byte[length];
        dataInput.readFully(bytes);
        return new TagByteArray(bytes);
    };

    public TagByteArray(Collection<TagByte> tagBases) {
        super(tagBases);
    }

    public TagByteArray(byte[] bytes) {
        for (byte v : bytes) {
            add(new TagByte(v));
        }
    }

    @Override
    public String getToStringPrefix() {
        return "B;";
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(size());
        dataOutput.write(byteArray());
    }

    @Override
    public TagType getTypeId() {
        return TagType.BYTE_ARRAY;
    }

    public byte[] byteArray() {
        byte[] arrays = new byte[size()];
        IntStream.range(0, size()).forEach(i -> arrays[i] = get(i).getValue());
        return arrays;
    }
}