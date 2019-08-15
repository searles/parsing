package at.searles.parsing.combinators;

import at.searles.parsing.*;
import at.searles.parsing.printing.ConcreteSyntaxTree;
import at.searles.parsing.printing.PartialConcreteSyntaxTree;
import org.jetbrains.annotations.NotNull;

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
    public boolean recognize(ParserStream stream) {
        return true;
    }

    @Override
    public T parse(ParserStream stream, @NotNull T left) {
        long preStart = stream.start();

        T nonOptResult = parent.parse(stream, left);

        assert stream.start() == preStart;

        return nonOptResult != null ? nonOptResult : left;
    }

    @Override
    public PartialConcreteSyntaxTree<T> print(@NotNull T t) {
        PartialConcreteSyntaxTree<T> output = parent.print(t);
        return output != null ? output : new PartialConcreteSyntaxTree<>(t, ConcreteSyntaxTree.empty());
    }

    @Override
    public String toString() {
        return createString();
    }
}
