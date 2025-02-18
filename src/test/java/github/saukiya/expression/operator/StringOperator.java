package github.saukiya.expression.operator;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StringOperator implements Operator {

    String value;

    public String evaluate() {
        return value;
    }

    public static StringOperator Convert(Operator operator) {
        // TODO
        return null;
    }
}
