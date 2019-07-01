package at.searles.parsing.combinators;

import at.searles.parsing.*;
import at.searles.parsing.printing.PartialStringTree;
import at.searles.parsing.printing.StringTree;

public class ReducerOpt<T> implements Reducer<T, T>, Recognizable.Opt {
    private final Reducer<T, T> parent;

    public ReducerOpt(Reducer<T, T> parent) {
        this.parent = parent;
    }

    @Override
    public Recognizable parent() {
        return parent;
    }

    @Override
    public boolean recognize(Environment env, ParserStream stream) {
        return true;
    }

    @Override
    public T parse(Environment env, T left, ParserStream stream) {
        long preStart = stream.start();

        T nonOptResult = parent.parse(env, left, stream);

        assert stream.start() == preStart;

        return nonOptResult != null ? nonOptResult : left;
    }

    @Override
    public PartialStringTree<T> print(Environment env, T t) {
        PartialStringTree<T> output = parent.print(env, t);
        return output != null ? output : new PartialStringTree<>(t, StringTree.empty());
    }

    @Override
    public String toString() {
        return createString();
    }
}
