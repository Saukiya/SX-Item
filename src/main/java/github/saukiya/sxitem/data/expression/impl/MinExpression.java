package github.saukiya.sxitem.data.expression.impl;

import github.saukiya.sxitem.data.expression.ExpressionSpace;
import github.saukiya.sxitem.data.expression.IExpression;

public class MinExpression implements IExpression {

    @Override
    public String replace(String key, ExpressionSpace space) {
        double min = Double.MAX_VALUE;
        String result = "";

        for (String part : key.split(":")) {
            double num = Double.parseDouble(part);
            if (num < min) {
                min = num;
                result = part;
            }
        }
        return result;
    }
}