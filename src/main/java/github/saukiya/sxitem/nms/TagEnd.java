package github.saukiya.sxitem.nms;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class TagEnd implements TagBase {

    @Getter
    private static final TagEnd inst = new TagEnd();

    @Override
    public String getValue() {
        return "END";
    }
}
