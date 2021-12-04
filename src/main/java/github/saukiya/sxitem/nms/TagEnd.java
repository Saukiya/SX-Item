package github.saukiya.sxitem.nms;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.DataInput;
import java.io.DataOutput;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class TagEnd implements TagBase {

    @Getter
    private static final TagEnd inst = new TagEnd();
    protected static final TagType.Method typeMethod = new TagType.Method() {
        @Override
        public TagEnd readTagBase(DataInput dataInput, int depth) {
            return inst;
        }

        @Override
        public TagEnd toTag(Object object) {
            return null;
        }
    };

    @Override
    public void write(DataOutput dataOutput) {
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public TagType getTypeId() {
        return TagType.END;
    }

    @Override
    public String toString() {
        return "END";
    }
}
