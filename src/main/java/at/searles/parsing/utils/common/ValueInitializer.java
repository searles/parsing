package at.searles.parsing.utils.common;

import at.searles.parsing.ParserCallBack;
import at.searles.parsing.Initializer;
import at.searles.parsing.ParserStream;
import at.searles.parsing.PrinterCallBack;

/**
 * Created by searles on 02.04.19.
 */
public class ValueInitializer<V> implements Initializer<V> {
    private final V value;

    public ValueInitializer(V value) {
        this.value = value;
    }

    @Override
    public V parse(ParserCallBack env, ParserStream stream) {
        return value;
    }

    @Override
    public boolean consume(PrinterCallBack env, V v) {
        return v.equals(value);
    }

    @Override
    public String toString() {
        return String.format("{%s}", value);
    }
}
