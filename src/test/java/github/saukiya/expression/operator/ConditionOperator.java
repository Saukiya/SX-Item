package github.saukiya.expression.operator;

/**
 * 条件操作符
 * LessThanOrEqualOperator: A <= B
 * EqualOperator: A == B
 * GreaterThanOrEqualOperator: A >= B
 * NotEqualOperator: A != B
 * LessThanOperator: A < B
 * GreaterThanOperator: A > B
 * LogicalAndOperator: A && B
 * LogicalOrOperator: A || B
 * LogicalNotOperator: !A
 */
public interface ConditionOperator extends Operator {
    boolean evaluate();
}
