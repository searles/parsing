package at.searles.parsing;

import at.searles.parsing.printing.ConcreteSyntaxTree;

public interface Initializer<T> extends Parser<T> {

    @Override
    T parse(Environment env, ParserStream stream);

    default boolean consume(Environment env, T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    default ConcreteSyntaxTree print(Environment env, T t) {
        return consume(env, t) ? ConcreteSyntaxTree.empty() : null;
    }

    @Override
    default boolean recognize(Environment env, ParserStream stream) {
        return true;
    }
}