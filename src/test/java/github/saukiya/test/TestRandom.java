package github.saukiya.test;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.RandomDocker;
import github.saukiya.sxitem.util.Config;
import github.saukiya.util.TestBenchmark;
import github.saukiya.util.helper.PlaceholderHelper;
import lombok.val;
import org.bukkit.configuration.file.YamlConfiguration;
import org.openjdk.jmh.annotations.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@State(Scope.Thread)
public class TestRandom {

    static final TestRandom inst = new TestRandom();

    static final Pattern LONG_PATTERN = Pattern.compile("\\d+");

    static Pattern CALCULATOR_PATTERN = Pattern.compile("(?<!\\d)-?\\d+(\\.\\d+)?|[+\\-*/%()]");

    final RandomDocker docker = new RandomDocker();

    public static void main(String[] args) throws Exception {
        TestBenchmark.run("calculator1", "calculator3");
//        TestBenchmark.run("time1", "time2");
//        TestBenchmark.run("double1", "double2");
//        TestBenchmark.run("int1", "int2");
//        TestBenchmark.run("lock1", "lock2");
//        TestBenchmark.run();
//        System.out.println("结果1： " + inst.calculator1(new CalculatorState()));
//        System.out.println("结果2： " + inst.calculator2(new CalculatorState()));
//        System.out.println("结果3： " + inst.calculator3(new CalculatorState()));
    }

    public void setup() throws Exception {
        setupTrial();
        setupInvocation();
    }

    @Setup(Level.Trial)
    public void setupTrial() throws Exception {
        YamlConfiguration config = new YamlConfiguration();
        config.set(Config.TIME_FORMAT, "yyyy/MM/dd HH:mm");
        val temp = Config.class.getDeclaredField("config");
        temp.setAccessible(true);
        temp.set(null, config);
    }

    @Setup(Level.Invocation)
    public void setupInvocation() {
        docker.getLockMap().clear();
    }

    @State(value = Scope.Thread)
    public static class CalculatorState {
        @Param({"(8 + 6 / 3) * 10 - 5 / 10", "12 + 24 - 36 * 2 / (4 + 6) % 3"})
        String key = "5 + (8 + 6 / 3) * 10 - 5 / 10";
    }

    @Benchmark
    public Number calculator1(CalculatorState state) throws Exception {
        String expr = state.key;
        boolean intTransform = expr.startsWith("int");
        if (intTransform) {
            expr = expr.substring(3);
        }
        /*数字栈*/
        Stack<Double> number = new Stack<>();
        /*符号栈*/
        Stack<String> operator = new Stack<>();
        operator.push(null);// 在栈顶压人一个null，配合它的优先级，目的是减少下面程序的判断

        /* 将expr打散为运算数和运算符 */
        Matcher m = CALCULATOR_PATTERN.matcher(expr);
        while (m.find()) {
            String temp = m.group();
            if (temp.matches("[+\\-*/%()]")) {//遇到符号
                if (temp.equals("(")) {//遇到左括号，直接入符号栈
                    operator.push(temp);
                } else if (temp.equals(")")) {//遇到右括号，"符号栈弹栈取栈顶符号b，数字栈弹栈取栈顶数字a1，数字栈弹栈取栈顶数字a2，计算a2 b a1 ,将结果压入数字栈"，重复引号步骤至取栈顶为左括号，将左括号弹出
                    String b;
                    while (!(b = operator.pop()).equals("(")) {
                        double a1 = number.pop();
                        double a2 = number.pop();
                        number.push(doubleCal(a2, a1, b.charAt(0)));
                    }
                } else {//遇到运算符，满足该运算符的优先级大于栈顶元素的优先级压栈；否则计算后压栈
                    while (getPriority(temp) <= getPriority(operator.peek())) {
                        double a1 = number.pop();
                        double a2 = number.pop();
                        String b = operator.pop();
                        number.push(doubleCal(a2, a1, b.charAt(0)));
                    }
                    operator.push(temp);
                }
            } else {//遇到数字，直接压入数字栈
                number.push(Double.valueOf(temp));
            }
        }

        while (operator.peek() != null) {//遍历结束后，符号栈数字栈依次弹栈计算，并将结果压入数字栈
            double a1 = number.pop();
            double a2 = number.pop();
            String b = operator.pop();
            number.push(doubleCal(a2, a1, b.charAt(0)));
        }
        return intTransform ? Math.round(number.pop()) : number.pop();
    }

