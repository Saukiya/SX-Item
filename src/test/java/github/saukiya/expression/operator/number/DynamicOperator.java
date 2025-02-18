package github.saukiya.expression.operator.number;

import github.saukiya.expression.operator.NumberOperator;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DynamicOperator implements NumberOperator {

    private String variable;

    @Override
    public double evaluate() {
        switch (variable) {
            case "AA":
                return 1;
            case "BB":
                return 2;
            case "CC":
                return 3;
            case "T1V2C32424":
                return 4;
            case "12B23":
                return 5;
        }
        System.out.println("????");
        return 0;
    }
}
