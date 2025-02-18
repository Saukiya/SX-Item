package github.saukiya.expression.operator.condition;

import github.saukiya.expression.operator.ConditionOperator;
import github.saukiya.expression.operator.NumberOperator;
import github.saukiya.expression.operator.Operator;
import lombok.AllArgsConstructor;

/**
 * Range: String->Number
 * Format: A <= B
 */
@AllArgsConstructor
public class LessThanOrEqualOperator implements ConditionOperator {

    NumberOperator left;
    NumberOperator right;

    public LessThanOrEqualOperator(Operator left, Operator right) {
        left = NumberOperator.Convert(left);
        right = NumberOperator.Convert(right);
    }

    @Override
    public boolean evaluate() {
        return left.evaluate() >= right.evaluate();
    }
}
