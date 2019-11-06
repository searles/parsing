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
        try {
            return ref.recognize(stream);
        } catch(StackOverflowError e) {
            throw new PossiblyInfiniteRecursionException(this, e);
        }
    }

    @Override
    public T parse(ParserStream stream) {
        try {
            return ref.parse(stream);
        } catch(StackOverflowError e) {
            throw new PossiblyInfiniteRecursionException(this, e);
        }
    }

    @Override
    public ConcreteSyntaxTree print(T t) {
        try {
            return ref.print(t);
        } catch(StackOverflowError e) {
            throw new PossiblyInfiniteRecursionException(this, e);
        }
    }

    @Override
    public String toString() {
        return label;
    }
}
