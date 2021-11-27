package github.saukiya.sxitem.nms;

import lombok.NoArgsConstructor;

import java.util.stream.IntStream;

@NoArgsConstructor
public class TagLongArray extends TagListBase<TagLong> {

    public TagLongArray(long[] bytes) {
        for (long v : bytes) {
            add(new TagLong(v));
        }
    }


    public long[] longArray() {
        long[] arrays = new long[size()];
        IntStream.range(0, size()).forEach(i -> arrays[i] = get(i).getValue());
        return arrays;
    }
}
