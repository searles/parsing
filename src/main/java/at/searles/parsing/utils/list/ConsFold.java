package at.searles.parsing.utils.list;

import at.searles.parsing.Environment;
import at.searles.parsing.Fold;
import at.searles.parsing.ParserStream;

import java.util.List;

/**
 * Fold to append elements to a list.
 * @param <T>
 */
public class ConsFold<T> implements Fold<List<T>, T, List<T>> {

    private final boolean mayBeEmpty; // for inversion. If false, left may not be empty.

    public ConsFold(boolean mayBeEmpty) {
        this.mayBeEmpty = mayBeEmpty;
    }

    @Override
    public List<T> apply(Environment env, List<T> left, T right, ParserStream stream) {
        left.add(right);
        return left;
    }

    private boolean canInvert(List<T> list) {
        return list.size() - 1 >= (mayBeEmpty ? 0 : 1);
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
