package github.saukiya.sxitem.nms;

import lombok.NoArgsConstructor;

import java.util.stream.IntStream;

@NoArgsConstructor
public class TagIntArray extends TagListBase<TagInt> {

    public TagIntArray(int[] bytes) {
        for (int v : bytes) {
            add(new TagInt(v));
        }
    }

    public int[] intArray() {
        int[] arrays = new int[size()];
        IntStream.range(0, size()).forEach(i -> arrays[i] = get(i).getValue());
        return arrays;
    }
}
