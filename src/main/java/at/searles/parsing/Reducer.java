package at.searles.parsing;

import at.searles.parsing.combinators.*;
import at.searles.parsing.printing.PartialConcreteSyntaxTree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Reducer<T, U> extends Recognizable {

    static <T> Reducer<T, T> opt(Reducer<T, T> reducer) {
        return new ReducerOpt<>(reducer);
    }

    // Since parse requires two arguments apart from the env,
    // the printer is split into two methods

    static <T> Reducer<T, T> rep(Reducer<T, T> reducer) {
        return new ReducerRep<>(reducer);
    }

    static <T> Reducer<T, T> plus(Reducer<T, T> reducer) {
        return new ReducerPlus<>(reducer, 1);
    }

    /**
     * Parses elements from TokStream
     *
     * @param stream The stream from which elements are read
     * @param left   The element left of this reducer
     * @return The parsed element, null if parsing was not successful.
     */
    @Nullable
    U parse(ParserStream stream, @NotNull T left); // null = fail.

    /**
     * Prints the argument that is split of u on its right. It is the
     * counterpart of the left method. This method always succeeds if left succeeds.
     * Otherwise, it will trigger an error via env.
     *
     * @param u   The argument
     * @return null if fail
     */
    @Nullable
    PartialConcreteSyntaxTree<T> print(@NotNull U u);

    default <V> Reducer<T, V> then(Reducer<U, V> reducer) {
        return then(reducer, false);
    }

    default <V> Reducer<T, V> then(Reducer<U, V> reducer, boolean allowParserBacktrack) {
        return then(reducer, allowParserBacktrack, false);
    }

    default <V> Reducer<T, V> then(Reducer<U, V> reducer, boolean allowParserBacktrack, boolean allowPrinterBacktrack) {
        return new ReducerThenReducer<>(this, reducer, allowParserBacktrack, allowPrinterBacktrack);
    }

    default Reducer<T, U> then(Recognizer recognizer) {
        return then(recognizer, false);
    }

    default Reducer<T, U> then(Recognizer recognizer, boolean allowParserBacktrack) {
        return new ReducerThenRecognizer<>(this, recognizer, allowParserBacktrack);
    }

    default Reducer<T, U> or(Reducer<T, U> alternative) {
        return or(alternative, false);
    }

    default Reducer<T, U> or(Reducer<T, U> alternative, boolean swapOnInvert) {
        return new ReducerOrReducer<>(this, alternative, swapOnInvert);
    }
}
