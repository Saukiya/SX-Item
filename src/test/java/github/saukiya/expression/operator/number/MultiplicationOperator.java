package github.saukiya.expression.operator.number;

import github.saukiya.expression.operator.NumberOperator;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MultiplicationOperator implements NumberOperator {

    private NumberOperator left;
    private NumberOperator right;

    @Override
    public double evaluate() {
        return left.evaluate() * right.evaluate();
    }
}