package at.searles.parsing.combinators;

import at.searles.parsing.*;
import at.searles.parsing.printing.PartialStringTree;
import at.searles.parsing.printing.StringTree;

/**
 * Creates a reducer out of a parser.
 */
public class ParserToReducer<T, U, V> implements Reducer<T, V> {

    private final Parser<U> parent;
    private final Fold<T, U, V> fold;

    public ParserToReducer(Parser<U> parent, Fold<T, U, V> fold) {
        this.parent = parent;
        this.fold = fold;
    }

    public V parse(Environment env, T left, ParserStream stream) {
        // must preserve start position.
        long leftStart = stream.start();

        U right = parent.parse(env, stream);

        if(right == null) {
            return null;
        }

        stream.setStart(leftStart);

        return fold.apply(env, left, right, stream);
    }

    @Override
    public PartialStringTree<T> print(Environment env, V v) {
        U right = fold.rightInverse(env, v);

        if(right == null) {
            return null;
        }

        T left = fold.leftInverse(env, v);

        if(left == null) {
            return null;
        }

        StringTree rightOutput = parent.print(env, right);

        if(rightOutput == null) {
            return null;
        }

        return new PartialStringTree<>(left, rightOutput);
    }

    @Override
    public boolean recognize(Environment env, ParserStream stream) {
        return parent.recognize(env, stream);
    }

    @Override
    public String toString() {
        return parent.toString() + "~" + fold.toString();
    }
}
