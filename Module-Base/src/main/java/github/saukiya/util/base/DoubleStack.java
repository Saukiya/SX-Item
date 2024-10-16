package github.saukiya.util.base;

import java.util.EmptyStackException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DoubleStack {

    double[] stack;
    int top = -1;

    public DoubleStack() {
        this(8);
    }

    public DoubleStack(int size) {
        stack = new double[size];
    }

    public void push(double value) {
        if (++top == stack.length) resize();
        stack[top] = value;
    }

    public double pop() {
        if (top == -1) throw new EmptyStackException();
        return stack[top--];
    }

    public double peek() {
        if (top == -1) throw new EmptyStackException();
        return stack[top];
    }

    public int size() {
        return top + 1;
    }

    private void resize() {
        double[] newStack = new double[stack.length * 2];
        System.arraycopy(stack, 0, newStack, 0, stack.length);
        stack = newStack;
    }

    @Override
    public String toString() {
        return "[" + IntStream.range(0, size()).mapToObj(i -> Double.toString(stack[i])).collect(Collectors.joining(", ")) + "]";
    }
}
