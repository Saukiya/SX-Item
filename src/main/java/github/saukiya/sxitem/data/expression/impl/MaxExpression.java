package github.saukiya.sxitem.data.expression.impl;

import github.saukiya.sxitem.data.expression.ExpressionSpace;
import github.saukiya.sxitem.data.expression.IExpression;

public class MaxExpression implements IExpression {

    @Override
    public String replace(String key, ExpressionSpace space) {
        double max = Double.MIN_VALUE;
        String result = "";

        for (String part : key.split(":")) {
            double num = Double.parseDouble(part);
            if (num > max) {
                max = num;
                result = part;
            }
        }
        return result;
    }
}