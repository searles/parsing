package at.searles.parsing;

public interface Consumer<T> {
    boolean consume(ParserStream stream, T t);

    default T inverse() {
        return null;
    }
}