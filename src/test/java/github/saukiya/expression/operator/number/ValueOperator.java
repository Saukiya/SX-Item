package github.saukiya.expression.operator.number;

import github.saukiya.expression.operator.NumberOperator;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ValueOperator implements NumberOperator {

    private double value;

    @Override
    public double evaluate() {
        return value;
    }
}