    // 负数支持不行 '-1234 * 5'
    @Benchmark
    public Number calculator2(CalculatorState state) throws Exception {
        String expr = state.key;
        boolean intTransform = expr.startsWith("int");
        if (intTransform) {
            expr = expr.substring(3);
        }
        /*数字栈*/
        Stack<Double> number = new Stack<>();
        /*符号栈*/
        Stack<Character> operator = new Stack<>();
        operator.push('?');// 在栈顶压人一个null，配合它的优先级，目的是减少下面程序的判断

        /* 将expr打散为运算数和运算符 */
        double temp = 0;
        int isDecimal = 0;
        for (int i = 0, length = expr.length(); i < length; i++) {
            char c = expr.charAt(i);
            switch (c) {
                case '(':
                    operator.push('(');
                    number.push(temp);
                    temp = 0;
                    isDecimal = 0;
                    break;
                case ')':
                    // 计算处理
                    while ((c = operator.pop()) != '(') {
                        temp = doubleCal(number.pop(), temp, c);
                    }
                    number.push(temp);
                    temp = 0;
                    isDecimal = 0;
                    break;
                case '+':
                case '-':
                case '*':
                case '/':
                case '%':
                    int priority = getPriority(c);
                    while (priority <= getPriority(operator.peek())) {
                        temp = doubleCal(number.pop(), temp, operator.pop());
                    }
                    number.push(temp);
                    operator.push(c);
                    temp = 0;
                    isDecimal = 0;
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
                    if (isDecimal <= 0) {
                        temp = temp * 10 + c - 48;
                        isDecimal++;
                        break;
                    }
                    temp += (c - 48) * Math.pow(10, isDecimal--);
                    break;
                case '.':
                    isDecimal = -1;
                    break;
                default:
                    continue;
            }
        }

        while (operator.peek() != '?') {//遍历结束后，符号栈数字栈依次弹栈计算，并将结果压入数字栈
            temp = doubleCal(number.pop(), temp, operator.pop());
        }
        return intTransform ? Math.round(temp) : temp;
    }

    @Benchmark
    public Number calculator3(CalculatorState state) throws Exception {
        String expr = state.key;
        boolean intTransform = expr.startsWith("int");
        if (intTransform) {
            expr = expr.substring(3);
        }
        /*数字栈*/
        Stack<Double> number = new Stack<>();
        /*符号栈*/
        Stack<Character> operator = new Stack<>();
        operator.push('?');// 在栈顶压人一个null，配合它的优先级，目的是减少下面程序的判断

        /* 将expr打散为运算数和运算符 */
        Matcher m = CALCULATOR_PATTERN.matcher(expr);
        while (m.find()) {
            String temp = m.group();
            switch (temp) {
                case "(":
                    operator.push('(');
                    break;
                case ")":
                    char b;
                    while ((b = operator.pop()) != '(') {
                        double a1 = number.pop();
                        double a2 = number.pop();
                        number.push(doubleCal(a2, a1, b));
                    }
                    break;
                case "+":
                case "-":
                case "*":
                case "/":
                case "%":
                    int priority = getPriority(temp);
                    while (priority <= getPriority(operator.peek())) {
                        double a1 = number.pop();
                        double a2 = number.pop();
                        number.push(doubleCal(a2, a1, operator.pop()));
                    }
                    operator.push(temp.charAt(0));
                    break;
                default:
                    number.push(Double.valueOf(temp));
                    break;
            }
        }

        while (operator.peek() != '?') {//遍历结束后，符号栈数字栈依次弹栈计算，并将结果压入数字栈
            double a1 = number.pop();
            double a2 = number.pop();
            number.push(doubleCal(a2, a1, operator.pop()));
        }
        return intTransform ? Math.round(number.pop()) : number.pop();
    }

