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
        long offset = stream.offset();

        long preStart = stream.start();
        long preEnd = stream.end();

        U result = this.left.parse(stream, left);

        assert stream.start() == preStart;

        if (result == null) {
            return null;
        }

        if (!right.recognize(stream)) {
            throwIfNoBacktrack(stream);

            stream.setOffset(offset);
            assert stream.start() == preStart;
            stream.setEnd(preEnd);

            return null;
        }

        stream.setStart(preStart);

        return result;
    }


    @Override
    public boolean recognize(ParserStream stream) {
        long preStart = stream.start();

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
