package at.searles.parsing.utils.list;

import at.searles.parsing.Initializer;
import at.searles.parsing.ParserStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Initializer that introduces an empty list
 */
public class EmptyList<T> implements Initializer<List<T>> {
    private static class Holder {
        static List<?> instance = Collections.emptyList();
    }

    @Override
    public List<T> parse(ParserStream stream) {
        //noinspection unchecked
        return (List<T>) Holder.instance;
    }

    @Override
    public boolean consume(List<T> ts) {
        return ts.isEmpty();
    }

    @Override
    public String toString() {
        return "{emptylist}";
    }
}
