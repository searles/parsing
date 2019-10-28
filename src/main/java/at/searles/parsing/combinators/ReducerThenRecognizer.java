package at.searles.parsing.combinators;

import at.searles.parsing.*;
import at.searles.parsing.printing.ConcreteSyntaxTree;
import at.searles.parsing.printing.PartialConcreteSyntaxTree;
import org.jetbrains.annotations.NotNull;

public class ReducerThenRecognizer<T, U> implements Reducer<T, U>, Recognizable.Then {
    private final Reducer<T, U> left;
    private final Recognizer right;
    private final boolean allowParserBacktrack;

    public ReducerThenRecognizer(Reducer<T, U> left, Recognizer right, boolean allowParserBacktrack) {
        this.left = left;
        this.right = right;
        this.allowParserBacktrack = allowParserBacktrack;
    }

    @Override
    public Recognizable left() {
        return left;
    }

    @Override
    public Recognizable right() {
        return right;
    }

    @Override
    public U parse(ParserStream stream, @NotNull T left) {
        long offset = stream.getOffset();

        long preStart = stream.getStart();
        long preEnd = stream.getEnd();

        U result = this.left.parse(stream, left);

        assert stream.getStart() == preStart;

        if (result == null) {
            return null;
        }

        if (!right.recognize(stream)) {
            if(stream.getOffset() != offset) {
                throwIfNoBacktrack(stream);
                stream.backtrackToOffset(offset);
            }

            assert stream.getStart() == preStart;
            stream.setEnd(preEnd);

            return null;
        }

        stream.setStart(preStart);

        return result;
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
    public PartialConcreteSyntaxTree<T> print(@NotNull U u) {
        PartialConcreteSyntaxTree<T> leftOutput = left.print(u);

        if (leftOutput == null) {
            return null;
        }

        ConcreteSyntaxTree rightOutput = right.print();

        return new PartialConcreteSyntaxTree<>(leftOutput.left, leftOutput.right.consRight(rightOutput));
    }

    @Override
    public String toString() {
        return createString();
    }
}
