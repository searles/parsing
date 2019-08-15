package at.searles.parsing.utils.list;

import at.searles.parsing.Initializer;
import at.searles.parsing.ParserStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Initializer that introduces an empty list
 */
public class EmptyList<T> implements Initializer<List<T>> {

    @Override
    public List<T> parse(ParserStream stream) {
        return new ArrayList<>();
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
