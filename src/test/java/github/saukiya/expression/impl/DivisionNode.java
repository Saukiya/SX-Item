package github.saukiya.expression.impl;

import github.saukiya.expression.Node;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DivisionNode implements Node {

    private Node left;
    private Node right;

    @Override
    public double evaluate() {
        double rightValue = right.evaluate();
        if (rightValue == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return left.evaluate() / rightValue;
    }
}