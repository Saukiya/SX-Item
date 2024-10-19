package github.saukiya.test;

import github.saukiya.sxitem.SXItem;
import github.saukiya.sxitem.data.random.RandomDocker;
import github.saukiya.sxitem.util.Config;
import github.saukiya.util.base.CharStack;
import github.saukiya.util.base.DoubleStack;
import github.saukiya.util.helper.PlaceholderHelper;
import lombok.val;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.YamlConfiguration;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("UnusedReturnValue")
@State(Scope.Thread)
public class TestRandom {

    private static final Pattern SCRIPT_PATTERN = Pattern.compile("(\\w+)\\.(\\w+)#(.+)");

    static final Pattern LONG_PATTERN = Pattern.compile("\\d+");

    static Pattern CALCULATOR_PATTERN = Pattern.compile("(?<!\\d)-?\\d+(\\.\\d+)?|[+\\-*/%()]");

    static List<String> copyList = Arrays.asList("|||||||||||||", "|||||||||||||||", "||||||||||\n|||||||||||", "||||||||||||||||", "|||||||||||||||||||||", "||||||||||\n|||||||||||", "|||||||||||||||||||||", "||||||||\n|||||||\n||||||", "|||||||||||||||||||||");

    static {
        try {
            YamlConfiguration config = new YamlConfiguration();
            config.set(Config.TIME_FORMAT, "yyyy/MM/dd HH:mm");
            Field temp = Config.class.getDeclaredField("config");
            temp.setAccessible(true);
            temp.set(null, config);
        } catch (Exception e) {
        }
    }

    public static void main(String[] args) throws Exception {
//        TestBenchmark.run(ScriptBM.class);
//        TestBenchmark.run(BooleanBM.class);
//        TestBenchmark.run(CalculatorBM.class);
//        TestBenchmark.run(TimeBM.class);
//        TestBenchmark.run(DoubleBM.class);
//        TestBenchmark.run(IntBM.class);
//        TestBenchmark.run(LockBM.class);
//        TestBenchmark.run("BooleanBM.test3", "CalculatorBM.test3", "TimeBM.test3", "DoubleBM.test3", "IntegerBM.test3", "LockBM.test2");
//        TestBenchmark.run("replace1", "replace2");
//        TestBenchmark.run();
//        calculatorValidation();
//        System.out.println(boolean1("AAA:BBB:CCC:DDD:EEE:AAA") != null);
//        System.out.println(boolean1("AAA:BBB:CCC:DDD:EEE:AAAA") != null);
//        System.out.println(boolean1("AAA:AAA:BB:CC:DD:EE:FF:GG:HH:YY:GG:KK:AA") != null);
//        System.out.println(boolean3("AAA:BBB:CCC:DDD:EEE:AAA") != null);
//        System.out.println(boolean3("AAA:BBB:CCC:DDD:EEE:AAAA") != null);
//        System.out.println(boolean3("AA#AAA:BB:CC:DD:EE:FF:GG:HH:YY:GG:KK:AA") != null);
    }

    @Benchmark
    public static Object replace1() {
        return copyList.stream()
                .flatMap(str -> str.indexOf('\n') != -1 ? Arrays.stream(str.split("\n")) : Stream.of(str))
                .filter(s -> !s.contains("%DeleteLore"))
                .collect(Collectors.toList());
    }

    @Benchmark
    public static Object replace2() {
        val list = new ArrayList<>();
        for (String str : copyList) {
            if (str.indexOf('\n') != -1) {
                String[] split = str.split("\n");
                for (String s : split) {
                    if (!s.contains("%DeleteLore")) {
                        list.add(s);
                    }
                }
            } else if (!str.contains("%DeleteLore")) {
                list.add(str);
            }
        }
        return list;
    }

    public static String scriptRegex1(String key) {
        Matcher matcher = SCRIPT_PATTERN.matcher(key);
        if (matcher.matches()) {
            String fileName = matcher.group(1);
            String functionName = matcher.group(2);
            Object[] args = matcher.group(3).split(",");
        }
        return null;
    }

