package github.saukiya.expression.operator.condition;

import github.saukiya.expression.operator.ConditionOperator;
import github.saukiya.expression.operator.NumberOperator;
import github.saukiya.expression.operator.Operator;
import github.saukiya.expression.operator.StringOperator;
import lombok.AllArgsConstructor;

/**
 * Range: String~Number
 * Format: A == B
 * Condition: A is Number && B is Number ? (Number==Number) : A is Boolean && B is Boolean ? (Boolean=Boolean) : (String==String)
 */
public abstract class EqualOperator implements ConditionOperator {

    public abstract boolean evaluate();

    public static EqualOperator Convert(Operator left, Operator right) {
        if (left instanceof NumberOperator) {
            if (right instanceof NumberOperator) {
                return new NumberEqualOperator((NumberOperator) left, (NumberOperator) right);
            }
            if (right instanceof ConditionOperator) {
                throw new IllegalStateException("number不能与boolean进行比较");
            }
        }
        if (left instanceof ConditionOperator) {
            if (right instanceof ConditionOperator) {
                return new BooleanEqualOperator((ConditionOperator) left, (ConditionOperator) right);
            }
            if (right instanceof NumberOperator) {
                throw new IllegalStateException("number不能与boolean进行比较");
            }
        }
        return new StringEqualOperator(StringOperator.Convert(left), StringOperator.Convert(right));
    }

    @AllArgsConstructor
    public static class StringEqualOperator extends EqualOperator {

        StringOperator left;
        StringOperator right;

        @Override
        public boolean evaluate() {
            return left.equals(right);
        }
    }

    @AllArgsConstructor
    public static class BooleanEqualOperator extends EqualOperator {

        ConditionOperator left;
        ConditionOperator right;

        @Override
        public boolean evaluate() {
            return left.evaluate() == right.evaluate();
        }
    }

    @AllArgsConstructor
    public static class NumberEqualOperator extends EqualOperator {

        NumberOperator left;
        NumberOperator right;

        @Override
        public boolean evaluate() {
            return left.evaluate() == right.evaluate();
        }
    }
}
