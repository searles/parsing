package at.searles.parsing.combinators;

import at.searles.parsing.*;
import at.searles.parsing.printing.ConcreteSyntaxTree;
import at.searles.parsing.printing.PartialConcreteSyntaxTree;
import org.jetbrains.annotations.NotNull;

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

    public V parse(ParserStream stream, @NotNull T left) {
        // must preserve start position.
        long leftStart = stream.start();

        U right = parent.parse(stream);

        if (right == null) {
            return null;
        }

        stream.setStart(leftStart);

        return fold.apply(stream, left, right);
    }

    @Override
    public PartialConcreteSyntaxTree<T> print(@NotNull V v) {
        U right = fold.rightInverse(v);

        if (right == null) {
            return null;
        }

        T left = fold.leftInverse(v);

        if (left == null) {
            return null;
        }

        ConcreteSyntaxTree rightOutput = parent.print(right);

        if (rightOutput == null) {
            return null;
        }

        return new PartialConcreteSyntaxTree<>(left, rightOutput);
    }

    @Override
    public boolean recognize(ParserStream stream) {
        return parent.recognize(stream);
    }

    @Override
    public String toString() {
        return parent.toString() + " >> " + fold.toString();
    }
}
