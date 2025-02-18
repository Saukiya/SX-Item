package github.saukiya.expression;

import github.saukiya.expression.operator.NumberOperator;
import github.saukiya.expression.operator.number.*;

public class ConditionExpressionParser {

    private static NumberOperator operator(NumberOperator a1, NumberOperator a2, char operator) {
        switch (operator) {
            case '+':
                return new AdditionOperator(a1, a2);
            case '-':
                return new SubtractionOperator(a1, a2);
            case '*':
                return new MultiplicationOperator(a1, a2);
            case '/':
                return new DivisionOperator(a1, a2);
            case '%':
                return new ModulusOperator(a1, a2);
            default:
                break;
        }
        throw new IllegalStateException("illegal operator: " + operator);
    }

    private static int getPriority(char operator) {
        switch (operator) {
            case '?':
                return 0;
            case '(':
                return 1;
            case '+':
            case '-':
                return 2;
            case '*':
            case '/':
            case '%':
                return 3;
            case '<':
            case '>':
            case '=':
            case '!':
                // 处理 >= <= == != > <
                return 4;
            case '&':
            case '|':
                return 5;
            default:
                break;
        }
        throw new IllegalStateException("illegal operator: " + operator);
    }
}
