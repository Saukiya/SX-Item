package github.saukiya.sxitem.nms;

import lombok.NoArgsConstructor;

import java.util.stream.IntStream;

@NoArgsConstructor
public class TagByteArray extends TagListBase<TagByte> {

    public TagByteArray(byte[] bytes) {
        for (byte v : bytes) {
            add(new TagByte(v));
        }
    }

    public byte[] byteArray() {
        byte[] arrays = new byte[size()];
        IntStream.range(0, size()).forEach(i -> arrays[i] = get(i).getValue());
        return arrays;
    }
}