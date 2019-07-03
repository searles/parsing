package at.searles.parsing.utils.common;

import at.searles.parsing.Environment;
import at.searles.parsing.Fold;
import at.searles.parsing.ParserStream;
import at.searles.utils.Pair;

/**
 * Created by searles on 02.04.19.
 */
public class SwapPairFold<T, U> implements Fold<T, U, Pair<U, T>> {
    @Override
    public Pair<U, T> apply(Environment env, T left, U right, ParserStream stream) {
        return new Pair<>(right, left);
    }

    @Override
    public T leftInverse(Environment env, Pair<U, T> pair) {
        return pair.r();
    }

    @Override
    public U rightInverse(Environment env, Pair<U, T> pair) {
        return pair.l();
    }

    @Override
    public String toString() {
        return "{<y,x>}";
    }
}
