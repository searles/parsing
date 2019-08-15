package at.searles.parsing;

import at.searles.parsing.printing.ConcreteSyntaxTree;

public interface Initializer<T> extends Parser<T> {

    @Override
    T parse(ParserStream stream);

    default boolean consume(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    default ConcreteSyntaxTree print(T t) {
        return consume(t) ? ConcreteSyntaxTree.empty() : null;
    }

    @Override
    default boolean recognize(ParserStream stream) {
        return true;
    }
}