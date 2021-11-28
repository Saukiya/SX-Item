package github.saukiya.sxitem.util;

import github.saukiya.sxitem.nms.TagBase;

import java.io.DataOutput;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class NbtDataOut implements DataOutput {
    Deque<Object> stack = new ArrayDeque<>();

    @Override
    public void write(int value) {
        stack.push(value);
    }

    @Override
    public void write(byte[] value) {
        stack.push(value);
    }

    @Override
    public void write(byte[] value, int off, int len) {
        stack.push(Arrays.copyOfRange(value, off, len));
    }

    @Override
    public void writeBoolean(boolean value) {
        stack.push(value);
    }

    @Override
    public void writeByte(int value) {
        stack.push(value);
    }

    @Override
    public void writeShort(int value) {
        stack.push(value);
    }

    @Override
    public void writeChar(int value) {
        stack.push(value);
    }

    @Override
    public void writeInt(int value) {
        stack.push(value);
    }

    @Override
    public void writeLong(long value) {
        stack.push(value);
    }

    @Override
    public void writeFloat(float value) {
        stack.push(value);
    }

    @Override
    public void writeDouble(double value) {
        stack.push(value);
    }

    @Override
    public void writeBytes(String value) {
        stack.push(value);
    }

    @Override
    public void writeChars(String value) {
        stack.push(value);
    }

    @Override
    public void writeUTF(String value) {
        stack.push(value);
    }

    public TagBase read() {
        if (stack.size() == 0) return null;
        return TagBase.parse(stack.pollLast());
    }
}
