package at.searles.parsing;

public interface Consumer<T> {
	boolean consume(Environment env, T t, ParserStream stream);

    default T inverse(Environment env) {
        throw new UnsupportedOperationException();
    }
}