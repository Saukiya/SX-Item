package github.saukiya.expression.operator;

/**
 * 数字操作符
 * AdditionOperator: A + B
 * DivisionOperator: A / B
 * DynamicOperator: Variable
 * ModulusOperator: A % B
 * MultiplicationOperator: A * B
 * SubtractionOperator: A - B
 * ValueOperator: 123
 */
public interface NumberOperator extends Operator {

    double evaluate();

    static NumberOperator Convert(Operator operator) {
        // TOOD 来自其他的转换
        if (operator instanceof NumberOperator) {
            return (NumberOperator) operator;
        }
        return null;
    }
}