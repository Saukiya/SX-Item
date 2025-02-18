package github.saukiya.expression;

import github.saukiya.expression.operator.NumberOperator;
import github.saukiya.expression.operator.number.*;
import github.saukiya.tools.base.CharStack;
import github.saukiya.tools.util.CalculatorUtil;
import github.saukiya.tools.util.TimingsUtil;

import java.util.Stack;

public class DoubleExpressionParser {

    public static void main(String[] args) {
        System.out.println("abc".compareTo("abc"));
        System.out.println("123".compareTo("234"));
        System.out.println("123".compareTo("123"));
        System.out.println("234".compareTo("123"));
        String expression;
        // 开始测速
        expression = "-3 + ((4 * (10 - (6 / 2))) - (8 % 5) + (5 + (-4) / 2))";
        System.out.println(CalculatorUtil.calculator(expression));
        ;
        expression = "-CC + ((4 * (10 - (6 / 2))) - (8 % 12B23) + (5 + (-T1V2C32424) / BB))";
        NumberOperator rootNode = parse(expression);
//        System.out.println(Test.gson.toJson(rootNode));
        System.out.println(rootNode.evaluate());
        System.out.println(-3 + ((4 * (10 - (6 / 2D))) - (8 % 5) + (5 + (-4) / 2D)));
    }

    public static void performance() {
        String expression;
        NumberOperator rootNode;
        // 热身
        expression = "-3 + ((4 * (10 - (6 / 2))) - (8 % 3) + (5 + (-7) / 2))";
        for (int i = 0; i < 50000; i++) {
            CalculatorUtil.calculator(expression);
        }

        expression = "-CC + ((4 * (10 - (6 / 2))) - (8 % CC) + (5 + (-7) / BB))";
        rootNode = parse(expression);
        for (int i = 0; i < 50000; i++) {
            rootNode.evaluate();
        }

        // 开始测速
        expression = "-3 + ((4 * (10 - (6 / 2))) - (8 % 3) + (5 + (-7) / 2))";
        TimingsUtil timingsUtil = new TimingsUtil();
        for (int i = 0; i < 200; i++) {
            CalculatorUtil.calculator(expression);
        }
        timingsUtil.dot("1-JX+ZX * 1000");
        timingsUtil.dot();

        expression = "-CC + ((4 * (10 - (6 / 2))) - (8 % CC) + (5 + (-7) / BB))";
        rootNode = parse(expression);
        for (int i = 0; i < 200; i++) {
            rootNode.evaluate();
        }
        timingsUtil.dot("2-JX+ZX * 1000");
        timingsUtil.print();
    }

    public static NumberOperator parse(String expression) {
        Stack<NumberOperator> nodes = new Stack<>();
        CharStack operator = new CharStack();
        // 在栈顶压人一个?，配合它的优先级，目的是减少下面程序的判断
        operator.push('?');

        NumberOperator currentNode;
        double num = 0;
        int numBits = 0;
        boolean canNegative = true;
        StringBuilder dynamic = null;
        for (char c : expression.toCharArray()) {
            if (dynamic != null) {
                switch (c) {
                    case '+':
                    case '-':
                    case '*':
                    case '/':
                    case '(':
                    case ')':
                    case ' ':
                        nodes.add(new DynamicOperator(dynamic.toString()));
                        dynamic = null;
                        canNegative = false;
                        break;
                    default:
                        dynamic.append(c);
                        continue;
                }
            }
            switch (c) {
                case '(':
                    canNegative = true;
                    operator.push('(');
                    if (numBits != 0) {
                        nodes.push(new ValueOperator(num));
                        num = numBits = 0;
                    }
                    break;
                case ')':
                    canNegative = false;
                    currentNode = numBits != 0 ? new ValueOperator(num) : nodes.pop();
                    while ((c = operator.pop()) != '(') {
                        currentNode = operator(nodes.pop(), currentNode, c);
                    }
                    nodes.push(currentNode);
                    num = numBits = 0;
                    break;
                case '-':
                    if (canNegative) {
                        // 补位
                        nodes.push(new ValueOperator(0));
                        operator.push(c);
                        break;
                    }
                case '%':
                case '+':
                case '*':
                case '/':
                    canNegative = true;
                    currentNode = numBits != 0 ? new ValueOperator(num) : nodes.pop();
                    int priority = getPriority(c);
                    while (priority <= getPriority(operator.peek())) {
                        currentNode = operator(nodes.pop(), currentNode, operator.pop());
                    }
                    operator.push(c);
                    nodes.push(currentNode);
                    num = numBits = 0;
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    canNegative = false;
                    if (numBits >= 0) {
                        num = (num * 10) + (c - 48);
                        numBits++;
                    } else {
                        num += (c - 48) * Math.pow(10, numBits--);
                    }
                    break;
                case '.':
                    if (numBits > 0) {
                        numBits = -1;
                    }
                    break;
                case ' ':
                    break;
                default:
                    dynamic = new StringBuilder();
                    if (numBits != 0) {
                        dynamic.append((int) num);
                        num = numBits = 0;
                    }
                    dynamic.append(c);
                    break;
            }
        }

        currentNode = numBits != 0 ? new ValueOperator(num) : nodes.pop();

        while (operator.peek() != '?') { //遍历结束后，符号栈数字栈依次弹栈计算，并将结果压入数字栈
            currentNode = operator(nodes.pop(), currentNode, operator.pop());
        }
        return currentNode;
    }

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
            default:
                break;
        }
        throw new IllegalStateException("illegal operator: " + operator);
    }

    /**
     * 获取动态值
     *
     * @param expression
     * @return
     */
    public static NumberOperator GetDynamicNode(String expression) {
        return new DynamicOperator(expression);
    }
}

