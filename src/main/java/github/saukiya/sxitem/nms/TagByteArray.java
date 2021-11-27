package github.saukiya.sxitem.nms;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TagByteArray extends TagListBase<TagByte> {

    TagByteArray(Byte[] bytes) {
        for (Byte b : bytes) {
            add(new TagByte(b));
        }
    }

    @Override
    public TagListBase<TagByte> getValue() {
        return this;
    }
}