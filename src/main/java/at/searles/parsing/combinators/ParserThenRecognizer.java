package at.searles.parsing.combinators;

import at.searles.parsing.*;
import at.searles.parsing.printing.ConcreteSyntaxTree;

public class ParserThenRecognizer<T> implements Parser<T>, Recognizer.Then {
    private final Parser<T> left;
    private final Recognizer right;
    private final boolean allowParserBacktrack;

    public ParserThenRecognizer(Parser<T> left, Recognizer right, boolean allowParserBacktrack) {
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
    public boolean allowParserBacktrack() {
        return allowParserBacktrack;
    }

    @Override
    public T parse(ParserStream stream) {
        long offset = stream.getOffset();

        // to restore if backtracking
        long preStart = stream.getStart();
        long preEnd = stream.getEnd();

        T result = left.parse(stream);

        if (result == null) {
            return null;
        }

        // The start position of left.
        long start = stream.getStart();

        if (!right.recognize(stream)) {
            if(stream.getOffset() != offset) {
                throwIfNoBacktrack(stream);
                stream.backtrackToOffset(offset);
            }

            stream.setStart(preStart);
            stream.setEnd(preEnd);
            return null;
        }

        stream.setStart(start);

        return result;
    }

    @Override
    public ConcreteSyntaxTree print(T t) {
        ConcreteSyntaxTree output = left.print(t);

        // Recognizer.printTo always succeeds.
        return output != null ? output.consRight(right.print()) : null;
    }

    @Override
    public String toString() {
        return createString();
    }
}
