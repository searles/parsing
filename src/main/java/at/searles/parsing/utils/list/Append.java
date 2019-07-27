package at.searles.parsing.utils.list;

import at.searles.parsing.Environment;
import at.searles.parsing.Fold;
import at.searles.parsing.ParserStream;
import at.searles.parsing.utils.ImmutableList;
import org.jetbrains.annotations.NotNull;

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
    public List<T> apply(Environment env, ParserStream stream, @NotNull List<T> left, @NotNull T right) {
        return ImmutableList.createFrom(left).pushBack(right);
    }

    private boolean cannotInvert(List<T> list) {
        return list.size() <= minSize;
    }

    @Override
    public List<T> leftInverse(Environment env, @NotNull List<T> result) {
        if(cannotInvert(result)) {
            return null;
        }

        return result.subList(0, result.size() - 1);
    }

    @Override
    public T rightInverse(Environment env, @NotNull List<T> result) {
        if(cannotInvert(result)) {
            return null;
        }

        return result.get(result.size() - 1);
    }

    @Override
    public String toString() {
        return "{append}";
    }
}
