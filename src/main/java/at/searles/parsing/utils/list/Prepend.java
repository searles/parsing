package at.searles.parsing.utils.list;

import at.searles.parsing.Environment;
import at.searles.parsing.Fold;
import at.searles.parsing.ParserStream;
import at.searles.parsing.utils.ImmutableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Fold to prepend elements to a list.
 * @param <T>
 */
public class Prepend<T> implements Fold<T, List<T>, List<T>> {

    @Override
    public List<T> apply(Environment env, T left, List<T> right, ParserStream stream) {
        ArrayList<T> list = new ArrayList<>(right.size() + 1);
        list.add(left);
        list.addAll(right);
        return list;
    }

    @Override
    public T leftInverse(Environment env, List<T> result) {
        if(result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }

    @Override
    public List<T> rightInverse(Environment env, List<T> result) {
        if(result.isEmpty()) {
            return null;
        }

        return result.subList(1, result.size());
    }

    @Override
    public String toString() {
        return "{prepend}";
    }
}
