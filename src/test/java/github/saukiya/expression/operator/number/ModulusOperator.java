package github.saukiya.expression.operator.number;

import github.saukiya.expression.operator.NumberOperator;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ModulusOperator implements NumberOperator {

    private NumberOperator left;
    private NumberOperator right;

    @Override
    public double evaluate() {
        double rightValue = right.evaluate();
        if (rightValue == 0) {
            throw new ArithmeticException("Modulus by zero");
        }
        return left.evaluate() % rightValue;
    }
}