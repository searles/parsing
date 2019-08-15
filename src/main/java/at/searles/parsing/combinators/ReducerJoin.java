package at.searles.parsing.combinators;

import at.searles.parsing.*;
import at.searles.parsing.printing.PartialConcreteSyntaxTree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReducerJoin<T> implements Reducer<T, T> {

    private final Reducer<T, T> parserReducer;
    private final Reducer<T, T> printerReducer;

    public ReducerJoin(Recognizer separator, Reducer<T, T> reducer) {
        this.parserReducer = Reducer.opt(reducer.then(Reducer.rep(separator.then(reducer))));
        this.printerReducer = Reducer.opt(Reducer.rep(reducer.then(separator)).then(reducer));
    }

    @Nullable
    @Override
    public T parse(ParserStream stream, @NotNull T left) {
        return parserReducer.parse(stream, left);
    }

    @Nullable
    @Override
    public PartialConcreteSyntaxTree<T> print(@NotNull T t) {
        return printerReducer.print(t);
    }

    @Override
    public boolean recognize(ParserStream stream) {
        return parserReducer.recognize(stream);
    }

    @Override
    public String toString() {
        return String.format("join(%s)", parserReducer.toString());
    }
}
