package github.saukiya.expression.impl;

import github.saukiya.expression.Node;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DynamicNode implements Node {

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
        }

        return 0;
    }
}