    public static String scriptRegex2(String key) {
        int index1 = key.indexOf('.');
        Validate.isTrue(index1 != -1, key);
        String fileName = key.substring(0, index1++);

        int index2 = key.indexOf('#', index1);
        index2 = index2 != -1 ? index2 : key.length();
        String functionName = key.substring(index1, index2++);

        Object[] args;
        if (index2 > key.length()) {
            args = null;
        } else {
            args = key.substring(index2).split(",");
        }
        return null;
    }

    public static String boolean1(String key) {
        String comStr = null;

        for (String str : key.split(":")) {
            if (comStr == null) comStr = str;
            else if (str.equals(comStr)) return "";
        }
        return null;
    }

    public static String boolean3(String key) {
        char[] chars = key.toCharArray();
        int index = 0, check = 0, length = chars.length;

        for (int i = 0; i < length; i++) {
            switch (chars[i]) {
                case '#':
                case ':':
                    check = i;
                    break;
                default:
                    continue;
            }
            break;
        }
        if (check == 0) return null;
        for (int i = check + 1; i < length; i++) {
            char c = chars[i];
            switch (c) {
                case ':':
                    if (index == check) {
                        return "";
                    }
                    index = 0;
                    break;
                default:
                    if (index != -1 && chars[index++] != c) {
                        index = -1;
                    }
                    break;
            }
        }
        return index == check ? "" : null;
    }

    public static void calculatorValidation() {
        val validation = new LinkedHashMap<String, Object>();
        validation.put("(8 + 6 / 3) * 10 - 5 / 10", (8d + 6d / 3d) * 10d - 5d / 10d);
        validation.put("12 + 24 - 36 * 2 / (4 + 6) % 3", 12d + 24d - 36d * 2d / (4d + 6d) % 3d);
        validation.put("14/3*2", 14d / 3d * 2d);
        validation.put("1+2-5*3", 1 + 2 - 5 * 3);
        validation.put("(1+(4+5+2)-3)+(6+8)", (1 + (4 + 5 + 2) - 3) + (6 + 8));
        validation.put("-3 + ((4 * (10 - (6 / 2))) - (8 % 3) + (5 + (-7) / 2))", -3d + ((4d * (10d - (6d / 2d))) - (8d % 3d) + (5d + (-7d) / 2d)));
        validation.put("-2 + 1", -2 + 1);
        validation.put("- (3 + (4 + 5))", -(3 + (4 + 5)));
        validation.put("- (3 - (- (4 + 5) ) )", -(3 - (-(4 + 5))));
        validation.put("3.5 + (-12.7) * 4.2", 3.5 + (-12.7) * 4.2);
        validation.put("-8.2 / 2.4 + 5 * 3", -8.2 / 2.4 + 5 * 3);
        validation.put("(7.8 % 2) - 15 / 2", (7.8 % 2d) - 15 / 2d);
        validation.put("19.5 * (-3) / 7 + 1", 19.5 * (-3) / 7d + 1);
        validation.put("12.5 - 4.6 * (2 % 5)", 12.5 - 4.6 * (2 % 5));
        validation.put("8.9 * 2.2 / (-3) - 1", 8.9 * 2.2 / (-3d) - 1);
        validation.put("-7 + 4.5 * (6 / 3)", -7 + 4.5 * (6 / 3d));
        validation.put("15 / 3.5 + (-2) * 9", 15 / 3.5 + (-2) * 9);
        validation.put("(5.4 - 1.8) * 3 % 7", (5.4 - 1.8) * 3 % 7);
        validation.put("8.0 * (-2.5) / 5 + 4", 8.0 * (-2.5) / 5d + 4);
        validation.put("(14.2 / (-7.1) + 18.5) * 3.2", (14.2 / (-7.1) + 18.5) * 3.2);
        validation.put("-5.7 * (6.9 % 3.3 - 1.8) + 15", -5.7 * (6.9 % 3.3 - 1.8) + 15);
        validation.put("22.8 / (2.4 - 12.5 * 3) + 9", 22.8 / (2.4 - 12.5 * 3) + 9);
        validation.put("(-3.2 + 8.4) * 4.7 % 6 - 1.5", (-3.2 + 8.4) * 4.7 % 6 - 1.5);
        validation.put("17.6 * 3.5 / (-2.2) + 5.8 - 4", 17.6 * 3.5 / (-2.2) + 5.8 - 4);
        validation.put("(9.5 - 4.2) / 7.3 * 2.1 + (-8)", (9.5 - 4.2) / 7.3 * 2.1 + (-8));
        validation.put("5.6 % 2.3 - 1.9 * (11 / 2.8) + 6", 5.6 % 2.3 - 1.9 * (11 / 2.8) + 6);
        validation.put("(-14.5 / 7.2 + 2.3) * 3.5 - 9", (-14.5 / 7.2 + 2.3) * 3.5 - 9);
        validation.put("8.8 * (5 - 2.7 / (-3.1)) + 10", 8.8 * (5 - 2.7 / (-3.1)) + 10);
        validation.put("4.1 / (----2.5) * (1.6 - 8.7 % 3) + 6", 4.1 / 2.5 * (1.6 - 8.7 % 3) + 6);
        validation.put("----------------1", 1);

        for (Map.Entry<String, Object> entry : validation.entrySet()) {
            System.out.print(entry.getKey());
            System.out.printf("  =  %s -> %s%n", calculator3(entry.getKey()), entry.getValue());
        }
    }

