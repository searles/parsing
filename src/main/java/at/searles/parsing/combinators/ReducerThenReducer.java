package at.searles.parsing.combinators;

import at.searles.parsing.*;
import at.searles.parsing.printing.PartialConcreteSyntaxTree;
import at.searles.parsing.printing.PrinterBacktrackException;
import org.jetbrains.annotations.NotNull;

/**
 * Reducer followed by a reducer
 */
public class ReducerThenReducer<T, U, V> implements Reducer<T, V>, Recognizable.Then {

    private final Reducer<T, U> left;
    private final Reducer<U, V> right;
    private final boolean allowParserBacktrack;
    private final boolean allowPrinterBacktrack;

    public ReducerThenReducer(Reducer<T, U> left, Reducer<U, V> right, boolean allowParserBacktrack, boolean allowPrinterBacktrack) {
        this.left = left;
        this.right = right;
        this.allowParserBacktrack = allowParserBacktrack;
        this.allowPrinterBacktrack = allowPrinterBacktrack;
    }

    @Override
    public V parse(ParserStream stream, @NotNull T left) {
        long offset = stream.getOffset();
        long preStart = stream.getStart();
        long preEnd = stream.getEnd();

        U u = this.left.parse(stream, left);

        assert stream.getStart() == preStart;

        if (u == null) {
            return null;
        }

        V v = this.right.parse(stream, u);

        assert stream.getStart() == preStart;

        if (v == null) {
            throwIfNoBacktrack(stream);
            stream.backtrackToOffset(offset);
            stream.setEnd(preEnd);
            return null;
        }

        return v;
    }


    @Override
    public boolean recognize(ParserStream stream) {
        long preStart = stream.getStart();

        boolean status = Recognizable.Then.super.recognize(stream);

        if (status) {
            stream.setStart(preStart);
        }

        return status;
    }

    @Override
    public boolean allowParserBacktrack() {
        return allowParserBacktrack;
    }

    @Override
    public PartialConcreteSyntaxTree<T> print(@NotNull V v) {
        PartialConcreteSyntaxTree<U> midTree = right.print(v);

        if (midTree == null) {
            return null;
        }

        PartialConcreteSyntaxTree<T> leftTree = left.print(midTree.left);

        if (leftTree == null) {
            if(!allowPrinterBacktrack) {
                throw new PrinterBacktrackException(this, midTree.right);
            }

            return null;
        }

        return new PartialConcreteSyntaxTree<>(leftTree.left, leftTree.right.consRight(midTree.right));
    }

    @Override
    public String toString() {
        return createString();
    }

    @Override
    public Recognizable left() {
        return left;
    }

    @Override
    public Recognizable right() {
        return right;
    }
}
