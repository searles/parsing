package at.searles.parsing.combinators;

import at.searles.parsing.*;
import at.searles.parsing.printing.ConcreteSyntaxTree;
import at.searles.parsing.printing.PartialConcreteSyntaxTree;
import org.jetbrains.annotations.NotNull;

public class RecognizerThenReducer<T, U> implements Reducer<T, U>, Recognizable.Then {
    private final Recognizer parent;
    private final Reducer<T, U> reducer;

    public RecognizerThenReducer(Recognizer parent, Reducer<T, U> reducer) {
        this.parent = parent;
        this.reducer = reducer;
    }

    @Override
    public U parse(ParserCallBack env, ParserStream stream, @NotNull T left) {
        long offset = stream.offset();
        long preStart = stream.start();
        long preEnd = stream.end();

        if (!parent.recognize(env, stream)) {
            return null;
        }

        stream.setStart(preStart);

        U u = reducer.parse(env, stream, left);

        if (u == null) {
            env.notifyNoMatch(stream, this);
            stream.setOffset(offset);
            stream.setEnd(preEnd);
            return null;
        }

        assert preStart == stream.start();

        return u;
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
    public Recognizable left() {
        return parent;
    }

    @Override
    public Recognizable right() {
        return reducer;
    }

    @Override
    public PartialConcreteSyntaxTree<T> print(PrinterCallBack env, @NotNull U u) {
        PartialConcreteSyntaxTree<T> output = reducer.print(env, u);

        if (output == null) {
            return null;
        }

        ConcreteSyntaxTree leftOutput = parent.print(env);

        return new PartialConcreteSyntaxTree<>(output.left, output.right.consLeft(leftOutput));
    }

    @Override
    public String toString() {
        return createString();
    }
}
