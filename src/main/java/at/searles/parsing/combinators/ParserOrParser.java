package at.searles.parsing.combinators;

import at.searles.parsing.*;
import at.searles.parsing.printing.ConcreteSyntaxTree;

/**
 * Parser for options. The order is important. First one to
 * succeed is the one that is used.
 */
public class ParserOrParser<T> implements Parser<T>, Recognizable.Or {

    private final Parser<T> p1;
    private final Parser<T> p2;
    private final boolean swapOnInvert;

    public ParserOrParser(Parser<T> p1, Parser<T> p2, boolean swapOnInvert) {
        this.p1 = p1;
        this.p2 = p2;
        this.swapOnInvert = swapOnInvert;
    }

    @Override
    public T parse(ParserStream stream) {
        T ret = p1.parse(stream);

        return ret != null ? ret : p2.parse(stream);
    }

    @Override
    public ConcreteSyntaxTree print(T t) {
        Parser<T> first = swapOnInvert ? p2 : p1;
        Parser<T> second = swapOnInvert ? p1 : p2;

        ConcreteSyntaxTree output = first.print(t);

        return output != null ? output : second.print(t);
    }

    @Override
    public Recognizable first() {
        return p1;
    }

    @Override
    public Recognizable second() {
        return p2;
    }

    @Override
    public String toString() {
        return createString();
    }
}