    private double doubleCal(double a1, double a2, char operator) throws Exception {
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
        throw new Exception("illegal operator!");
    }

    private int getPriority(String s) throws Exception {
        if (s == null) {
            return 0;
        }
        switch (s) {
            case "(":
                return 1;
            case "+":
            case "-":
                return 2;
            case "*":
            case "%":
            case "/":
                return 3;
            default:
                break;
        }
        throw new Exception("illegal operator!");
    }

    private int getPriority(char c) throws Exception {
        switch (c) {
            case '?':
                return 0;
            case '(':
                return 1;
            case '+':
            case '-':
                return 2;
            case '*':
            case '%':
            case '/':
                return 3;
            default:
                break;
        }
        throw new Exception("illegal operator!");
    }

    @State(Scope.Thread)
    public static class TimeState {
        @Param({"20Y12M31D23h59m59s", "12345"})
        String key = "20Y12M31D23h59m59s";
    }

    @Benchmark
    public String time1(TimeState state) {
        val key = state.key;
        if (LONG_PATTERN.matcher(key).matches()) {
            return SXItem.getSdf().get().format(System.currentTimeMillis() + Long.parseLong(key) * 1000);
        } else {
            Calendar calendar = Calendar.getInstance();
            int num = 0;
            for (int i = 0, length = key.length(); i < length; i++) {
                char c = key.charAt(i);
                switch (c) {
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
                        num = num * 10 + c - '0';
                        break;
                    case 'Y':
                    case 'y':
                        calendar.add(Calendar.YEAR, num);
                        num = 0;
                        break;
                    case 'M':
                        calendar.add(Calendar.MONTH, num);
                        num = 0;
                        break;
                    case 'D':
                    case 'd':
                        calendar.add(Calendar.DATE, num);
                        num = 0;
                        break;
                    case 'H':
                    case 'h':
                        calendar.add(Calendar.HOUR_OF_DAY, num);
                        num = 0;
                        break;
                    case 'm':
                        calendar.add(Calendar.MINUTE, num);
                        num = 0;
                        break;
                    case 'S':
                    case 's':
                        calendar.add(Calendar.SECOND, num);
                        num = 0;
                        break;
                }
            }
            return SXItem.getSdf().get().format(calendar.getTimeInMillis());
        }
    }

    @Benchmark
    public String time2(TimeState state) {
        val key = state.key;
        if (LONG_PATTERN.matcher(key).matches()) {
            return SXItem.getSdf().get().format(System.currentTimeMillis() + Long.parseLong(key) * 1000);
        } else {
            Calendar calendar = Calendar.getInstance();
            int num = 0;
            for (int i = 0, length = key.length(); i < length; i++) {
                char c = key.charAt(i);
                if (Character.isDigit(c)) {
                    num = num * 10 + (c - '0');
                } else {
                    updateCalendar(calendar, num, c);
                    num = 0;
                }
            }
            return SXItem.getSdf().get().format(calendar.getTimeInMillis());
        }
    }

    private void updateCalendar(Calendar calendar, int num, char unit) {
        switch (unit) {
            case 'Y':
            case 'y':
                calendar.add(Calendar.YEAR, num);
                break;
            case 'M':
                calendar.add(Calendar.MONTH, num);
                break;
            case 'D':
            case 'd':
                calendar.add(Calendar.DATE, num);
                break;
            case 'H':
            case 'h':
                calendar.add(Calendar.HOUR_OF_DAY, num);
                break;
            case 'm':
                calendar.add(Calendar.MINUTE, num);
                break;
            case 'S':
            case 's':
                calendar.add(Calendar.SECOND, num);
                break;
        }
    }

