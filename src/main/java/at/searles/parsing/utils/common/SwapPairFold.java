package at.searles.parsing.utils.common;

import at.searles.parsing.ParserCallBack;
import at.searles.parsing.Fold;
import at.searles.parsing.ParserStream;
import at.searles.parsing.PrinterCallBack;
import at.searles.utils.Pair;
import org.jetbrains.annotations.NotNull;

public class SwapPairFold<T, U> implements Fold<T, U, Pair<U, T>> {
    @Override
    public Pair<U, T> apply(ParserCallBack env, ParserStream stream, @NotNull T left, @NotNull U right) {
        return new Pair<>(right, left);
    }

    @Override
    public T leftInverse(PrinterCallBack env, @NotNull Pair<U, T> pair) {
        return pair.r();
    }

    @Override
    public U rightInverse(PrinterCallBack env, @NotNull Pair<U, T> pair) {
        return pair.l();
    }

    @Override
    public String toString() {
        return "{<y,x>}";
    }
}
