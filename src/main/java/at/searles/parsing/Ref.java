package at.searles.parsing;

import at.searles.parsing.printing.StringTree;

public class Ref<T, L> implements Parser<T> {

    private final L label;
    private Parser<T> ref;

    public Ref(L label) {
        this.label = label;
    }

    public Ref<T, L> set(Parser<T> ref) {
        this.ref = ref;
        return this;
    }

    @Override
    public boolean recognize(Environment env, ParserStream stream) {
        return ref.recognize(env, stream);
    }

    @Override
    public T parse(Environment env, ParserStream stream) {
        return ref.parse(env, stream);
    }

    @Override
    public StringTree print(Environment env, T t) {
        return ref.print(env, t);
    }

    @Override
    public String toString() {
        return label.toString();
    }
}
