package at.searles.parsing;

import at.searles.parsing.combinators.*;
import at.searles.parsing.printing.PartialConcreteSyntaxTree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Reducer<T, U> extends Recognizable {

    /**
     * Parses elements from TokStream
     * @param env The env used to communicate parsing errors.
     * @param left The element left of this reducer
     * @param stream The stream from which elements are read
     * @return The parsed element, null if parsing was not successful.
     */
    @Nullable U parse(Environment env, @NotNull T left, ParserStream stream); // null = fail.

    // Since parse requires two arguments apart from the env,
    // the printer is split into two methods

    /**
     * Prints the argument that is split of u on its right. It is the
     * counterpart of the left method. This method always succeeds if left succeeds.
     * Otherwise, it will trigger an error via env.
     * @param env The env
     * @param u The argument
     * @return null if fail
     */
    @Nullable
    PartialConcreteSyntaxTree<T> print(Environment env, @NotNull U u);

    default <V> Reducer<T, V> then(Reducer<U, V> reducer) {
        return new ReducerThenReducer<>(this, reducer);
    }

    default Reducer<T, U> then(Recognizer recognizer) {
        return new ReducerThenRecognizer<>(this, recognizer);
    }

    default Reducer<T, U> or(Reducer<T, U> alternative) {
        return or(alternative, false);
    }

    default Reducer<T, U> or(Reducer<T, U> alternative, boolean swapOnInvert) {
        return new ReducerOrReducer<>(this, alternative, swapOnInvert);
    }

    static <T> Reducer<T, T> opt(Reducer<T, T> reducer) {
        return new ReducerOpt<>(reducer);
    }

    static <T> Reducer<T, T> rep(Reducer<T, T> reducer) {
        return new ReducerRep<>(reducer);
    }

    static <T> Reducer<T, T> plus(Reducer<T, T> reducer) {
        return new ReducerPlus<>(reducer, 1);
    }
}
