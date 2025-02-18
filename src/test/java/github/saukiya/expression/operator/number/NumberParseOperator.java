package github.saukiya.expression.operator.number;

import github.saukiya.expression.operator.NumberOperator;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NumberParseOperator implements NumberOperator {

    String value;

    @Override
    public double evaluate() {
        return Integer.parseInt(value);
    }
}
