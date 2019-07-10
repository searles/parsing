package at.searles.parsing.utils.list;

import at.searles.parsing.Environment;
import at.searles.parsing.Fold;
import at.searles.parsing.ParserStream;
import at.searles.parsing.utils.ImmutableList;

import java.util.List;

/**
 * Fold to append elements to a list.
 * @param <T>
 */
public class Append<T> implements Fold<List<T>, T, List<T>> {

    private final int minSize; // for inversion. If false, left may not be empty.

    public Append(int minSize) {
        this.minSize = minSize;
    }

    @Override
    public List<T> apply(Environment env, List<T> left, T right, ParserStream stream) {
        return ImmutableList.createFrom(left).pushBack(right);
    }

    private boolean canInvert(List<T> list) {
        return list.size() > minSize;
    }

    @Override
    public List<T> leftInverse(Environment env, List<T> result) {
        if(!canInvert(result)) {
            return null;
        }

        return result.subList(0, result.size() - 1);
    }

    @Override
    public T rightInverse(Environment env, List<T> result) {
        if(!canInvert(result)) {
            return null;
        }

        return result.get(result.size() - 1);
    }

    @Override
    public String toString() {
        return "{append}";
    }
}
