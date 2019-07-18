package at.searles.parsing.combinators;

import at.searles.parsing.*;
import at.searles.parsing.printing.ConcreteSyntaxTree;

public class ParserThenRecognizer<T> implements Parser<T>, Recognizer.Then {
    private final Parser<T> left;
    private final Recognizer right;

    public ParserThenRecognizer(Parser<T> left, Recognizer right) {
        this.left = left;
        this.right = right;
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
    public T parse(Environment env, ParserStream stream) {
        long offset = stream.offset();

        // to restore if backtracking
        long preStart = stream.start();
        long preEnd = stream.end();

        T result = left.parse(env, stream);

        if(result == null) {
            return null;
        }

        // The start position of left.
        long start = stream.start();

        if(!right.recognize(env, stream)) {
            env.notifyNoMatch(stream, this);
            stream.setOffset(offset);
            stream.setStart(preStart);
            stream.setEnd(preEnd);
            return null;
        }

        stream.setStart(start);

        return result;
    }

    @Override
    public ConcreteSyntaxTree print(Environment env, T t) {
        ConcreteSyntaxTree output = left.print(env, t);

        // Recognizer.print always succeeds.
        return output != null ? output.consRight(right.print(env)) : null;
    }

    @Override
    public String toString() {
        return createString();
    }
}
