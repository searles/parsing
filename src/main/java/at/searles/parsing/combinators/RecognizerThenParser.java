package at.searles.parsing.combinators;

import at.searles.parsing.*;
import at.searles.parsing.printing.ConcreteSyntaxTree;

public class RecognizerThenParser<T> implements Parser<T>, Recognizable.Then {
    private final Recognizer parent;
    private final Parser<T> parser;

    public RecognizerThenParser(Recognizer parent, Parser<T> parser) {
        this.parent = parent;
        this.parser = parser;
    }

    @Override
    public T parse(Environment env, ParserStream stream) {
        long offset = stream.offset();

        long preStart = stream.start();
        long preEnd = stream.end();

        if(!parent.recognize(env, stream)) {
            return null;
        }

        long start = stream.start();

        T t = parser.parse(env, stream);

        if(t == null) {
            env.notifyNoMatch(stream, this);
            stream.setOffset(offset);
            stream.setStart(preStart);
            stream.setEnd(preEnd);
            return null;
        }

        stream.setStart(start);

        return t;
    }

    @Override
    public ConcreteSyntaxTree print(Environment env, T t) {
        ConcreteSyntaxTree output = parser.print(env, t);

        // printTo in recognizer always succeeds.
        return output != null ? output.consLeft(parent.print(env)) : null;
    }

    @Override
    public String toString() {
        return createString();
    }

    @Override
    public Recognizable left() {
        return parent;
    }

    @Override
    public Recognizable right() {
        return parser;
    }
}
