package at.searles.parsing;

public interface Consumer<T> {
    boolean consume(ParserCallBack env, T t, ParserStream stream);

    default T inverse(ParserCallBack env) {
        throw new UnsupportedOperationException();
    }
}