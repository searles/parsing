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
    public boolean recognize(ParserStream stream) {
        return ref.recognize(stream);
    }

    @Override
    public T parse(ParserStream stream) {
        return ref.parse(stream);
    }

    @Override
    public ConcreteSyntaxTree print(T t) {
        return ref.print(t);
    }

    @Override
    public String toString() {
        return label;
    }
}
