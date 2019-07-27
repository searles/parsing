package at.searles.lexer.utils;

import java.util.Iterator;

public class Counter implements Iterator<Integer> {
    private int i = 0;

    public int incr() {
        return i++;
    }

    public int get() {
        return i;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Integer next() {
        return incr();
    }
}
