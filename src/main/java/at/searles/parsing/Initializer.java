package at.searles.parsing;

import at.searles.parsing.printing.ConcreteSyntaxTree;

public interface Initializer<T> extends Parser<T> {

    @Override
    T parse(ParserCallBack env, ParserStream stream);

    default boolean consume(PrinterCallBack env, T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    default ConcreteSyntaxTree print(PrinterCallBack env, T t) {
        return consume(env, t) ? ConcreteSyntaxTree.empty() : null;
    }

    @Override
    default boolean recognize(ParserCallBack env, ParserStream stream) {
        return true;
    }
}