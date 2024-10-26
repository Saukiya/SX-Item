package github.saukiya.tools.nbt;

import com.google.common.base.Preconditions;
import lombok.NoArgsConstructor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.IntStream;

@NoArgsConstructor
public class TagByteArray extends TagListBase<Byte, byte[]> {

    protected static final TagType.Method typeMethod = new TagType.Method() {
        @Override
        public TagByteArray readTagBase(DataInput dataInput, int depth) throws IOException {
            int length = dataInput.readInt();
            Preconditions.checkArgument(length < 16777216);
            byte[] bytes = new byte[length];
            dataInput.readFully(bytes);
            return new TagByteArray(bytes);
        }

        @Override
        public TagByteArray toTag(Object object) {
            return object instanceof byte[] ? new TagByteArray((byte[]) object) : null;
        }
    };

    public TagByteArray(Collection<Byte> collection) {
        super(collection);
    }

    public TagByteArray(byte[] bytes) {
        for (byte value : bytes) {
            add(value);
        }
    }

    @Override
    public String getToStringPrefix() {
        return "B;";
    }

    @Override
    public String getToStringSuffix() {
        return "B";
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(size());
        dataOutput.write(getValue());
    }

    @Override
    public byte[] getValue() {
        byte[] arrays = new byte[size()];
        IntStream.range(0, size()).forEach(i -> arrays[i] = get(i));
        return arrays;
    }

    @Override
    public TagType getTypeId() {
        return TagType.BYTE_ARRAY;
    }
}