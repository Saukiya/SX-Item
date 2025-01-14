package github.saukiya.expression.impl;

import github.saukiya.expression.Node;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MultiplicationNode implements Node {

    private Node left;
    private Node right;

    @Override
    public double evaluate() {
        return left.evaluate() * right.evaluate();
    }
}