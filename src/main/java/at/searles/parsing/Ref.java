package at.searles.parsing;

import at.searles.parsing.printing.ConcreteSyntaxTree;

public class Ref<T> implements Parser<T> {

    private final String label;
    private Parser<T> ref;

    public Ref(String label) {
        this.label = label;
    }

    public Ref<T> set(Parser<T> ref) {
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
    public ConcreteSyntaxTree print(Environment env, T t) {
        return ref.print(env, t);
    }

    @Override
    public String toString() {
        return label;
    }
}
