package at.searles.parsing.combinators;

import at.searles.parsing.*;
import at.searles.parsing.printing.PartialStringTree;
import org.jetbrains.annotations.NotNull;

/**
 * Reducer followed by a reducer
 */
public class ReducerThenReducer<T, U, V>  implements Reducer<T, V>, Recognizable.Then {

    private final Reducer<T, U> left;
    private final Reducer<U, V> right;

    public ReducerThenReducer(Reducer<T, U> left, Reducer<U, V> right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public V parse(Environment env, @NotNull T left, ParserStream stream) {
        long offset = stream.offset();
        long preStart = stream.start();
        long preEnd = stream.start();

        U u = this.left.parse(env, left, stream);

        assert stream.start() == preStart;

        if(u == null) {
            return null;
        }

        V v = this.right.parse(env, u, stream);

        assert stream.start() == preStart;

        if(v == null) {
            env.notifyNoMatch(stream, this);
            stream.setOffset(offset);
            stream.setEnd(preEnd);
            return null;
        }

        return v;
    }


    @Override
    public boolean recognize(Environment env, ParserStream stream) {
        long preStart = stream.start();

        boolean status = Recognizable.Then.super.recognize(env, stream);

        if(status) {
            stream.setStart(preStart);
        }

        return status;
    }

    @Override
    public PartialStringTree<T> print(Environment env, @NotNull V v) {
        PartialStringTree<U> midTree = right.print(env, v);

        if(midTree == null) {
            return null;
        }

        PartialStringTree<T> leftTree = left.print(env, midTree.left);

        if(leftTree == null) {
            env.notifyLeftPrintFailed(midTree.right, this);
            return null;
        }

        return new PartialStringTree<>(leftTree.left, leftTree.right.consRight(midTree.right));
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
