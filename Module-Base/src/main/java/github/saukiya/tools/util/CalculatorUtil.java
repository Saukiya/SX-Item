package github.saukiya.tools.util;

import github.saukiya.tools.base.CharStack;
import github.saukiya.tools.base.DoubleStack;

/**
 * 计算器工具 - 处理数学表达式 (枫溪: 随地大小算)
 * <pre>{@code
 * CalculatorUtil.calculator("12.42+24.13-36.2*2.3/-(-4.1+6.5)%3");
 * CalculatorUtil.calculator("-3 + ((4 * (10 - (6 / 2))) - (8 % 3) + (5 + (-7) / 2))");
 * }</pre>
 */
public class CalculatorUtil {

    /**
     * 计算数学表达式
     *
     * @param expression
     * @return
     */
    public static double calculator(String expression) {
        /*数字栈*/
        DoubleStack number = new DoubleStack();
        /*符号栈*/
        CharStack operator = new CharStack();
        // 在栈顶压人一个?，配合它的优先级，目的是减少下面程序的判断
        operator.push('?');

        double num = 0;
        int numBits = 0;
        boolean canNegative = true;
        for (char c : expression.toCharArray()) {
            switch (c) {
                case '(':
                    canNegative = true;
                    operator.push('(');
                    if (numBits != 0) {
                        number.push(num);
                        num = numBits = 0;
                    }
                    break;
                case ')':
                    canNegative = false;
                    num = numBits != 0 ? num : number.pop();
                    while ((c = operator.pop()) != '(') {
                        num = operator(number.pop(), num, c);
                    }
                    number.push(num);
                    num = numBits = 0;
                    break;
                case '-':
                    if (canNegative) {
                        // 补位
                        number.push(0);
                        operator.push(c);
                        break;
                    }
                case '+':
                case '*':
                case '/':
                case '%':
                    canNegative = true;
                    num = numBits != 0 ? num : number.pop();
                    int priority = getPriority(c);
                    while (priority <= getPriority(operator.peek())) {
                        num = operator(number.pop(), num, operator.pop());
                    }
                    operator.push(c);
                    number.push(num);
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
                    numBits = -1;
                    break;
                default:
                    break;
            }
        }

        if (numBits == 0) {
            num = number.pop();
        }

        while (operator.peek() != '?') {//遍历结束后，符号栈数字栈依次弹栈计算，并将结果压入数字栈
            num = operator(number.pop(), num, operator.pop());
        }
        return num;
    }

    private static double operator(double a1, double a2, char operator) {
        switch (operator) {
            case '+':
                return a1 + a2;
            case '-':
                return a1 - a2;
            case '*':
                return a1 * a2;
            case '/':
                return a1 / a2;
            case '%':
                return a1 % a2;
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
