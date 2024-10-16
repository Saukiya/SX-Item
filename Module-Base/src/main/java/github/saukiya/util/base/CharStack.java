package github.saukiya.util.base;

import java.util.EmptyStackException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CharStack {

    char[] stack = new char[8];
    int top = -1;

    public void push(char value) {
        if (++top == stack.length) resize();
        stack[top] = value;
    }

    public char pop() {
        if (top == -1) throw new EmptyStackException();
        return stack[top--];
    }

    public char peek() {
        if (top == -1) throw new EmptyStackException();
        return stack[top];
    }

    public int size() {
        return top + 1;
    }

    private void resize() {
        char[] newStack = new char[stack.length * 2];
        System.arraycopy(stack, 0, newStack, 0, stack.length);
        stack = newStack;
    }

    @Override
    public String toString() {
        return "[" + IntStream.range(0, size()).mapToObj(i -> String.valueOf(stack[i])).collect(Collectors.joining(", ")) + "]";
    }
}
