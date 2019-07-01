package at.searles.parsing.utils.common;

import at.searles.parsing.Environment;
import at.searles.parsing.Fold;
import at.searles.parsing.ParserStream;
import at.searles.utils.Pair;

/**
 * Created by searles on 02.04.19.
 */
public class PairFold<T, U> implements Fold<T, U, Pair<T, U>> {
    @Override
    public Pair<T, U> apply(Environment env, T left, U right, ParserStream stream) {
        return new Pair<>(left, right);
    }

    @Override
    public T leftInverse(Environment env, Pair<T, U> pair) {
        return pair.l();
    }

    @Override
    public U rightInverse(Environment env, Pair<T, U> pair) {
        return pair.r();
    }

    @Override
    public String toString() {
        return "{<x,y>}";
    }
}
