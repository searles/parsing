package at.searles.parsing.combinators;

import at.searles.parsing.*;
import at.searles.parsing.printing.PartialConcreteSyntaxTree;
import org.jetbrains.annotations.NotNull;

/**
 * Reducer followed by a reducer
 */
public class ReducerThenReducer<T, U, V> implements Reducer<T, V>, Recognizable.Then {

    private final Reducer<T, U> left;
    private final Reducer<U, V> right;

    public ReducerThenReducer(Reducer<T, U> left, Reducer<U, V> right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public V parse(ParserCallBack env, ParserStream stream, @NotNull T left) {
        long offset = stream.offset();
        long preStart = stream.start();
        long preEnd = stream.end();

        U u = this.left.parse(env, stream, left);

        assert stream.start() == preStart;

        if (u == null) {
            return null;
        }

        V v = this.right.parse(env, stream, u);

        assert stream.start() == preStart;

        if (v == null) {
            env.notifyNoMatch(stream, this);
            stream.setOffset(offset);
            stream.setEnd(preEnd);
            return null;
        }

        return v;
    }


    @Override
    public boolean recognize(ParserCallBack env, ParserStream stream) {
        long preStart = stream.start();

        boolean status = Recognizable.Then.super.recognize(env, stream);

        if (status) {
            stream.setStart(preStart);
        }

        return status;
    }

    @Override
    public PartialConcreteSyntaxTree<T> print(PrinterCallBack env, @NotNull V v) {
        PartialConcreteSyntaxTree<U> midTree = right.print(env, v);

        if (midTree == null) {
            return null;
        }

        PartialConcreteSyntaxTree<T> leftTree = left.print(env, midTree.left);

        if (leftTree == null) {
            env.notifyLeftPrintFailed(midTree.right, this);
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
