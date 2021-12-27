package github.saukiya.sxitem.data.random.nodes;

import github.saukiya.sxitem.data.random.INode;
import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class SingletonNode implements INode {

    final String value;

    @Override
    public String get() {
        return value;
    }
}
