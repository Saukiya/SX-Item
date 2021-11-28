package github.saukiya.sxitem.nms;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class TagEnd implements TagBase {

    @Getter
    private static final TagEnd inst = new TagEnd();

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public String toString() {
        return "END";
    }
}
