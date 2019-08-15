package at.searles.parsing.combinators;

import at.searles.parsing.*;
import at.searles.parsing.printing.ConcreteSyntaxTree;

public class RecognizerThenParser<T> implements Parser<T>, Recognizable.Then {
    private final Recognizer parent;
    private final Parser<T> parser;
    private final boolean allowParserBacktrack;

    public RecognizerThenParser(Recognizer parent, Parser<T> parser, boolean allowParserBacktrack) {
        this.parent = parent;
        this.parser = parser;
        this.allowParserBacktrack = allowParserBacktrack;
    }

    @Override
    public T parse(ParserStream stream) {
        long offset = stream.offset();

        long preStart = stream.start();
        long preEnd = stream.end();

        if (!parent.recognize(stream)) {
            return null;
        }

        long start = stream.start();

        T t = parser.parse(stream);

        if (t == null) {
            throwIfNoBacktrack(stream);
            stream.setOffset(offset);
            stream.setStart(preStart);
            stream.setEnd(preEnd);
            return null;
        }

        stream.setStart(start);

        return t;
    }

    @Override
    public ConcreteSyntaxTree print(T t) {
        ConcreteSyntaxTree output = parser.print(t);

        // printTo in recognizer always succeeds.
        return output != null ? output.consLeft(parent.print()) : null;
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

    @Override
    public boolean allowParserBacktrack() {
        return allowParserBacktrack;
    }
}
