package at.searles.parsing.combinators;

import at.searles.parsing.*;
import at.searles.parsing.printing.ConcreteSyntaxTree;
import at.searles.parsing.printing.PartialConcreteSyntaxTree;
import org.jetbrains.annotations.NotNull;

public class RecognizerThenReducer<T, U> implements Reducer<T, U>, Recognizable.Then {
    private final Recognizer parent;
    private final Reducer<T, U> reducer;
    private final boolean allowParserBacktrack;

    public RecognizerThenReducer(Recognizer parent, Reducer<T, U> reducer, boolean allowParserBacktrack) {
        this.parent = parent;
        this.reducer = reducer;
        this.allowParserBacktrack = allowParserBacktrack;
    }

    @Override
    public U parse(ParserStream stream, @NotNull T left) {
        long offset = stream.getOffset();
        long preStart = stream.getStart();
        long preEnd = stream.getEnd();

        if (!parent.recognize(stream)) {
            return null;
        }

        stream.setStart(preStart);

        U u = reducer.parse(stream, left);

        if (u == null) {
            if(stream.getOffset() != offset) {
                throwIfNoBacktrack(stream);
                stream.backtrackToOffset(offset);
            }

            stream.setEnd(preEnd);
            return null;
        }

        assert preStart == stream.getStart();

        return u;
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
    public Recognizable left() {
        return parent;
    }

    @Override
    public Recognizable right() {
        return reducer;
    }

    @Override
    public PartialConcreteSyntaxTree<T> print(@NotNull U u) {
        PartialConcreteSyntaxTree<T> output = reducer.print(u);

        if (output == null) {
            return null;
        }

        ConcreteSyntaxTree leftOutput = parent.print();

        return new PartialConcreteSyntaxTree<>(output.left, output.right.consLeft(leftOutput));
    }

    @Override
    public String toString() {
        return createString();
    }
}