    public static double calculator1(String expr) {
        /*数字栈*/
        Stack<Double> number = new Stack<>();
        /*符号栈*/
        Stack<String> operator = new Stack<>();
        operator.push("?");// 在栈顶压人一个null，配合它的优先级，目的是减少下面程序的判断

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
                        number.push(operator(a2, a1, b.charAt(0)));
                    }
                } else {//遇到运算符，满足该运算符的优先级大于栈顶元素的优先级压栈；否则计算后压栈
                    while (getPriority(temp.charAt(0)) <= getPriority(operator.peek().charAt(0))) {
                        double a1 = number.pop();
                        double a2 = number.pop();
                        number.push(operator(a2, a1, operator.pop().charAt(0)));
                    }
                    operator.push(temp);
                }
            } else {//遇到数字，直接压入数字栈
                number.push(Double.valueOf(temp));
            }
        }

        while (!operator.peek().equals("?")) {//遍历结束后，符号栈数字栈依次弹栈计算，并将结果压入数字栈
            double a1 = number.pop();
            double a2 = number.pop();
            number.push(operator(a2, a1, operator.pop().charAt(0)));
        }
        return number.pop();
    }

    public static double calculator3(String expression) {
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
        throw new IllegalStateException("illegal operator!");
    }

    private static int getPriority(char c) {
        switch (c) {
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
        throw new IllegalStateException("illegal operator!");
    }

    public static String time1(String key) {
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

    public static String time2(String key) {
        if (StringUtils.isNumeric(key)) {
            return SXItem.getSdf().get().format(System.currentTimeMillis() + Long.parseLong(key) * 1000);
        }
        Calendar calendar = Calendar.getInstance();
        int num = 0;
        for (char c : key.toCharArray()) {
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
                    num = c - 48 + num * 10;
                    continue;
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
                default:
                    continue;
            }
            num = 0;
        }
        return SXItem.getSdf().get().format(calendar.getTime());
    }

    public static String double1(String key) {
        String[] strSplit = key.split("_");
        if (strSplit.length == 1) return key;
        double[] doubles = {Double.parseDouble(strSplit[0]), Double.parseDouble(strSplit[1])};
        return Test.df.format(SXItem.getRandom().nextDouble() * (doubles[1] - doubles[0]) + doubles[0]);
    }

    public static String double3(String key) {
        int index = key.indexOf('_');
        if (index == -1) return key;
        double min = Double.parseDouble(key.substring(0, index));
        double max = Double.parseDouble(key.substring(index + 1));
        double result = SXItem.getRandom().nextFloat() * (max - min) + min;
        return String.valueOf(Math.round(result * 100) / 100D);
    }

    public static String int1(String key) {
        int[] ints = Arrays.stream(key.split("_")).mapToInt(Integer::parseInt).sorted().toArray();
        return String.valueOf(SXItem.getRandom().nextInt(1 + ints[1] - ints[0]) + ints[0]);
    }

    public static String int2(String key) {
        val index = key.indexOf('_');
        int min = Integer.parseInt(key.substring(0, index));
        int max = Integer.parseInt(key.substring(index + 1));
        return String.valueOf(SXItem.getRandom().nextInt(1 + Math.abs(max - min)) + Math.min(max, min));
    }

    private static String randomArray(String[] array) {
        return array[SXItem.getRandom().nextInt(array.length)];
    }

    public static String lock1(String key, RandomDocker docker) {
        String value;
        if (key.contains("#")) {
            String[] temp = key.substring(key.indexOf("#") + 1).split(":");
            String finalKey = key = key.substring(0, key.indexOf("#"));
            String tempValue = docker.getOtherMap().get(finalKey);
            value = PlaceholderHelper.setPlaceholders(docker.getPlayer(), docker.replace(tempValue != null ? tempValue : temp[SXItem.getRandom().nextInt(temp.length)]));
        } else {
            value = PlaceholderHelper.setPlaceholders(docker.getPlayer(), docker.replace(docker.random(key)));
        }
        return docker.getLockMap() == null ? value : docker.getLockMap().computeIfAbsent(key, k -> value);
    }

    public static String lock2(String key, RandomDocker docker) {
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
            value = docker.getOtherMap().get(key);
            if (value == null) {
                value = randomArray(temp.split(":"));
            }
        } else {
            value = docker.replace(docker.random(key));
        }

        if (docker.getLockMap() != null) {
            docker.getLockMap().put(key, value);
        }
        return value;
    }

    @State(Scope.Thread)
    public static class LockBM {
        @Param({"KEY#100:200:300"})
        String key;
        RandomDocker docker = new RandomDocker();

        @Benchmark
        public void test1() {
            TestRandom.lock1(key, docker);
        }

        @Benchmark
        public void test2() {
            TestRandom.lock2(key, docker);
        }
    }

    @State(Scope.Thread)
    public static class IntBM {
        @Param({"10_20"})
        String key;

        @Benchmark
        public void test1() {
            TestRandom.int1(key);
        }

        @Benchmark
        public void test3() {
            TestRandom.int2(key);
        }
    }

    @State(Scope.Thread)
    public static class DoubleBM {
        @Param({"1.5_15.5"})
        String key;

        @Benchmark
        public void test1() {
            TestRandom.double1(key);
        }

        @Benchmark
        public void test3() {
            TestRandom.double3(key);
        }
    }

    @State(Scope.Thread)
    public static class TimeBM {
        @Param({"20Y12M31D23h59m59s"})
        String key;

        @Benchmark
        public void test1() {
            TestRandom.time1(key);
        }

        @Benchmark
        public void test3() {
            TestRandom.time2(key);
        }
    }

    @State(value = Scope.Thread)
    public static class CalculatorBM {
        @Param({"-3 + ((4 * (10 - (6 / 2))) - (8 % 3) + (5 + (-7) / 2))"})
        String key;

        @Benchmark
        public void test1() {
            TestRandom.calculator1(key);
        }

        @Benchmark
        public void test3() {
            TestRandom.calculator3(key);
        }
    }

    @State(value = Scope.Thread)
    public static class BooleanBM {
        @Param({"AA:AAA:BB:CC:DD:EE:FF:GG:AA:HH:YY:GG:KK"})
        String key;

        @Benchmark
        public void test1() {
            TestRandom.boolean1(key);
        }

        @Benchmark
        public void test3() {
            TestRandom.boolean3(key);
        }
    }

    @State(value = Scope.Thread)
    public static class ScriptBM {
        @Param({"File.function#AAA,BBB,CCC"})
        String key;

        @Benchmark
        public void test1() {
            TestRandom.scriptRegex1(key);
        }

        @Benchmark
        public void test2() {
            TestRandom.scriptRegex2(key);
        }
    }
}
