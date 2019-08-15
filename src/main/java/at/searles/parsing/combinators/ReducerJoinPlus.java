package at.searles.parsing.combinators;

import at.searles.parsing.*;
import at.searles.parsing.printing.PartialConcreteSyntaxTree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReducerJoinPlus<T> implements Reducer<T, T> {

    private final Reducer<T, T> parserReducer;
    private final Reducer<T, T> printerReducer;

    public ReducerJoinPlus(Recognizer separator, Reducer<T, T> reducer) {
        this.parserReducer = reducer.then(Reducer.rep(separator.then(reducer)));
        this.printerReducer = Reducer.rep(reducer.then(separator)).then(reducer);
    }

    @Nullable
    @Override
    public T parse(ParserCallBack env, ParserStream stream, @NotNull T left) {
        return parserReducer.parse(env, stream, left);
    }

    @Nullable
    @Override
    public PartialConcreteSyntaxTree<T> print(PrinterCallBack env, @NotNull T t) {
        return printerReducer.print(env, t);
    }

    @Override
    public boolean recognize(ParserCallBack env, ParserStream stream) {
        return parserReducer.recognize(env, stream);
    }

    @Override
    public String toString() {
        return String.format("join(%s)", parserReducer.toString());
    }
}
