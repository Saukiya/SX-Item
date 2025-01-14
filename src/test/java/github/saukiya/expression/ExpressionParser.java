package github.saukiya.expression;

import com.google.gson.Gson;
import github.saukiya.expression.impl.*;
import github.saukiya.tools.base.CharStack;
import github.saukiya.tools.util.CalculatorUtil;

import java.util.Stack;

public class ExpressionParser {

    public static void main(String[] args) {
        String expression = "-3 + ((4 * (10 - (6 / 2))) - (8 % 3) + (5 + (-7) / 2))";
        Node rootNode = parse(expression);
        System.out.println(new Gson().toJson(rootNode));
        System.out.println("Result-1: " + rootNode.evaluate());
        System.out.println("Result-2: " + CalculatorUtil.calculator(expression));
    }

    public static Node parse(String expression) {
        Stack<Node> nodes = new Stack<>();
        CharStack operator = new CharStack();
        // 在栈顶压人一个?，配合它的优先级，目的是减少下面程序的判断
        operator.push('?');

        Node currentNode;
        double num = 0;
        int numBits = 0;
        boolean canNegative = true;
        for (char c : expression.toCharArray()) {
            switch (c) {
                case '(':
                    canNegative = true;
                    operator.push('(');
                    if (numBits != 0) {
                        nodes.push(new ValueNode(num));
                        num = numBits = 0;
                    }
                    break;
                case ')':
                    canNegative = false;
                    currentNode = numBits != 0 ? new ValueNode(num) : nodes.pop();
                    while ((c = operator.pop()) != '(') {
                        currentNode = operator(nodes.pop(), currentNode, c);
                    }
                    nodes.push(currentNode);
                    num = numBits = 0;
                    break;
                case '-':
                    if (canNegative) {
                        // 补位
                        nodes.push(new ValueNode(0));
                        operator.push(c);
                        break;
                    }
                case '+':
                case '*':
                case '/':
                case '%':
                    canNegative = true;
                    currentNode = numBits != 0 ? new ValueNode(num) : nodes.pop();
                    int priority = getPriority(c);
                    while (priority <= getPriority(operator.peek())) {
                        currentNode = operator(nodes.pop(), currentNode, operator.pop());
                    }
                    operator.push(c);
                    nodes.push(currentNode);
                    num = numBits = 0;
                    break;
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case '0':
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
                default:
                    break;
            }
        }

        currentNode = numBits != 0 ? new ValueNode(num) : nodes.pop();

        while (operator.peek() != '?') { //遍历结束后，符号栈数字栈依次弹栈计算，并将结果压入数字栈
            currentNode = operator(nodes.pop(), currentNode, operator.pop());
        }
        return currentNode;
    }

    /**
     * 获取动态值
     *
     * @param expression
     * @return
     */
    public static Node GetDynamicNode(String expression) {
        return new DynamicNode(expression);
    }

    private static Node operator(Node a1, Node a2, char operator) {
        switch (operator) {
            case '+':
                return new AdditionNode(a1, a2);
            case '-':
                return new SubtractionNode(a1, a2);
            case '*':
                return new MultiplicationNode(a1, a2);
            case '/':
                return new DivisionNode(a1, a2);
            case '%':
                return new ModulusNode(a1, a2);
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
}