    @State(Scope.Thread)
    public static class DoubleState {
        @Param({"1.5_2.5", "200_100"})
        String key = "1.5_2.5";
    }

    @Benchmark
    public String double1(DoubleState state) {
        val key = state.key;
        String[] strSplit = key.split("_");
        if (strSplit.length == 1) return key;
        double[] doubles = {Double.parseDouble(strSplit[0]), Double.parseDouble(strSplit[1])};
        return SXItem.getDf().format(SXItem.getRandom().nextDouble() * (doubles[1] - doubles[0]) + doubles[0]);
    }

    @Benchmark
    public String double2(DoubleState state) {
        val key = state.key;
        int index = key.indexOf('_');
        if (index == -1) return key;
        double min = Double.parseDouble(key.substring(0, index));
        double max = Double.parseDouble(key.substring(index + 1));
        return SXItem.getDf().format(SXItem.getRandom().nextDouble() * (max - min) + min);
    }

    @State(Scope.Thread)
    public static class IntState {
        @Param({"1_2", "200_100"})
        String key = "1_2";
    }

    @Benchmark
    public String int1(IntState state) {
        val key = state.key;
        int[] ints = Arrays.stream(key.split("_")).mapToInt(Integer::parseInt).sorted().toArray();
        return String.valueOf(SXItem.getRandom().nextInt(1 + ints[1] - ints[0]) + ints[0]);
    }

    @Benchmark
    public String int2(IntState state) {
        val key = state.key;
        val index = key.indexOf('_');
        int min = Integer.parseInt(key.substring(0, index));
        int max = Integer.parseInt(key.substring(index + 1));
        return String.valueOf(SXItem.getRandom().nextInt(1 + Math.abs(max - min)) + Math.min(max, min));
    }

    @State(Scope.Thread)
    public static class LockState {
        @Param({"KEY#100,200,300"})
        String key = "KEY#100,200,300";
    }

    @Benchmark
    public String lock1(LockState state) {
        String key = state.key;
        String value;
        if (key.contains("#")) {
            String[] temp = key.substring(key.indexOf("#") + 1).split(",");
            String finalKey = key = key.substring(0, key.indexOf("#"));
            String tempValue = docker.getOtherList().stream().map(map -> map.get(finalKey)).filter(Objects::nonNull).findFirst().orElse(null);
            value = PlaceholderHelper.setPlaceholders(docker.getPlayer(), docker.replace(tempValue != null ? tempValue : temp[SXItem.getRandom().nextInt(temp.length)]));
        } else {
            value = PlaceholderHelper.setPlaceholders(docker.getPlayer(), docker.replace(docker.random(key)));
        }
        return docker.getLockMap() == null ? value : docker.getLockMap().computeIfAbsent(key, k -> value);
    }

    @Benchmark
    public String lock2(LockState state) {
        String key = state.key;
        String value = null;
        String temp = null;
        int indexOf = key.indexOf('#');

        if (indexOf >= 0) {
            temp = key.substring(indexOf + 1);
            key = key.substring(0, indexOf);
        }

        if (docker.getLockMap() != null && (value = docker.getLockMap().get(key)) != null) {
            return value;
        }

        if (temp != null) {
            for (Map<String, String> map : docker.getOtherList()) {
                value = map.get(key);
                if (value != null) break;
            }
            if (value == null) {
                value = randomArray(temp.split(","));
            }
        } else {
            value = docker.replace(docker.random(key));
        }

        if (docker.getLockMap() != null) {
            docker.getLockMap().put(key, value);
        }
        return value;
    }

    public String randomArray(String[] array) {
        return array[SXItem.getRandom().nextInt(array.length)];
    }
}
