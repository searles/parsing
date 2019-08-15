package at.searles.parsing.combinators;

import at.searles.parsing.ParserStream;
import at.searles.parsing.Reducer;
import at.searles.parsing.printing.PartialConcreteSyntaxTree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReducerPlus<T> implements Reducer<T, T> {

    private final Reducer<T, T> reducer;
    private final int minCount;

    private final Reducer<T, T> parser;
    private final Reducer<T, T> printer;

    public ReducerPlus(Reducer<T, T> reducer, int minCount) {
        if (minCount < 1) {
            throw new IllegalArgumentException("minCount must be >= 1");
        }

        this.reducer = reducer;
        this.minCount = minCount;

        Reducer<T, T> sequence = reducer;

        for (int i = 1; i < minCount; ++i) {
            sequence = sequence.then(reducer);
        }

        parser = sequence.then(Reducer.rep(reducer));
        printer = Reducer.rep(reducer).then(sequence);
    }

    @Nullable
    @Override
    public T parse(ParserStream stream, @NotNull T left) {
        return parser.parse(stream, left);
    }

    @Nullable
    @Override
    public PartialConcreteSyntaxTree<T> print(@NotNull T t) {
        return printer.print(t);
    }

    @Override
    public boolean recognize(ParserStream stream) {
        return parser.recognize(stream);
    }

    public String toString() {
        return reducer + (minCount == 1 ? "+" : "{" + minCount + "}");
    }
}
