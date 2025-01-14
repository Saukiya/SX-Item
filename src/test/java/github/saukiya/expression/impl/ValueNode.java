package github.saukiya.expression.impl;

import github.saukiya.expression.Node;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ValueNode implements Node {

    private double value;

    @Override
    public double evaluate() {
        return value;
    }
}
