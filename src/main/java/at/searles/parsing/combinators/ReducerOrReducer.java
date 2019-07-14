package at.searles.parsing.combinators;
import at.searles.parsing.*;
import at.searles.parsing.printing.PartialStringTree;
import org.jetbrains.annotations.NotNull;

public class ReducerOrReducer<T, U> implements Reducer<T, U>, Recognizable.Or {

    private final Reducer<T, U> r1;
    private final Reducer<T, U> r2;
    private final boolean swapOnInvert;

    public ReducerOrReducer(Reducer<T, U> r1, Reducer<T, U> r2, boolean swapOnInvert) {
        this.r1 = r1;
        this.r2 = r2;
        this.swapOnInvert = swapOnInvert;
    }

    @Override
    public U parse(Environment env, @NotNull T left, ParserStream stream) {
        long start = stream.start();
        long end = stream.end();

        U ret = r1.parse(env, left, stream);

        if(ret != null) {
            return ret;
        }

        assert (stream.start() == start && stream.end() == end);

        return r2.parse(env, left, stream);
    }

    @Override
    public PartialStringTree<T> print(Environment env, @NotNull U result) {
        Reducer<T, U> first = swapOnInvert ? r2 : r1;
        Reducer<T, U> second = swapOnInvert ? r1 : r2;

        PartialStringTree<T> firstOutput = first.print(env, result);

        return firstOutput != null ? firstOutput : second.print(env, result);
    }

    @Override
    public Recognizable first() {
        return r1;
    }

    @Override
    public Recognizable second() {
        return r2;
    }

    @Override
    public String toString() {
        return createString();
    }
}