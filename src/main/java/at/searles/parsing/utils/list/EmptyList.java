package at.searles.parsing.utils.list;

import at.searles.parsing.Environment;
import at.searles.parsing.Initializer;
import at.searles.parsing.ParserStream;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by searles on 31.03.19.
 */
public class EmptyList<T> implements Initializer<List<T>> {

    @Override
    public List<T> parse(Environment env, ParserStream stream) {
        return new ArrayList<>();
    }

    @Override
    public boolean consume(Environment env, List<T> ts) {
        return ts.isEmpty();
    }

    @Override
    public String toString() {
        return "{emptylist}";
    }
}
