package at.searles.parsing.utils.list;

import at.searles.parsing.Environment;
import at.searles.parsing.Fold;
import at.searles.parsing.ParserStream;

import java.util.Arrays;
import java.util.List;

/**
 * Created by searles on 31.03.19.
 */
public class BinaryList<T> implements Fold<T, T, List<T>> {
    @Override
    public List<T> apply(Environment env, T left, T right, ParserStream stream) {
        return Arrays.asList(left, right);
    }

    @Override
    public T leftInverse(Environment env, List<T> result) {
        return result.size() == 2 ? result.get(0) : null;
    }

    @Override
    public T rightInverse(Environment env, List<T> result) {
        return result.size() == 2 ? result.get(1) : null;
    }

    @Override
    public String toString() {
        return "{list(x,y)}";
    }
}
