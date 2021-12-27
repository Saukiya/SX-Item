package github.saukiya.sxitem.data.random.nodes;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.INode;
import github.saukiya.sxitem.util.Tuple;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public class MultipleNode implements INode {

    final List<Tuple<Double, String>> list = new ArrayList<>();
    private double maximum;

    @Override
    public String get() {
        double value = SXItem.getRandom().nextDouble() * maximum;
        int last = list.size() - 1;
        for (int i = 0; i < last; i++) {
            Tuple<Double, String> tuple = list.get(i);
            if (value < tuple.a()) return tuple.b();
        }
        return list.get(last).b();
    }

    public void add(Double weight, String value) {
        list.add(new Tuple(maximum += weight, value));
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }
}
